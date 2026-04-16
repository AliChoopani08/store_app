package com.Ali.Store.App.checkOut.repository;

import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItem;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.checkout.NotFoundCart;
import com.Ali.Store.App.repository.checkout.CartRepository;
import com.Ali.Store.App.repository.checkout.CartItemsRepository;
import com.Ali.Store.App.repository.productAndCategory.CategoryRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
@ActiveProfiles("test")
public class CartItemsRepositoryTest {
    @Autowired
    private ProductRepository repositoryProduct;
    @Autowired
    private UserRepository repositoryUser;
    @Autowired
    private CartItemsRepository repositoryCartItems;
    @Autowired
    private CartRepository repositoryCart;
    @Autowired
    private CategoryRepository repositoryCategory;

    private Users savedUser;
    private final List<Product> getAllSavedProducts = new LinkedList<>();

    @BeforeEach
    void setUp() {
        repositoryUser.deleteAll();
        repositoryProduct.deleteAll();
        repositoryCategory.deleteAll();
        repositoryCart.deleteAll();
        repositoryCartItems.deleteAll();

        final Users user = createUser();
        savedUser = createUserCart(user);
        createCategoryAndAddProducts();
    }

    private Users createUserCart(Users user) {
        user.addCart(new Cart());
        return repositoryUser.save(user);
    }

    private Users createUser() {
        return Users.builder()
                .username("09534526353")
                .createdAt(now())
                .build();
    }

    @Test
    void shouldFindUserCartItemsDetails_whenUserExists() {
        final Long userId = savedUser.getId();

        addAndSaveCartItems(userId, getDefaultCartItems());

        final List<CartItemDto> foundProfileCartItemsDetails = repositoryCartItems.findUserCartItemsDetails(userId);

        assertThat(foundProfileCartItemsDetails)
                .extracting(CartItemDto::productName, CartItemDto::productCategory, CartItemDto::quantity, i -> i.totalProductPrice().intValue())
                .containsExactly(tuple("Fish Stew With Rice", "Food", 3, 9000));

    }

    private Product createProducts() {
        return Product.builder()
                .name("Fish Stew With Rice")
                .price(new BigDecimal("3000"))
                .isAvailable(true)
                .build();
    }

    private void createCategoryAndAddProducts() {
        Category food = new Category("Food");

        final Product product = createProducts();

        food.addProduct(product);
        repositoryCategory.save(food);
    }

    private CartItem getDefaultCartItems() {
        getAllSavedProducts.addAll(repositoryProduct.findAll());

        return CartItem.builder()
                .product(getAllSavedProducts.getFirst())
                .quantity(3)
                .build();
    }

    private void addAndSaveCartItems(Long userId, CartItem item) {
        final Cart cart = getCartByUserId(userId);

        cart.addItems(item);
        repositoryCart.save(cart);
    }

    private Cart getCartByUserId(Long userId) {
        return repositoryCart.findByUserId(userId)
                .orElseThrow(() -> new NotFoundCart(userId));
    }
}
