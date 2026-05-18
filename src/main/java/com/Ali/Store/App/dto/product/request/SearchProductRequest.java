package com.Ali.Store.App.dto.product.request;

import lombok.Builder;
import java.math.BigDecimal;

@Builder
public record SearchProductRequest(String category, String name
        , BigDecimal maxPrice, BigDecimal minPrice, Boolean status) {}
