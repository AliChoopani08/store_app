package com.Ali.Store.App.entities.productAndCategory;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@ToString(of = "name")
@EqualsAndHashCode(of = "name")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "category", fetch = LAZY, cascade = ALL)
    private List<Product> products = new LinkedList<>();

    public Category(String kind) {
        this.name = kind;
    }

    public void addProduct(Product product) {
        if (this.getProducts() == null) {
            this.products = new ArrayList<>();
        }
        this.getProducts().add(product);
        product.setCategory(this);
    }

}
