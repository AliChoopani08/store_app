package com.Ali.Store.App.dto.product.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PriceDeltaRequest {

    @NotNull(message = "New Price cannot be empty !")
    @Positive(message = "Price must be a positive number !")
    @JsonProperty("new price")
    private int newPrice;
}
