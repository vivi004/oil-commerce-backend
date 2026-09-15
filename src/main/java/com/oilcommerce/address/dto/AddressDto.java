package com.oilcommerce.address.dto;
import lombok.Builder; import lombok.Data; import java.util.UUID;
@Data @Builder
public class AddressDto {
    private UUID id; private String label; private String fullName; private String phone;
    private String addressLine1; private String addressLine2; private String city;
    private String state; private String postalCode; private String country; private boolean isDefault;
}
