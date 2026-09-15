package com.oilcommerce.cart.dto;
import jakarta.validation.constraints.Min; import lombok.Data;
@Data public class UpdateCartRequest { @Min(1) private int quantity; }
