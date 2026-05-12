package com.Ali.Store.App.service.product;

import com.Ali.Store.App.dto.product.request.*;
import com.Ali.Store.App.dto.product.response.ProductSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
public interface ProductService {
    Map<String, Object> createOrUpdateProduct(CreateProductRequest productRequest);
    ProductSummary increaseQuality(QuantityIncreaseRequest request, Long productId);
    Page<ProductSummary> searchProducts(SearchProductRequest search, Pageable pageable);
    ProductSummary resetProductPrice(PriceDeltaRequest priceDeltaRequest, Long productId);
    ProductSummary getProductByNameAndCategory(FindProductRequest findProductRequest);
    void deleteProduct(Long productId);
}
