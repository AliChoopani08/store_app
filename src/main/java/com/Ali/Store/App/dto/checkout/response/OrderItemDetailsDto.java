package com.Ali.Store.App.dto.checkout.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record OrderItemDetailsDto(Long id, @JsonProperty("Product id") Long productId, @JsonProperty("Product name") String productName
        , @JsonProperty("Product Slug") String productSlug, @JsonProperty("Product Price") BigDecimal productPrice, @JsonProperty("quantity") int quantity) {
}
