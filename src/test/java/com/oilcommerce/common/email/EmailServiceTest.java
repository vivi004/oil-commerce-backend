package com.oilcommerce.common.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private Environment environment;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private EmailService emailService;

    @BeforeEach
    public void setUp() {
        emailService = new EmailService(mailSender, objectMapper, environment);
        ReflectionTestUtils.setField(emailService, "frontendUrl", "https://oil-commerce-frontend.onrender.com");
        ReflectionTestUtils.setField(emailService, "resendApiKey", "re_test_key_123456789");
        ReflectionTestUtils.setField(emailService, "resendFrom", "onboarding@resend.dev");
    }

    @Test
    void testIsValidEmail() {
        assertTrue(emailService.isValidEmail("user@example.com"));
        assertTrue(emailService.isValidEmail("first.last@domain.co.in"));
        assertTrue(emailService.isValidEmail("test+tag@sub.example.org"));

        assertFalse(emailService.isValidEmail(null));
        assertFalse(emailService.isValidEmail(""));
        assertFalse(emailService.isValidEmail("   "));
        assertFalse(emailService.isValidEmail("plainaddress"));
        assertFalse(emailService.isValidEmail("@missingusername.com"));
        assertFalse(emailService.isValidEmail("user@.com"));
    }

    @Test
    void testResolveSenderEmail_Default() {
        ReflectionTestUtils.setField(emailService, "resendFrom", "");
        assertEquals("onboarding@resend.dev", emailService.resolveSenderEmail());

        ReflectionTestUtils.setField(emailService, "resendFrom", "custom@mydomain.com");
        assertEquals("custom@mydomain.com", emailService.resolveSenderEmail());
    }

    @Test
    void testSendViaResendDetailed_RejectsInvalidEmail() {
        EmailService.EmailSendResult result = emailService.sendViaResendDetailed("invalid-email", "Subject", "<p>Hello</p>");
        assertFalse(result.success());
        assertEquals(400, result.statusCode());
        assertTrue(result.message().contains("Invalid recipient email format"));
    }

    @Test
    void testSendViaResendDetailed_RejectsMissingApiKey() {
        ReflectionTestUtils.setField(emailService, "resendApiKey", "");
        EmailService.EmailSendResult result = emailService.sendViaResendDetailed("test@example.com", "Subject", "<p>Hello</p>");
        assertFalse(result.success());
        assertEquals(500, result.statusCode());
        assertTrue(result.message().contains("RESEND_API_KEY is not configured"));
    }

    @Test
    void testVerifyEmailConfiguration_MasksKey() {
        assertDoesNotThrow(() -> emailService.verifyEmailConfiguration());
    }
}
