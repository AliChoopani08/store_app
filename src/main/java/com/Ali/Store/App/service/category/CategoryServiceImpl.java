package com.Ali.Store.App.service.category;

import com.Ali.Store.App.dto.product.CategoryMapper;
import com.Ali.Store.App.dto.product.request.CategoryRequest;
import com.Ali.Store.App.dto.product.request.ChangeNameCategoryRequest;
import com.Ali.Store.App.dto.product.response.CategorySummary;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.exceptions.productAndCategory.DuplicateCategory;
import com.Ali.Store.App.exceptions.productAndCategory.NotFoundCategory;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repositoryCategory;
    private final CategoryMapper mapper;

    @Override
    @Transactional
    public CategorySummary createCategory(CategoryRequest request) {
    repositoryCategory.findByNameIgnoreCase(request.getName())
            .ifPresent(__ -> {
                throw new DuplicateCategory(request.getName());
            });
        final Category category = mapper.toEntity(request);
        final Category savedCategory = repositoryCategory.save(category);

        log.info("Category [{}] created successfully", savedCategory.getId());
        return mapper.ToSummary(savedCategory);
    }

    @Override
    @Transactional
    public CategorySummary changeName(Long id, ChangeNameCategoryRequest request) {
        final Category foundCategory = getCategoryById(id);

        foundCategory.setName(request.getNewName());
        final Category updatedCategory = repositoryCategory.save(foundCategory);

        log.info("Category [{}] name changed successfully", updatedCategory.getId());
        return mapper.ToSummary(updatedCategory);
    }


    @Override
    public CategorySummary getCategoryByName(String categoryName) {
        final Category foundCategory = repositoryCategory.findByNameIgnoreCase(categoryName)
                .orElseThrow(() -> new NotFoundCategory(categoryName));

        return mapper.ToSummary(foundCategory);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        final Category foundCategory = getCategoryById(id);

        repositoryCategory.deleteById(foundCategory.getId());

        log.info("Category [{}] deleted successfully", foundCategory.getId());
    }


    private Category getCategoryById(Long id) {
        return repositoryCategory.findById(id)
                .orElseThrow(() -> new NotFoundCategory(id));
    }
}
