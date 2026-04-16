package com.Ali.Store.App.entities.productAndCategory;

import com.Ali.Store.App.entities.checkout.CartItem;
import com.Ali.Store.App.entities.checkout.OrderItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "products" ,
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "category_id"}))
@Getter
@Setter
@ToString(of = {"name", "price", "category", "quantity", "isAvailable"})
@EqualsAndHashCode(of = {"name", "price", "category"})
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Product {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Positive
    private BigDecimal price;
    private int quantity = 1;
    @Column(nullable = false)
    private boolean isAvailable;
    @Column(unique = true)
    private String slug;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product",fetch = LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", fetch = LAZY, cascade = ALL)
    private List<OrderItem> orderItems = new ArrayList<>();



    public List<OrderItem> getOrderItems() {
        return orderItems = new ArrayList<>();
    }

    @Version // -> for Optimistic Locking
    private Long version;

    public List<CartItem> getCartItems() {
        return cartItems = new ArrayList<>();
    }

    public Product(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public Product(Long id, String name, BigDecimal price, int quantity, boolean isAvailable, String slug) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
        this.slug = slug;
    }

    public Product(String name, BigDecimal price, int quantity, boolean isAvailable, String slug) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
        this.slug = slug;
    }
}
