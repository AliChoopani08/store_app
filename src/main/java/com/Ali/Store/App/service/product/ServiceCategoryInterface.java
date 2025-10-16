package com.Ali.Store.App.service.product;

import com.Ali.Store.App.dto.product.request.CategoryRequest;
import com.Ali.Store.App.dto.product.request.ChangeNameCategoryRequest;
import com.Ali.Store.App.dto.product.response.CategoryResponse;

public interface ServiceCategoryInterface {
    CategoryResponse createCategory(CategoryRequest categoryRequest);
    CategoryResponse changeName(Long id, ChangeNameCategoryRequest request);
    CategoryResponse getCategoryByName(String categoryName);
    void delete(Long id);
}
