package com.Ali.Store.App.dto.product;


import com.Ali.Store.App.dto.product.request.CategoryRequest;
import com.Ali.Store.App.dto.product.response.CategoryResponse;
import com.Ali.Store.App.entities.productAndCategory.Category;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring"
,nullValuePropertyMappingStrategy = IGNORE)
@Component("categoryMapper")
public interface CategoryMapper {

    Category categoryRequestToCategory(CategoryRequest categoryRequest);
    CategoryResponse categoryToCategoryResponse(Category category);
}
