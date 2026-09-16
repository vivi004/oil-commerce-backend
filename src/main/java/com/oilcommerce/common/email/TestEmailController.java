package com.oilcommerce.common.email;

import com.oilcommerce.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "Test", description = "Diagnostic and test endpoints")
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestEmailController {

    private final EmailService emailService;

    @Operation(summary = "Send a test email via Resend to verify delivery pipeline")
    @PostMapping("/email")
    public ResponseEntity<ApiResponse<Map<String, Object>>> sendTestEmail(@Valid @RequestBody TestEmailRequest request) {
        log.info("[Test Email Endpoint] Initiating test email delivery for '{}'", request.getEmail());

        EmailService.EmailSendResult result = emailService.sendTestEmail(request.getEmail());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("recipient", request.getEmail());
        responseData.put("statusCode", result.statusCode());
        responseData.put("responseBody", result.responseBody());
        responseData.put("sender", emailService.resolveSenderEmail());
        responseData.put("elapsedMs", result.elapsedMs());

        if (result.success()) {
            return ResponseEntity.ok(ApiResponse.success("Test email sent successfully via Resend", responseData));
        } else {
            HttpStatus status = HttpStatus.resolve(result.statusCode());
            if (status == null || status.is2xxSuccessful()) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }
            responseData.put("error", result.message());
            return ResponseEntity.status(status)
                    .body(ApiResponse.success("Failed to deliver test email via Resend: " + result.message(), responseData));
        }
    }
}
