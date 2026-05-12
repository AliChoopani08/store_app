package com.Ali.Store.App.entities.checkout;

import com.Ali.Store.App.entities.userAndProfileUser.Users;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(of = {"id", "user"})
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = ALL, fetch = LAZY)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private Users user;

    public void addItems(CartItem item) {
        if (this.getCartItems() == null) {
            this.cartItems = new ArrayList<>();
        }
        this.getCartItems().add(item);
        item.setCart(this);
    }
}
