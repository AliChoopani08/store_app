package com.Ali.Store.App.dto.checkout.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CartItemDto(@JsonProperty("Cart item id") Long id, @JsonProperty("Product id") Long productId, @JsonProperty("Product name") String productName
, @JsonProperty("Product category") String productCategory, @JsonProperty("Total productPrice of product") BigDecimal totalProductPrice
        , @Schema(name = "Quantity", description = "The order quantity for this product") @JsonProperty("Quantity") int quantity){
}
