package com.Ali.Store.App.service.product;

import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;


public class ProductSpecification {


    public static Specification<Product> withName(String name) {
        return ((root, __, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like
                        (criteriaBuilder.lower
                                (root.get("name")), "%" + name.toLowerCase() + "%"));
    }

    public static Specification<Product> withCategory(String category) {
        return (root, __, criteriaBuilder) -> {
            if (category == null) {
                return null;
            }
            Join<Product, Category> categoryJoin = root.join("category");
            return criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("name")), "%" + category.toLowerCase() + "%");
        };
    }

    public static Specification<Product> withMaxPrice(Integer maxPrice) {
        return ((root, __, criteriaBuilder) ->
                maxPrice == null ? null : criteriaBuilder
                        .lessThanOrEqualTo(root.get("price"), maxPrice));
    }

   public static Specification<Product> withMinPrice(Integer minPrice) {
        return ((root, __, criteriaBuilder) ->
                minPrice == null ? null : criteriaBuilder
                        .greaterThanOrEqualTo(root.get("price"), minPrice));
   }
   public static Specification<Product> isAvailable(Boolean statue) {
        return ((root, __, criteriaBuilder) ->
                statue == null ? null : criteriaBuilder
                        .equal(root.get("isAvailable"), statue));
   }
    }
