package com.Ali.Store.App.dto.product;

import com.Ali.Store.App.dto.product.request.CreateProductRequest;
import com.Ali.Store.App.dto.product.response.CategoryResponse;
import com.Ali.Store.App.dto.product.response.ProductResponse;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring",
nullValuePropertyMappingStrategy = IGNORE)
@Component("productMapper")
public interface ProductMapper {

    @Mapping(target = "category", ignore = true)
    Product productRequestToProduct(CreateProductRequest productRequest);

    @Mapping(source = "category", target = "categoryResponse")
    ProductResponse productToProductResponse(Product product);
    CategoryResponse categoryToCategoryResponse(Category category);
}