package com.Ali.Store.App.dto.checkout.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderItemDetailsDto(Long id, @JsonProperty("Product id") Long productId, @JsonProperty("Product name") String productName
        ,@JsonProperty("Product Slug") String productSlug, @JsonProperty("Product price") int price, @JsonProperty("quantity") int quantity) {
}
