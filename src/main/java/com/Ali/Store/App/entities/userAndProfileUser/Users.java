package com.Ali.Store.App.entities.userAndProfileUser;

import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.Orders;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.ALL;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@ToString(of = {"username", "password"})
@EqualsAndHashCode(of = {"username", "password"})
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Users {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "user",
    cascade = ALL,
    orphanRemoval = true)
    private ProfileUser profile;

    @OneToMany(mappedBy = "user", fetch = LAZY, cascade = ALL)
    private List<RefreshToken> refreshTokens = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = ALL, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", cascade = ALL, fetch = LAZY)
   private List<Orders> orders = new ArrayList<>();

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;
    private boolean status;


    public Users(String username, String password) { // for SingUp
        this.username = username;
        this.password = password;
    }

    public Users(String username, String password,Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public Users(Long id, String username, Role role) {
        this.id = id;
        this.username = username;
        this.role = role;
    }

    public void setProfileFields(ProfileUser profile) {
        profile.setUsernameOnCorrectFields(this);
        this.setProfile(profile);
        profile.setUser(this);
    }

    public void addOrder(Orders order) {
        if (this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.getOrders().add(order);
        order.setUser(this);
    }

    public void addCart(Cart cart) {
        this.setCart(cart);
        cart.setUser(this);
    }

    public void addRefreshToken(RefreshToken refreshToken) {
        if (this.getRefreshTokens() == null) {
            this.refreshTokens = new ArrayList<>();
        }
        this.getRefreshTokens().add(refreshToken);
        refreshToken.setUser(this);
    }


}
