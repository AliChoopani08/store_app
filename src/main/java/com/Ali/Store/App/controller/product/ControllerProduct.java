package com.Ali.Store.App.controller.product;

import com.Ali.Store.App.dto.product.response.ApiResponse;
import com.Ali.Store.App.dto.product.request.*;
import com.Ali.Store.App.dto.product.response.ProductResponse;
import com.Ali.Store.App.service.product.ServiceProductInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Search Product API")
@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ControllerProduct {

    private final ServiceProductInterface service;


    @GetMapping
    @Operation(
            summary = "Search product",
            security = {@SecurityRequirement(name = "")}
    )
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> searchDynamicProduct(@ModelAttribute
                          SearchProductRequest searchProductRequest, @PageableDefault(size = 20, sort = "name", direction = DESC) @ParameterObject Pageable pageable) {

        final Page<ProductResponse> productResponses = service.searchProducts(searchProductRequest, pageable);

        return ok(new ApiResponse<>(200, "Books fetched successfully", productResponses));
    }
}
