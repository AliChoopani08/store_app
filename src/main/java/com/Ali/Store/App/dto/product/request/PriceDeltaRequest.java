package com.Ali.Store.App.dto.product.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PriceDeltaRequest {

    @NotNull(message = "New Price cannot be empty !")
    @Positive(message = "Price must be a positive number !")
    @JsonProperty("new productPrice")
    private BigDecimal newPrice;
}
