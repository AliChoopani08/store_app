package com.Ali.Store.App.dto.checkout.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddToCartRequest {
    @NotNull(message = "Product id cannot be null")
    @Positive(message = "Product id must be positive !")
    private Long productId;

    @Positive(message = "Quantity of product must be a positive number !")
    @Min(value = 1, message = "The quantity of product must be at least 1")
    private int quantity;
}
