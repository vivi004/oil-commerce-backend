package com.oilcommerce.common.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Value("${app.email.resend-api-key:}")
    private String resendApiKey;

    @Value("${app.email.resend-from:Nisha Pure Oils <onboarding@resend.dev>}")
    private String resendFrom;

    @Value("${app.email.brevo-api-key:}")
    private String brevoApiKey;

    @Value("${app.email.brevo-from-email:}")
    private String brevoFromEmail;

    @Value("${app.email.brevo-from-name:Nisha Pure Oils}")
    private String brevoFromName;

    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/auth/reset-password?token=" + token;

        // Prominently log reset link for dev & instant reference
        log.info("================================================================================");
        log.info("[Password Reset Link] For: {}", toEmail);
        log.info("Link: {}", resetLink);
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

        // 1. If Resend API Key is provided, use Resend HTTP API (Port 443 - works on Render Free Tier)
        if (resendApiKey != null && !resendApiKey.isBlank()) {
            boolean sent = sendViaResend(toEmail, subject, htmlContent);
            if (sent) return;
        }

        // 2. If Brevo API Key is provided, use Brevo HTTP API (Port 443 - works on Render Free Tier)
        if (brevoApiKey != null && !brevoApiKey.isBlank()) {
            boolean sent = sendViaBrevo(toEmail, subject, htmlContent);
            if (sent) return;
        }

        // 3. Fallback to SMTP (Port 587 - works locally and on paid cloud instances)
        sendViaSmtp(toEmail, subject, htmlContent);
    }

    private boolean sendViaResend(String toEmail, String subject, String htmlContent) {
        try {
            log.info("Sending email to {} via Resend HTTP API (Port 443)...", toEmail);
            String jsonPayload = objectMapper.writeValueAsString(Map.of(
                    "from", resendFrom,
                    "to", List.of(toEmail),
                    "subject", subject,
                    "html", htmlContent
            ));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + resendApiKey.trim())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload, StandardCharsets.UTF_8))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                log.info("Email successfully sent via Resend to {}: {}", toEmail, response.body());
                return true;
            } else {
                log.error("Resend API returned status {}: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Resend email request interrupted for {}: {}", toEmail, e.getMessage());
            return false;
        } catch (java.io.IOException e) {
            log.error("Failed to send email via Resend to {}: {}", toEmail, e.getMessage());
            return false;
        }
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
            log.error("Brevo email request interrupted for {}: {}", toEmail, e.getMessage());
            return false;
        } catch (java.io.IOException e) {
            log.error("Failed to send email via Brevo to {}: {}", toEmail, e.getMessage());
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
        } catch (jakarta.mail.MessagingException | java.io.UnsupportedEncodingException | org.springframework.mail.MailException e) {
            log.error("Failed to send password reset email via SMTP to {}: {}", toEmail, e.getMessage());
        }
    }
}
