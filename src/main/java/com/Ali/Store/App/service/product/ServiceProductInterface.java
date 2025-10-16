package com.Ali.Store.App.service.product;

import com.Ali.Store.App.dto.product.request.*;
import com.Ali.Store.App.dto.product.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ServiceProductInterface {
    Map<String, Object> createOrUpdateProduct(CreateProductRequest productRequest);
    ProductResponse increaseQuality(QuantityIncreaseRequest request, Long productId);
    Page<ProductResponse> searchProducts(SearchProductRequest search, Pageable pageable);
    ProductResponse updateProductPrice(PriceDeltaRequest priceDeltaRequest, Long productId);
    ProductResponse getProductByNameAndCategory(FindProductRequest findProductRequest);
    void deleteProduct(Long productId);
}
