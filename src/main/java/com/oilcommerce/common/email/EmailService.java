package com.oilcommerce.common.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;
    private final Environment environment;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${resend.api.key:${app.email.resend-api-key:${RESEND_API_KEY:}}}")
    private String resendApiKey;

    @Value("${resend.from:${app.email.resend-from:${RESEND_FROM:onboarding@resend.dev}}}")
    private String resendFrom;

    @Value("${app.email.brevo-api-key:}")
    private String brevoApiKey;

    @Value("${app.email.brevo-from-email:}")
    private String brevoFromEmail;

    @Value("${app.email.brevo-from-name:Nisha Pure Oils}")
    private String brevoFromName;

    public record ResendResult(boolean success, int statusCode, String responseBody, String errorMessage) {}

    @PostConstruct
    public void verifyEmailConfiguration() {
        log.info("================================================================================");
        log.info("[EmailService Configuration Audit]");
        
        boolean keyLoaded = resendApiKey != null && !resendApiKey.trim().isBlank();
        if (keyLoaded) {
            String trimmedKey = resendApiKey.trim();
            String maskedKey = trimmedKey.length() > 8
                    ? trimmedKey.substring(0, 6) + "..." + trimmedKey.substring(trimmedKey.length() - 4)
                    : "***";
            log.info("✅ Resend API Key is loaded successfully (masked: {}, length: {})", maskedKey, trimmedKey.length());
            log.info("📧 Configured Resend Sender: {}", resolveSenderEmail());
            if (resolveSenderEmail().contains("onboarding@resend.dev")) {
                log.warn("⚠️ NOTICE: Default unverified domain 'onboarding@resend.dev' is in use.");
                log.warn("   Resend free tier testing policy allows sending ONLY to your Resend account email.");
                log.warn("   To send to arbitrary recipients, verify a custom domain in Resend & set RESEND_FROM.");
            }
        } else {
            log.error("❌ RESEND_API_KEY is NOT set or is empty!");
            boolean isProd = environment.acceptsProfiles(Profiles.of("prod"));
            if (isProd) {
                log.error("❌ Startup aborted: RESEND_API_KEY is required in production environment (Render)!");
                throw new IllegalStateException("CRITICAL STARTUP FAILURE: RESEND_API_KEY environment variable is missing in production! Please configure RESEND_API_KEY in Render environment variables.");
            } else {
                log.warn("⚠️ Non-prod environment: Startup will continue, but Resend email delivery will fail until RESEND_API_KEY is provided.");
            }
        }
        log.info("================================================================================");
    }

    /**
     * Validates an email address.
     */
    public boolean isValidEmail(String email) {
        return email != null && !email.trim().isBlank() && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Resolves the sender email. Defaults to onboarding@resend.dev if domain not verified or not configured.
     */
    public String resolveSenderEmail() {
        if (resendFrom != null && !resendFrom.trim().isBlank()) {
            return resendFrom.trim();
        }
        return "onboarding@resend.dev";
    }

    /**
     * Sends password reset email asynchronously with comprehensive logging and error handling.
     */
    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        log.info("[Password Reset Flow] Starting password reset email delivery for recipient: '{}'", toEmail);

        // 1. Validate recipient email
        if (toEmail == null || toEmail.trim().isBlank()) {
            log.error("[Password Reset Flow] Aborted: Recipient email is null or empty!");
            return;
        }

        String cleanEmail = toEmail.trim();
        if (!isValidEmail(cleanEmail)) {
            log.error("[Password Reset Flow] Aborted: Recipient email '{}' has invalid email format!", cleanEmail);
            return;
        }

        // 2. Validate token and construct reset URL
        if (token == null || token.trim().isBlank()) {
            log.error("[Password Reset Flow] Aborted: Password reset token is null or empty for recipient '{}'!", cleanEmail);
            return;
        }

        String resetLink = frontendUrl.replaceAll("/+$", "") + "/auth/reset-password?token=" + token.trim();

        // Prominently log reset link for dev & staging reference
        log.info("================================================================================");
        log.info("[Password Reset Flow] Recipient : {}", cleanEmail);
        log.info("[Password Reset Flow] Token     : {}", token.trim());
        log.info("[Password Reset Flow] Reset Link: {}", resetLink);
        log.info("================================================================================");

        String subject = "Reset Your Password — Nisha Pure Oils";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #92400e;">Nisha Pure Oils</h2>
                <p>Hello,</p>
                <p>We received a request to reset the password for your account.</p>
                <p style="margin: 24px 0;">
                    <a href="%s" style="background-color: #b45309; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; display: inline-block;">Reset Password</a>
                </p>
                <p>Or copy and paste this link into your browser:</p>
                <p style="word-break: break-all; color: #6b7280; font-size: 13px;">%s</p>
                <p style="color: #9ca3af; font-size: 12px; margin-top: 30px;">This link will expire in 1 hour. If you did not request this, please ignore this email.</p>
            </div>
            """.formatted(resetLink, resetLink);

        // 1. Primary: Resend HTTP API (Port 443 - works on Render Free/Starter tiers)
        if (resendApiKey != null && !resendApiKey.trim().isBlank()) {
            log.info("[Password Reset Flow] Attempting delivery via Resend HTTP API...");
            ResendResult result = sendViaResendDetailed(cleanEmail, subject, htmlContent);
            if (result.success()) {
                log.info("✅ [Password Reset Flow] Password reset email successfully delivered to {} via Resend! Response: {}", cleanEmail, result.responseBody());
                return;
            } else {
                log.error("❌ [Password Reset Flow] Resend delivery failed for {}. Status: {}, Response: {}, Error: {}",
                        cleanEmail, result.statusCode(), result.responseBody(), result.errorMessage());
            }
        } else {
            log.warn("[Password Reset Flow] RESEND_API_KEY is not configured, skipping Resend.");
        }

        // 2. Secondary fallback: Brevo HTTP API (Port 443)
        if (brevoApiKey != null && !brevoApiKey.trim().isBlank()) {
            log.info("[Password Reset Flow] Attempting delivery via Brevo HTTP API...");
            boolean sent = sendViaBrevo(cleanEmail, subject, htmlContent);
            if (sent) {
                log.info("✅ [Password Reset Flow] Password reset email sent to {} via Brevo.", cleanEmail);
                return;
            }
        }

        // 3. Tertiary fallback: SMTP (Port 587)
        if (fromEmail != null && !fromEmail.trim().isBlank()) {
            log.info("[Password Reset Flow] Attempting delivery via SMTP...");
            sendViaSmtp(cleanEmail, subject, htmlContent);
        } else {
            log.error("❌ [Password Reset Flow] No working email provider configured. Password reset email could not be delivered to {}!", cleanEmail);
        }
    }

    /**
     * Executes an email send directly via Resend HTTP API and returns diagnostic details.
     */
    public ResendResult sendViaResendDetailed(String toEmail, String subject, String htmlContent) {
        if (toEmail == null || toEmail.trim().isBlank()) {
            return new ResendResult(false, 400, "", "Recipient email cannot be null or empty");
        }

        String cleanEmail = toEmail.trim();
        if (!isValidEmail(cleanEmail)) {
            return new ResendResult(false, 400, "", "Invalid recipient email format: " + cleanEmail);
        }

        if (resendApiKey == null || resendApiKey.trim().isBlank()) {
            return new ResendResult(false, 500, "", "RESEND_API_KEY is not configured on the server");
        }

        String sender = resolveSenderEmail();
        log.info("[Resend] Preparing HTTP POST request to https://api.resend.com/emails");
        log.info("[Resend] From: '{}' -> To: '{}', Subject: '{}'", sender, cleanEmail, subject);

        try {
            String jsonPayload = objectMapper.writeValueAsString(Map.of(
                    "from", sender,
                    "to", List.of(cleanEmail),
                    "subject", subject,
                    "html", htmlContent
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            int statusCode = response.statusCode();
            String responseBody = response.body();

            log.info("[Resend] HTTP Status Code: {}", statusCode);
            log.info("[Resend] Response Body: {}", responseBody);

            if (statusCode >= 200 && statusCode < 300) {
                log.info("✅ [Resend] Email accepted by Resend for delivery to {}: {}", cleanEmail, responseBody);
                return new ResendResult(true, statusCode, responseBody, null);
            } else {
                String errorMsg = "Resend API returned HTTP " + statusCode + ": " + responseBody;
                if (statusCode == 403 && responseBody != null && responseBody.contains("validation_error")) {
                    log.error("❌ [Resend 403] Testing restriction: unverified 'onboarding@resend.dev' sender can ONLY send to your own registered Resend email address. To send to {}, you must verify your domain at https://resend.com/domains.", cleanEmail);
                } else if (statusCode == 401) {
                    log.error("❌ [Resend 401] Unauthorized! Verify that RESEND_API_KEY is valid and not revoked in Render environment variables.");
                }
                return new ResendResult(false, statusCode, responseBody, errorMsg);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String errorMsg = "Resend HTTP request interrupted for " + cleanEmail + ": " + e.getMessage();
            log.error("❌ [Resend Exception] {}", errorMsg, e);
            return new ResendResult(false, 500, "", errorMsg);
        } catch (java.io.IOException | RuntimeException e) {
            String errorMsg = "Failed to communicate with Resend API for " + cleanEmail + ": " + e.getMessage();
            log.error("❌ [Resend Exception] {}", errorMsg, e);
            return new ResendResult(false, 500, "", errorMsg);
        }
    }

    /**
     * Test email sender for diagnostic verification endpoint.
     */
    public ResendResult sendTestEmail(String toEmail) {
        String cleanEmail = toEmail != null ? toEmail.trim() : "";
        log.info("[Test Email] Received request to send diagnostic test email to '{}'", cleanEmail);

        String subject = "Resend Test Email — Nisha Pure Oils Backend Verification";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #92400e;">Nisha Pure Oils &bull; Resend Integration Test</h2>
                <p>Hello,</p>
                <p>This is an automated test email confirming that your Resend API email integration is <strong>working successfully</strong> on Render!</p>
                <div style="background: #fdf6b2; border-left: 4px solid #b45309; padding: 12px; margin: 16px 0;">
                    <p style="margin: 0; font-size: 13px; color: #723b13;">
                        <strong>Sender:</strong> %s<br/>
                        <strong>Recipient:</strong> %s<br/>
                        <strong>Timestamp:</strong> %s
                    </p>
                </div>
                <p style="color: #6b7280; font-size: 12px;">Oil Commerce Backend &mdash; Spring Boot 3 on Render</p>
            </div>
            """.formatted(resolveSenderEmail(), cleanEmail, java.time.Instant.now().toString());

        return sendViaResendDetailed(cleanEmail, subject, htmlContent);
    }

    private boolean sendViaBrevo(String toEmail, String subject, String htmlContent) {
        try {
            log.info("Sending email to {} via Brevo HTTP API (Port 443)...", toEmail);
            String senderEmail = (brevoFromEmail != null && !brevoFromEmail.isBlank()) ? brevoFromEmail : fromEmail;
            String jsonPayload = objectMapper.writeValueAsString(Map.of(
                    "sender", Map.of("name", brevoFromName, "email", senderEmail),
                    "to", List.of(Map.of("email", toEmail)),
                    "subject", subject,
                    "htmlContent", htmlContent
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("api-key", brevoApiKey.trim())
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Email successfully sent via Brevo to {}: {}", toEmail, response.body());
                return true;
            } else {
                log.error("Brevo API returned status {}: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Brevo email request interrupted for {}: {}", toEmail, e.getMessage(), e);
            return false;
        } catch (java.io.IOException | RuntimeException e) {
            log.error("Failed to send email via Brevo to {}: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }

    private void sendViaSmtp(String toEmail, String subject, String htmlContent) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("No HTTP email API key (RESEND_API_KEY/BREVO_API_KEY) and no SMTP username configured. Email not sent.");
            return;
        }

        try {
            log.info("Sending email to {} via SMTP...", toEmail);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Nisha Pure Oils");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Password reset email successfully sent via SMTP to {}", toEmail);
        } catch (jakarta.mail.MessagingException | java.io.UnsupportedEncodingException | RuntimeException e) {
            log.error("Failed to send password reset email via SMTP to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
