package com.Ali.Store.App.repository.checkout;

import com.Ali.Store.App.entities.checkout.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByUserId(Long UserId);
}
