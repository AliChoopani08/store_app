package com.Ali.Store.App.controller.categoryAdmin;

import com.Ali.Store.App.dto.product.request.CategoryRequest;
import com.Ali.Store.App.dto.product.request.ChangeNameCategoryRequest;
import com.Ali.Store.App.dto.product.response.ApiResponse;
import com.Ali.Store.App.dto.product.response.CategorySummary;
import com.Ali.Store.App.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Tag(name = "Category API", description = "Operations related to management the categories of products by admin")
@RestController
@RequestMapping("/admin/category")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Slf4j
public class AdminControllerCategory {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(
            summary = "Create New Category"
    )
    public ResponseEntity<ApiResponse<CategorySummary>> createCategory(@RequestBody @Valid CategoryRequest categoryRequest) {
        log.info("API request: create category [{}]...", categoryRequest.getName());
        final CategorySummary savedCategory = categoryService.createCategory(categoryRequest);

        return status(CREATED)
                .body(new ApiResponse<>(201, "Category created successfully", savedCategory));
    }

    @PatchMapping("/name/{id}")
    @Operation(
            summary = "Change Category Name",
            parameters = @Parameter(name = "newName", description = "Alternative Name Of This Product")
    )
    public ResponseEntity<ApiResponse<CategorySummary>> changeCategoryName(@PathVariable Long id, @RequestBody @Valid ChangeNameCategoryRequest changeNameRequest) {
        log.info("API request: change category name [{}]...", id);

        final CategorySummary changedCategory = categoryService.changeName(id, changeNameRequest);

        return ok(new ApiResponse<>(200, "The category name changed successfully", changedCategory ));
    }

    @GetMapping
    @Operation(
            summary = "Get Category By Name"
    )
    public ResponseEntity<ApiResponse<CategorySummary>> getCategoryByName(@RequestParam String categoryName) {
        final CategorySummary foundCategory = categoryService.getCategoryByName(categoryName);

        return ok(new ApiResponse<>(200, "This category found successfully", foundCategory));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Category By Id"
    )
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        log.info("API request: delete category [{}]...", id);
        categoryService.delete(id);

        return status(NO_CONTENT)
                .build();
    }
}
