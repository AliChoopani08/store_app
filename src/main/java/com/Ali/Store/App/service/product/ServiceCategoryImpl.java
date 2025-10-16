package com.Ali.Store.App.service.product;

import com.Ali.Store.App.dto.product.CategoryMapper;
import com.Ali.Store.App.dto.product.request.CategoryRequest;
import com.Ali.Store.App.dto.product.request.ChangeNameCategoryRequest;
import com.Ali.Store.App.dto.product.response.CategoryResponse;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundCategory;
import com.Ali.Store.App.exceptions.DuplicateValueException;
import com.Ali.Store.App.repository.productAndCategory.RepositoryCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceCategoryImpl implements ServiceCategoryInterface{

    private final RepositoryCategory repositoryCategory;
    private final CategoryMapper mapper;

    @Override
    public CategoryResponse createCategory(CategoryRequest request) {
    repositoryCategory.findByNameIgnoreCase(request.getName())
            .ifPresent(_ -> {
                throw new DuplicateValueException("This category is already exist !");
            });
        final Category category = mapper.categoryRequestToCategory(request);
        final Category savedCategory = repositoryCategory.save(category);

        return mapper.categoryToCategoryResponse(savedCategory);
    }

    @Override
    public CategoryResponse changeName(Long id, ChangeNameCategoryRequest request) {
        final Category foundCategory = getCategoryById(id);

        foundCategory.setName(request.getNewName());
        final Category updatedCategory = repositoryCategory.save(foundCategory);

        return mapper.categoryToCategoryResponse(updatedCategory);
    }


    @Override
    public CategoryResponse getCategoryByName(String categoryName) {
        final Category foundCategory = repositoryCategory.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new NotFoundCategory("Not found this category !"));

        return mapper.categoryToCategoryResponse(foundCategory);
    }

    @Override
    public void delete(Long id) {
        repositoryCategory.deleteById(id);
    }


    private Category getCategoryById(Long id) {
        return repositoryCategory.findById(id)
                .orElseThrow(() -> new NotFoundCategory("Not found this category !"));
    }
}
