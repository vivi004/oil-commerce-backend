package com.oilcommerce.address.dto;
import jakarta.validation.constraints.NotBlank; import lombok.Data;
@Data
public class AddressRequest {
    private String label;
    @NotBlank private String fullName; @NotBlank private String phone;
    @NotBlank private String addressLine1; private String addressLine2;
    @NotBlank private String city; @NotBlank private String state;
    @NotBlank private String postalCode; private String country = "India";
    private boolean defaultAddress;
}
