package com.Ali.Store.App.repository.checkout;

import com.Ali.Store.App.entities.checkout.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryOrder extends JpaRepository<Orders, Long> {
}
