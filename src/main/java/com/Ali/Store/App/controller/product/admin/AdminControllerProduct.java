package com.Ali.Store.App.controller.product.admin;

import com.Ali.Store.App.dto.product.request.CreateProductRequest;
import com.Ali.Store.App.dto.product.request.FindProductRequest;
import com.Ali.Store.App.dto.product.request.PriceDeltaRequest;
import com.Ali.Store.App.dto.product.request.QuantityIncreaseRequest;
import com.Ali.Store.App.dto.product.response.ApiResponse;
import com.Ali.Store.App.dto.product.response.ProductSummary;
import com.Ali.Store.App.service.product.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import static com.Ali.Store.App.service.product.ItemStatus.INCREASED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Tag(name = "Product API", description = "Operations related to Management Products by admin")
@RestController
@RequestMapping("/admin/product")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminControllerProduct {

    private final ProductService service;


    @PostMapping
    @Operation(
            summary = "Create Or Update Product",
            description = """
                     Behavior: \n
                     - If the product exists in the database ->
                     its quantity and productPrice is updated with
                     fields of inserted product \n
                     - If the product does not exist ->
                      a new product is created.
                     """,
           responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Product is created -> (201)"
                        , description = "a new product was creates") ,
                   @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Product quantity is updated -> (200)"
                        , description = "An existing product quantity and productPrice was updated")
           }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrUpdateProduct(@RequestBody @Valid CreateProductRequest productRequest) {
        final Map<String, Object> savedProduct = service.createOrUpdateProduct(productRequest);

        if (savedProduct.get("status") == INCREASED) {
            final ApiResponse<Map<String, Object>> responseUpdateProduct = new ApiResponse<>(200,"Product quantity and productPrice updated successfully",savedProduct);
            return ok(responseUpdateProduct);
        }
        else {
            final ApiResponse<Map<String, Object>> responseCreateProduct = new ApiResponse<>(201, "Product created successfully", savedProduct);
            return status(CREATED)
                    .body(responseCreateProduct);
        }
    }

    @PatchMapping("/quantity/{id}")
    @Operation(
            summary = "Increase Product Quantity"
    )
    public ResponseEntity<ApiResponse<ProductSummary>> increaseProductQuantity(@PathVariable Long id, @RequestBody @Valid QuantityIncreaseRequest increaseRequest) {
        final ProductSummary increasedQuality = service.increaseQuality(increaseRequest, id);

        return ok(new ApiResponse<>(200, "Product quantity updated successfully", increasedQuality));
    }


    @PatchMapping("/productPrice/{id}")
    @Operation(
            summary = "Reset Product Price"
    )
    public ResponseEntity<ApiResponse<ProductSummary>> resetProductPrice(@PathVariable Long id, @RequestBody @Valid PriceDeltaRequest priceDeltaRequest) {
        final ProductSummary productResponse = service.resetProductPrice(priceDeltaRequest, id);

        return ok(new ApiResponse<>(200, "New productPrice registered", productResponse));
    }

    @GetMapping
    @Operation(
            summary = "Get Product By Name And Category"
    )
    public ResponseEntity<ApiResponse<ProductSummary>> getProductByNameAndCategory(@ModelAttribute @Valid FindProductRequest findProductRequest) {
        final ProductSummary productResponse = service.getProductByNameAndCategory(findProductRequest);

        return ok(new ApiResponse<>(200, "This product found", productResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Product By Id"
    )
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);

        return status(NO_CONTENT)
                .build();
    }
}
