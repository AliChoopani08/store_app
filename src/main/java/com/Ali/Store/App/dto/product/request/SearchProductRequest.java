package com.Ali.Store.App.dto.product.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SearchProductRequest(String category, String name, @JsonProperty("Max price") Integer maxPrice,
                                   @JsonProperty("Min price")Integer minPrice, Boolean status) {}
