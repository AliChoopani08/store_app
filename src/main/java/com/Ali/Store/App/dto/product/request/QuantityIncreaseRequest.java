package com.Ali.Store.App.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class QuantityIncreaseRequest {

        @NotNull(message = "Quantity cannot be empty !")
        @Positive(message = "Quantity must be a positive number !")
        @Schema(name = "New quantity")
        private int quantity;
}
