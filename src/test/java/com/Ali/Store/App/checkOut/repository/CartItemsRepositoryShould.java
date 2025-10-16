package com.Ali.Store.App.checkOut.repository;

import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItems;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.checkout.RepositoryCart;
import com.Ali.Store.App.repository.checkout.RepositoryCartItems;
import com.Ali.Store.App.repository.productAndCategory.RepositoryCategory;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.LinkedList;
import java.util.List;
import static com.Ali.Store.App.entities.userAndProfileUser.Role.ROLE_USER;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@ActiveProfiles("test")
public class CartItemsRepositoryShould {
    @Autowired
    private RepositoryProduct repositoryProduct;
    @Autowired
    private RepositoryUser repositoryUser;
    @Autowired
    private RepositoryCartItems repositoryCartItems;
    @Autowired
    private RepositoryCart repositoryCart;
    @Autowired
    private RepositoryCategory repositoryCategory;

    private Users savedUser;
    private final List<Product> savedProducts = new LinkedList<>();
    @BeforeEach
    void setUp() {
        repositoryUser.deleteAll();
        repositoryProduct.deleteAll();
        repositoryCategory.deleteAll();
        repositoryCart.deleteAll();

        final Users user = new Users("09534526353", "Asgd1423435", ROLE_USER);
        user.setCreatedAt(now());
        savedUser = repositoryUser.save(user);

        Category food = new Category("Food");
        repositoryCategory.save(food);
        Category electronic = new Category("Electronic");
        repositoryCategory.save(electronic);

        List<Product> products = List.of(new Product("Fish Stew With Rice", 3000, 15, true, "fish-stew-with-rice"),
                new Product("Iphone 13 Pro Max", 2500, 6, true, "iphone-13-pro-max"));

        food.addProduct(products.getFirst());
        electronic.addProduct(products.getLast());

        savedProducts.addAll(repositoryProduct.saveAll(products));
    }

    @Test
    void findUserCartItemsDetail() {
        final Cart cart = new Cart();
        savedUser.addCart(cart);
        List<CartItems> cartItems = List.of(new CartItems(savedProducts.get(1), 3), new CartItems(savedProducts.getFirst(), 5));
        cart.addCartItems(cartItems);

        repositoryCart.save(cart);

        final List<CartItemDto> foundProfileCartItemsDetails = repositoryCartItems.findRequestedUserCartItemsDetails(1L);

        assertThat(foundProfileCartItemsDetails)
                .extracting(CartItemDto::id, CartItemDto::productId, CartItemDto::productCategory, CartItemDto::quantity, CartItemDto::totalPriceOfProduct)
                .containsExactly(tuple(1L, 2L,"Electronic", 3, 7500),
                        tuple(2L, 1L,"Food", 5, 15000));

    }

    @Test
    void delete_product_from_cart() {
        final Cart cart = new Cart();
        savedUser.addCart(cart);
        List<CartItems> cartItems = List.of(new CartItems(savedProducts.getFirst(), 3), new CartItems(savedProducts.getLast(), 2));
        cart.addCartItems(cartItems);

        repositoryCart.save(cart);

        final List<CartItemDto> userCartDetailsBeforeDelete = repositoryCartItems.findRequestedUserCartItemsDetails(1L);

        assertThat(userCartDetailsBeforeDelete)
                .extracting(CartItemDto::productId, CartItemDto::productName, CartItemDto::productCategory)
                        .containsExactly(tuple(1L, "Fish Stew With Rice", "Food"),
                                tuple(2L, "Iphone 13 Pro Max", "Electronic"));

        repositoryCartItems.deleteUserCartItemById(1L, 2L);

        final List<CartItemDto> userCartDetailsAfterDelete = repositoryCartItems.findRequestedUserCartItemsDetails(1L);

        assertThat(userCartDetailsAfterDelete)
                .extracting(CartItemDto::productId, CartItemDto::productName, CartItemDto::productCategory)
                .containsExactly(tuple(1L, "Fish Stew With Rice", "Food"));
    }

    @Test
    void delete_all_cart_items_by_user_id() {
        final Cart cart = new Cart();
        savedUser.addCart(cart);
        List<CartItems> cartItems = List.of(new CartItems(savedProducts.getFirst(), 2), new CartItems(savedProducts.getLast(), 3));
        cart.setCartItems(cartItems);
        repositoryCart.save(cart);

        repositoryCartItems.deleteAllByUserId(savedUser.getId());

        final List<CartItemDto> resultMethod = repositoryCartItems.findRequestedUserCartItemsDetails(savedUser.getId());

        assertThat(resultMethod).isEmpty();
    }
}
