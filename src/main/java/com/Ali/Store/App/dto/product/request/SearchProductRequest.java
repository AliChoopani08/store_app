package com.Ali.Store.App.dto.product.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record SearchProductRequest(@JsonProperty("Category")String category, @JsonProperty("Name")String name
        , @JsonProperty("Max productPrice") BigDecimal maxPrice,
                                   @JsonProperty("Min productPrice")BigDecimal minPrice, Boolean status) {}
