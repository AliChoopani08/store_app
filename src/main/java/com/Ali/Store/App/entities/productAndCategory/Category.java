package com.Ali.Store.App.entities.productAndCategory;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@ToString(of = "name")
@EqualsAndHashCode(of = "name")
@NoArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "category", fetch = LAZY)
    private List<Product> products = new LinkedList<>();

    public Category(String kind) {
        this.name = kind;
    }

    public void addProduct(Product product) {
        this.getProducts().add(product);
        product.setCategory(this);
    }

}
