package com.Ali.Store.App.repository.productAndCategory;

import com.Ali.Store.App.dto.product.response.ProductDtoForCartItems;
import com.Ali.Store.App.entities.productAndCategory.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryProduct extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.name = :name AND p.category.name = :category")
    Optional<Product> findByNameAndCategoryIgnoreCase(@Param("name") String name, @Param("category") String category);

    Optional<List<Product>> findBySlug(String slug);

    @Query("""
            SELECT new com.Ali.Store.App.dto.product.response.ProductDtoForCartItems
            (p.id, p.name, p.price, p.isAvailable)
            FROM Product p WHERE p.id IN :productIds
            """)
    List<ProductDtoForCartItems> productDetailForCartItems(@Param("productIds") List<Long> productIds);
}
