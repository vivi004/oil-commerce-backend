package com.oilcommerce.common.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    @Async
    public void sendPasswordResetEmail(String toEmail, String token) {
        String resetLink = frontendUrl + "/auth/reset-password?token=" + token;

        // In dev or when SMTP is not yet configured, log the reset URL for quick testing
        log.info("================================================================================");
        log.info("[Password Reset Link] For: {}", toEmail);
        log.info("Link: {}", resetLink);
        log.info("================================================================================");

        if (fromEmail == null || fromEmail.isBlank()) {
            log.warn("SMTP username not configured in application.yml. Skipped sending email over SMTP.");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, "Nisha Pure Oils");
            helper.setTo(toEmail);
            helper.setSubject("Reset Your Password — Nisha Pure Oils");

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

            helper.setText(htmlContent, true);
            mailSender.send(message);
            log.info("Password reset email successfully sent to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }
}
