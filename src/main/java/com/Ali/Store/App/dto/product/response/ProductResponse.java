package com.Ali.Store.App.dto.product.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonRootName(value = "Product")
public record ProductResponse(Long id, String name, int price, int quantity, String slug, @JsonProperty("Category") CategoryResponse categoryResponse) {
}
