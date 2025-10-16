package com.Ali.Store.App.controller.product.admin;

import com.Ali.Store.App.dto.product.request.CreateProductRequest;
import com.Ali.Store.App.dto.product.request.FindProductRequest;
import com.Ali.Store.App.dto.product.request.PriceDeltaRequest;
import com.Ali.Store.App.dto.product.request.QuantityIncreaseRequest;
import com.Ali.Store.App.dto.product.response.ApiResponse;
import com.Ali.Store.App.dto.product.response.ProductResponse;
import com.Ali.Store.App.service.product.ServiceProductInterface;
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

    private final ServiceProductInterface service;


    @PostMapping
    @Operation(
            summary = "Create Or Update Product",
            description = """
                     Behavior: \n
                     - If the product exists in the database ->
                     its quantity and price is updated with
                     fields of inserted product \n
                     - If the product does not exist ->
                      a new product is created.
                     """,
           responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Product is created -> (201)"
                        , description = "a new product was creates") ,
                   @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "Product quantity is updated -> (200)"
                        , description = "An existing product quantity and price was updated")
           }
    )
    public ResponseEntity<ApiResponse<Map<String, Object>>> createOrUpdateProduct(@RequestBody @Valid CreateProductRequest productRequest) {
        final Map<String, Object> savedProduct = service.createOrUpdateProduct(productRequest);

        if (savedProduct.get("status") == INCREASED) {
            final ApiResponse<Map<String, Object>> responseUpdateProduct = new ApiResponse<>(200,"Product quantity and price is updated",savedProduct);
            return ok(responseUpdateProduct);
        }
        else {
            final ApiResponse<Map<String, Object>> responseCreateProduct = new ApiResponse<>(201, "Product is created", savedProduct);
            return status(CREATED)
                    .body(responseCreateProduct);
        }
    }

    @PatchMapping("/quantity/{id}")
    @Operation(
            summary = "Increase Product Quantity"
    )
    public ResponseEntity<ApiResponse<ProductResponse>> increaseProductQuantity(@PathVariable Long id, @RequestBody @Valid QuantityIncreaseRequest increaseRequest) {
        final ProductResponse increasedQuality = service.increaseQuality(increaseRequest, id);

        return ok(new ApiResponse<ProductResponse>(200, "Product quantity is updated", increasedQuality));
    }


    @PatchMapping("/price/{id}")
    @Operation(
            summary = "Update Product Price"
    )
    public ResponseEntity<ApiResponse<ProductResponse>> updateProductPrice(@PathVariable Long id,@RequestBody @Valid PriceDeltaRequest priceDeltaRequest) {
        final ProductResponse productResponse = service.updateProductPrice(priceDeltaRequest, id);

        return ok(new ApiResponse<>(200, "New price registered", productResponse));
    }

    @GetMapping
    @Operation(
            summary = "Get Product By Name And Category"
    )
    public ResponseEntity<ApiResponse<ProductResponse>> getProductByNameAndCategory(@ModelAttribute @Valid FindProductRequest findProductRequest) {
        final ProductResponse productResponse = service.getProductByNameAndCategory(findProductRequest);

        return ok(new ApiResponse<>(200, "This product found", productResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Product By Id"
    )
    public void deleteProduct(@PathVariable Long id) {
        service.deleteProduct(id);

        status(NO_CONTENT)
                .build();
    }
}
