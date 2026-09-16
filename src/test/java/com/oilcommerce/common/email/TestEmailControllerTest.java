package com.oilcommerce.common.email;

import com.oilcommerce.common.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestEmailControllerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private TestEmailController controller;

    @BeforeEach
    public void setUp() {
        when(emailService.resolveSenderEmail()).thenReturn("onboarding@resend.dev");
    }

    @Test
    void testSendTestEmail_Success() {
        when(emailService.sendTestEmail(anyString()))
                .thenReturn(new EmailService.EmailSendResult(true, 200, "{\"id\":\"msg_123\"}", "Email accepted", 150));

        TestEmailRequest request = new TestEmailRequest("recipient@example.com");
        ResponseEntity<ApiResponse<Map<String, Object>>> response = controller.sendTestEmail(request);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<Map<String, Object>> body = java.util.Objects.requireNonNull(response.getBody());
        assertTrue(body.isSuccess());
        Map<String, Object> data = java.util.Objects.requireNonNull(body.getData());
        assertEquals("recipient@example.com", data.get("recipient"));
        assertEquals(200, data.get("statusCode"));
    }

    @Test
    void testSendTestEmail_ResendFailure() {
        when(emailService.sendTestEmail(anyString()))
                .thenReturn(new EmailService.EmailSendResult(false, 403, "{\"statusCode\":403,\"message\":\"validation_error\"}", "Forbidden", 120));

        TestEmailRequest request = new TestEmailRequest("unregistered@example.com");
        ResponseEntity<ApiResponse<Map<String, Object>>> response = controller.sendTestEmail(request);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        ApiResponse<Map<String, Object>> body = java.util.Objects.requireNonNull(response.getBody());
        Map<String, Object> data = java.util.Objects.requireNonNull(body.getData());
        assertEquals(403, data.get("statusCode"));
    }
}
