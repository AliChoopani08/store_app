package com.Ali.Store.App.repository.checkout;

import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.entities.checkout.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemsRepository extends JpaRepository<CartItem, Long> {
    @Query("""
            SELECT ci
            FROM CartItem ci
            join ci.cart c
            join ci.product p
            WHERE  c.id = :cartId AND p.id = :productId
            """)
    Optional<CartItem> findByCartAndProduct(@Param("cartId") Long cartId, @Param("productId") Long productId);

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

    List<CartItem> findByCartId(@Param("cartId") Long cartId);

    @Query("""
            SELECT ci 
            FROM CartItem ci
            JOIN ci.cart c
            JOIN c.user u
            WHERE u.id = :userId AND ci.id = :itemId
            """)
    Optional<CartItem> findByUserIdAndItemId(@Param("userId") Long userId, @Param("itemId") Long itemId);

    @Modifying
    @Query("""
            DELETE FROM CartItem ci
            WHERE ci.cart.id = :cartId
            """)
    void deleteByCartId(Long cartId);
}
