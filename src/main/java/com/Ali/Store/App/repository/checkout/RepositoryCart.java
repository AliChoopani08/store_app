package com.Ali.Store.App.repository.checkout;

import com.Ali.Store.App.entities.checkout.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositoryCart extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);
}
