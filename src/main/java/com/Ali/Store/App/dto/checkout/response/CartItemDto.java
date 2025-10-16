package com.Ali.Store.App.dto.checkout.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

public record CartItemDto(@JsonProperty("Cart item id") Long id, @JsonProperty("Product id") Long productId, @JsonProperty("Product name") String productName
, @JsonProperty("Product category") String productCategory, @JsonProperty("Total price of product") int totalPriceOfProduct
        , @Schema(name = "Quantity", description = "The order quantity for this product") @JsonProperty("Quantity") int quantity){
}
