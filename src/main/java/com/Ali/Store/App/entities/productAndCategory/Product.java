package com.Ali.Store.App.entities.productAndCategory;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "products" ,
    uniqueConstraints = @UniqueConstraint(columnNames = {"name", "category_id"}))
@Getter
@Setter
@ToString(of = {"name", "price", "category", "quantity", "isAvailable"})
@EqualsAndHashCode(of = {"name", "price", "category"})
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Positive
    private int price;
    private int quantity = 1;
    @Column(nullable = false)
    private boolean isAvailable;
    @Column(nullable = false, unique = true)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Version // -> for Optimistic Locking
    private Long version;


    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public Product(Long id, String name, int price, int quantity, boolean isAvailable, String slug) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
        this.slug = slug;
    }

    public Product(String name, int price, int quantity, boolean isAvailable, String slug) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.isAvailable = isAvailable;
        this.slug = slug;
    }
}
