package com.Ali.Store.App.entities.checkout;

import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.service.checkOut.order.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Orders {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    private Integer totalPrice;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL
    , fetch = LAZY, orphanRemoval = true)
    private List<OrderItems> orderItems = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public void setBidirectionalRelationBetweenOrderAndOrderItems(List<OrderItems> orderItems) {
        this.setOrderItems(orderItems);
        orderItems.forEach(item -> item.setOrder(this));
    }

}
