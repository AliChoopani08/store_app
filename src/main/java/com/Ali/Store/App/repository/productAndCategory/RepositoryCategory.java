package com.Ali.Store.App.repository.productAndCategory;

import com.Ali.Store.App.entities.productAndCategory.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryCategory extends JpaRepository<Category, Long> {
    Optional<Category> findByNameIgnoreCase(String name);
}
