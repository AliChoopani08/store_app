package com.Ali.Store.App.repository.checkout;

import com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto;
import com.Ali.Store.App.entities.checkout.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
            SELECT new com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto
            (oi.id, p.id, p.name, p.slug, p.price * oi.quantity, oi.quantity)
            FROM Users u
            JOIN u.orders o
            JOIN o.orderItems oi
            JOIN oi.product p
            WHERE u.id = :userId AND o.id = :orderId
            """)
    List<OrderItemDetailsDto> findUserOrderItemsDetails(@Param("userId") Long userId, @Param("orderId") Long orderId);
}
