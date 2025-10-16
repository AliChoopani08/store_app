package com.Ali.Store.App.dto.product.request;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateProductRequest {

    @NotBlank(message = "Product name cannot be empty !")
    @Pattern(regexp = "^[A-Za-z0-9آ-ی ]+$", message = "Only English or Persin words is valid !")
    private String name;

    @NotNull(message = "Price cannot be null !")
    @Positive(message = "Price must be a positive number")
    private Integer price;

    @NotBlank(message = "Product category cannot be empty !")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Entered category name is invalid !")
    private String category;

    @Min(value = 1, message = "Quantity must be at least 1 !")
    private int quantity = 1;
}
