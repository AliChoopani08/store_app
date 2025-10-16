package com.Ali.Store.App.dto.product.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class CategoryRequest {
    @NotBlank(message = "Category name cannot be empty !")
    @Pattern(regexp = "^[A-Za-zآ-ی ]+$", message = "Only English or Persin words is valid !")
    @JsonProperty("Category name")
    private String name;
}
