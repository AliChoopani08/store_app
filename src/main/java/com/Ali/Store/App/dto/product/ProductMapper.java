package com.Ali.Store.App.dto.product;

import com.Ali.Store.App.dto.product.request.CreateProductRequest;
import com.Ali.Store.App.dto.product.response.CategorySummary;
import com.Ali.Store.App.dto.product.response.ProductSummary;
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
    Product toEntity(CreateProductRequest productRequest);

    @Mapping(source = "category", target = "categoryResponse")
    ProductSummary toSummary(Product product);
    CategorySummary toSummary(Category category);
}