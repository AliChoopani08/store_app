package com.Ali.Store.App.dto.product.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Builder;

import java.math.BigDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonRootName(value = "Product")
@Builder(toBuilder = true)
public record ProductSummary(Long id, String name, BigDecimal price, int quantity, String slug, @JsonProperty("Category") CategorySummary categoryResponse) {
}
