package com.Ali.Store.App.repository.checkout;

import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.entities.checkout.CartItems;
import com.Ali.Store.App.entities.productAndCategory.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositoryCartItems extends JpaRepository<CartItems, Long> {
    Optional<CartItems> findByProduct(Product product);

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
    List<CartItemDto> findRequestedUserCartItemsDetails(@Param("userId") Long userId); // I only want CartItems, Not All Users and Its lazy objects


    @Modifying //for void methods
    @Query("""
            DELETE FROM CartItems ci
            WHERE ci.cart.user.id = :userId AND ci.id = :cartItemId
            """)
    void deleteUserCartItemById(@Param("userId") Long userId, @Param("cartItemId") Long cartItemId);

    @Modifying
    @Query("""
            DELETE FROM CartItems ci\s
            WHERE ci.cart.user.id = :userid
           \s""")
    void deleteAllByUserId(@Param("userid") Long userId);
}
