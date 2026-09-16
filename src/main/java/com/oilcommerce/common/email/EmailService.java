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
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
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

    // Fast-fail HTTP client: 5-second connect timeout
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Value("${app.frontend-url:${APP_FRONTEND_URL:${FRONTEND_URL:https://oil-commerce-frontend.onrender.com}}}")
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

    /**
     * Deterministic result record returned to calling services/controllers.
     */
    public record EmailSendResult(
            boolean success,
            int statusCode,
            String responseBody,
            String message,
            long elapsedMs
    ) {}

    @PostConstruct
    public void verifyEmailConfiguration() {
        log.info("================================================================================");
        log.info("[EmailService Configuration Audit]");

        boolean keyLoaded = resendApiKey != null && !resendApiKey.trim().isBlank();
        if (keyLoaded) {
            String maskedKey = maskApiKey(resendApiKey.trim());
            log.info("✅ Resend API Key loaded: {}", maskedKey);
            log.info("📧 Configured Resend Sender: {}", resolveSenderEmail());
            log.info("🌐 Configured Frontend URL : {}", resolveFrontendUrl());

            if (resolveSenderEmail().contains("onboarding@resend.dev")) {
                log.warn("⚠️ NOTICE: Sender is 'onboarding@resend.dev' (unverified Resend test domain).");
                log.warn("   Resend test policy allows delivery ONLY to your registered Resend account email!");
                log.warn("   To send to arbitrary customer emails, verify a custom domain and set RESEND_FROM.");
            }
        } else {
            log.error("❌ RESEND_API_KEY is NOT configured!");
            boolean isProd = environment.acceptsProfiles(Profiles.of("prod"));
            if (isProd) {
                log.error("❌ Startup failure: RESEND_API_KEY is required in production environment (Render)!");
                throw new IllegalStateException("CRITICAL STARTUP FAILURE: RESEND_API_KEY environment variable is missing in production! Please configure RESEND_API_KEY in Render environment variables.");
            } else {
                log.warn("⚠️ Non-production mode: startup continues, but Resend email delivery will be unavailable.");
            }
        }
        log.info("================================================================================");
    }

    /**
     * Mask API key for diagnostic logging without exposing sensitive secrets.
     */
    public String maskApiKey(String key) {
        if (key == null || key.isBlank()) return "***";
        String trimmed = key.trim();
        if (trimmed.length() <= 8) return "***";
        return trimmed.substring(0, 6) + "..." + trimmed.substring(trimmed.length() - 4);
    }

    /**
     * Validates recipient email format.
     */
    public boolean isValidEmail(String email) {
        return email != null && !email.trim().isBlank() && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Resolves the frontend URL, strictly avoiding localhost when deployed to production.
     */
    public String resolveFrontendUrl() {
        String url = (frontendUrl != null && !frontendUrl.trim().isBlank()) ? frontendUrl.trim() : "";
        boolean isProd = environment.acceptsProfiles(Profiles.of("prod"));
        if (isProd && (url.isEmpty() || url.contains("localhost"))) {
            log.warn("⚠️ Production active but frontendUrl contains localhost ('{}'). Overriding to: https://oil-commerce-frontend.onrender.com", url);
            return "https://oil-commerce-frontend.onrender.com";
        }
        if (url.isEmpty()) {
            return "http://localhost:4200";
        }
        return url.replaceAll("/+$", "");
    }

    /**
     * Resolves the sender email.
     */
    public String resolveSenderEmail() {
        if (resendFrom != null && !resendFrom.trim().isBlank()) {
            return resendFrom.trim();
        }
        return "onboarding@resend.dev";
    }

    /**
     * Synchronous password reset email dispatch.
     * Returns a deterministic EmailSendResult so the controller knows whether the email was accepted.
     */
    public EmailSendResult sendPasswordResetEmail(String toEmail, String token) {
        long startTime = System.currentTimeMillis();
        log.info("[Password Reset Flow] Initiating password reset email delivery for recipient: '{}'", toEmail);

        if (toEmail == null || toEmail.trim().isBlank()) {
            log.error("[Password Reset Flow] Aborted: Recipient email is null or empty!");
            return new EmailSendResult(false, 400, "", "Recipient email cannot be null or empty", 0);
        }

        String cleanEmail = toEmail.trim();
        if (!isValidEmail(cleanEmail)) {
            log.error("[Password Reset Flow] Aborted: Recipient email '{}' has invalid email format!", cleanEmail);
            return new EmailSendResult(false, 400, "", "Invalid recipient email format: " + cleanEmail, 0);
        }

        if (token == null || token.trim().isBlank()) {
            log.error("[Password Reset Flow] Aborted: Password reset token is null or empty for recipient '{}'!", cleanEmail);
            return new EmailSendResult(false, 400, "", "Password reset token is missing", 0);
        }

        String targetFrontendUrl = resolveFrontendUrl();
        String resetLink = targetFrontendUrl + "/auth/reset-password?token=" + token.trim();

        log.info("================================================================================");
        log.info("[Password Reset Flow] Recipient   : {}", cleanEmail);
        log.info("[Password Reset Flow] Frontend URL: {}", targetFrontendUrl);
        log.info("[Password Reset Flow] Reset Link  : {}", resetLink);
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

        // 1. Primary: Synchronous Resend HTTP API (Port 443 HTTPS, 8s timeout)
        if (resendApiKey != null && !resendApiKey.trim().isBlank()) {
            log.info("[Password Reset Flow] Dispatching via Resend HTTP API...");
            return sendViaResendDetailed(cleanEmail, subject, htmlContent);
        }

        log.warn("[Password Reset Flow] RESEND_API_KEY is not configured on server.");

        // 2. Secondary fallback: Brevo HTTP API (Port 443)
        if (brevoApiKey != null && !brevoApiKey.trim().isBlank()) {
            log.info("[Password Reset Flow] Attempting delivery via Brevo HTTP API fallback...");
            boolean sent = sendViaBrevo(cleanEmail, subject, htmlContent);
            long elapsed = System.currentTimeMillis() - startTime;
            if (sent) {
                return new EmailSendResult(true, 200, "{\"provider\":\"brevo\"}", "Email sent via Brevo fallback", elapsed);
            }
        }

        // 3. Tertiary fallback: SMTP (Port 587)
        if (fromEmail != null && !fromEmail.trim().isBlank()) {
            log.info("[Password Reset Flow] Attempting delivery via SMTP fallback...");
            boolean sent = sendViaSmtp(cleanEmail, subject, htmlContent);
            long elapsed = System.currentTimeMillis() - startTime;
            if (sent) {
                return new EmailSendResult(true, 200, "{\"provider\":\"smtp\"}", "Email sent via SMTP fallback", elapsed);
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        return new EmailSendResult(false, 500, "", "No working email provider configured. Please set RESEND_API_KEY.", elapsed);
    }

    /**
     * Executes a synchronous email send via Resend HTTP API with strict 8-second timeout.
     */
    public EmailSendResult sendViaResendDetailed(String toEmail, String subject, String htmlContent) {
        long startTime = System.currentTimeMillis();

        if (toEmail == null || toEmail.trim().isBlank()) {
            return new EmailSendResult(false, 400, "", "Recipient email cannot be null or empty", 0);
        }

        String cleanEmail = toEmail.trim();
        if (!isValidEmail(cleanEmail)) {
            return new EmailSendResult(false, 400, "", "Invalid recipient email format: " + cleanEmail, 0);
        }

        if (resendApiKey == null || resendApiKey.trim().isBlank()) {
            return new EmailSendResult(false, 500, "", "RESEND_API_KEY is not configured on the server", 0);
        }

        String sender = resolveSenderEmail();
        log.info("[Resend] Sending email: From='{}' -> To='{}', Subject='{}'", sender, cleanEmail, subject);

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
                    .timeout(Duration.ofSeconds(8)) // 8-second HTTP request timeout
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            long elapsed = System.currentTimeMillis() - startTime;

            int statusCode = response.statusCode();
            String responseBody = response.body();
            String sanitizedResponse = sanitizeResponse(responseBody);

            log.info("[Resend] Completed in {} ms. Status: {}, Sender: '{}', Recipient: '{}'",
                    elapsed, statusCode, sender, cleanEmail);
            log.info("[Resend] Response body: {}", sanitizedResponse);

            if (statusCode >= 200 && statusCode < 300) {
                log.info("✅ [Resend] Email successfully accepted for delivery to {} in {} ms", cleanEmail, elapsed);
                return new EmailSendResult(true, statusCode, sanitizedResponse, "Email accepted by Resend", elapsed);
            }

            // Explicit error status mapping
            String errorMsg;
            int returnStatusCode = statusCode;

            if (statusCode == 401) {
                errorMsg = "Resend API authentication failed (HTTP 401). Please verify RESEND_API_KEY in Render environment variables.";
                returnStatusCode = 500;
                log.error("❌ [Resend 401] {}", errorMsg);
            } else if (statusCode == 403) {
                if (responseBody != null && (responseBody.contains("validation_error") || responseBody.contains("testing"))) {
                    errorMsg = "Resend testing restriction (HTTP 403): Default 'onboarding@resend.dev' sender can only send to your own registered Resend account email. To send to '" + cleanEmail + "', verify your domain at https://resend.com/domains and set RESEND_FROM.";
                } else {
                    errorMsg = "Resend access forbidden (HTTP 403): " + sanitizedResponse;
                }
                returnStatusCode = 403;
                log.error("❌ [Resend 403] {}", errorMsg);
            } else if (statusCode == 429) {
                errorMsg = "Resend API rate limit exceeded (HTTP 429). Please try again in a few moments.";
                returnStatusCode = 429;
                log.error("❌ [Resend 429] Rate limit exceeded");
            } else if (statusCode >= 500) {
                errorMsg = "Resend email service temporarily unavailable (HTTP " + statusCode + "). Please try again later.";
                returnStatusCode = 502;
                log.error("❌ [Resend 5xx] Status {}: {}", statusCode, sanitizedResponse);
            } else {
                errorMsg = "Resend returned HTTP " + statusCode + ": " + sanitizedResponse;
                log.error("❌ [Resend Error] {}", errorMsg);
            }

            return new EmailSendResult(false, returnStatusCode, sanitizedResponse, errorMsg, elapsed);

        } catch (HttpTimeoutException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            String errorMsg = "Email service connection timed out after " + elapsed + " ms. Please try again.";
            log.error("❌ [Resend Timeout] Request timed out after {} ms for {}: {}", elapsed, cleanEmail, e.getMessage());
            return new EmailSendResult(false, 504, "", errorMsg, elapsed);
        } catch (ConnectException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            String errorMsg = "Failed to connect to Resend email API. Please check server outbound connectivity.";
            log.error("❌ [Resend Connection Failed] Failed to connect for {}: {}", cleanEmail, e.getMessage());
            return new EmailSendResult(false, 502, "", errorMsg, elapsed);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            long elapsed = System.currentTimeMillis() - startTime;
            String errorMsg = "Email request interrupted.";
            log.error("❌ [Resend Interrupted] Interrupted after {} ms for {}: {}", elapsed, cleanEmail, e.getMessage());
            return new EmailSendResult(false, 500, "", errorMsg, elapsed);
        } catch (java.io.IOException | RuntimeException e) {
            long elapsed = System.currentTimeMillis() - startTime;
            String errorMsg = "Email dispatch failed: " + e.getMessage();
            log.error("❌ [Resend Exception] Failed after {} ms for {}: {}", elapsed, cleanEmail, e.getMessage(), e);
            return new EmailSendResult(false, 500, "", errorMsg, elapsed);
        }
    }

    /**
     * Sanitizes response strings to avoid leaking keys or excessive payload length.
     */
    private String sanitizeResponse(String response) {
        if (response == null) return "";
        String trimmed = response.trim();
        if (trimmed.length() > 500) {
            return trimmed.substring(0, 500) + "... [truncated]";
        }
        return trimmed;
    }

    /**
     * Diagnostic test email endpoint.
     */
    public EmailSendResult sendTestEmail(String toEmail) {
        String cleanEmail = toEmail != null ? toEmail.trim() : "";
        log.info("[Test Email] Diagnostic test email requested for '{}'", cleanEmail);

        String subject = "Resend Diagnostic Test — Nisha Pure Oils";
        String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;">
                <h2 style="color: #92400e;">Nisha Pure Oils &bull; Resend Diagnostic Test</h2>
                <p>Hello,</p>
                <p>This test email confirms that your Resend API email integration is <strong>active and working</strong>!</p>
                <div style="background: #fdf6b2; border-left: 4px solid #b45309; padding: 12px; margin: 16px 0;">
                    <p style="margin: 0; font-size: 13px; color: #723b13;">
                        <strong>Sender:</strong> %s<br/>
                        <strong>Recipient:</strong> %s<br/>
                        <strong>Timestamp:</strong> %s
                    </p>
                </div>
            </div>
            """.formatted(resolveSenderEmail(), cleanEmail, java.time.Instant.now().toString());

        return sendViaResendDetailed(cleanEmail, subject, htmlContent);
    }

    private boolean sendViaBrevo(String toEmail, String subject, String htmlContent) {
        try {
            log.info("Sending email to {} via Brevo HTTP API...", toEmail);
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
                    .timeout(Duration.ofSeconds(8))
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
            log.error("Brevo email request interrupted for {}: {}", toEmail, e.getMessage());
            return false;
        } catch (java.io.IOException | RuntimeException e) {
            log.error("Failed to send email via Brevo to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }

    private boolean sendViaSmtp(String toEmail, String subject, String htmlContent) {
        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("No SMTP username configured. SMTP fallback skipped.");
            return false;
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
            return true;
        } catch (jakarta.mail.MessagingException | java.io.UnsupportedEncodingException | RuntimeException e) {
            log.error("Failed to send password reset email via SMTP to {}: {}", toEmail, e.getMessage(), e);
            return false;
        }
    }
}
