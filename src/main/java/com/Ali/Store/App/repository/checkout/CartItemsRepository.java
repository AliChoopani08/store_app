package com.Ali.Store.App.repository.checkout;

import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.entities.checkout.CartItem;
import com.Ali.Store.App.entities.productAndCategory.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByProduct(Product product);

    @Query("""
            SELECT new com.Ali.Store.App.dto.checkout.response.CartItemDto
            (ci.id, p.id, p.name, ca.name, p.price * ci.quantity, ci.quantity)
            FROM Users u
            JOIN u.cart c
            JOIN c.cartItems ci
            JOIN ci.product p
            JOIN p.category ca
            WHERE u.id = :userId
            """)
    List<CartItemDto> findUserCartItemsDetails(@Param("userId") Long userId); // I only want CartItems, Not All Users and Its lazy objects


    void deleteAllByCartId(Long cartId);
}
