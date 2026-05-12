package com.Ali.Store.App.service.category;

import com.Ali.Store.App.dto.product.request.CategoryRequest;
import com.Ali.Store.App.dto.product.request.ChangeNameCategoryRequest;
import com.Ali.Store.App.dto.product.response.CategorySummary;

public interface CategoryService {
    CategorySummary createCategory(CategoryRequest categoryRequest);
    CategorySummary changeName(Long id, ChangeNameCategoryRequest request);
    CategorySummary getCategoryByName(String categoryName);
    void delete(Long id);
}
