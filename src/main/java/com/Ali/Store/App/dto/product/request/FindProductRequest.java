package com.Ali.Store.App.dto.product.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FindProductRequest {

    @NotBlank(message = "Product name cannot be empty !")
    @Pattern(regexp = "^[A-Za-z0-9آ-ی ]+$", message = "Only English or Persin words is valid !")
    private String name;

    @NotBlank(message = "Product category cannot be empty !")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Entered category name is invalid !")
    private String category;
}
