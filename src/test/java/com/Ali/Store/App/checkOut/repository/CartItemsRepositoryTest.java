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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.List.of;
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

    private Long userId;
    private Long cartId;
    private final List<Product> getAllSavedProducts = new LinkedList<>();

    @BeforeEach
    void setUp() {
        repositoryUser.deleteAll();
        repositoryProduct.deleteAll();
        repositoryCategory.deleteAll();
        repositoryCart.deleteAll();
        repositoryCartItems.deleteAll();

        final Users user = createUser();
        userId = createUserCart(user).getId();
        createCategoryAndAddProducts();
        cartId = addAndSaveCartItems(userId, getDefaultCartItems()).getId();
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

        addAndSaveCartItems(userId, getDefaultCartItems());

        final List<CartItemDto> foundProfileCartItemsDetails = repositoryCartItems.findUserCartItemsDetails(userId);

        assertThat(foundProfileCartItemsDetails.getFirst())
                .extracting(CartItemDto::productName, CartItemDto::productCategory, CartItemDto::quantity, i -> i.totalProductPrice().intValue())
                .containsExactly("Fish Stew With Rice", "Food", 3, 9000);

    }

    @Test
    void shouldFindCartItemsOfCartOfUser_whenExist() {
        final Product firstProduct = repositoryProduct.findAll().getFirst();

        final Optional<CartItem> foundItemByProductAndCart = repositoryCartItems.findByCartAndProduct(cartId, firstProduct.getId());

        foundItemByProductAndCart.ifPresent(i -> assertThat(i)
                .extracting(c -> c.getProduct().getName())
                .isEqualTo("Fish Stew With Rice"));
            }

    @Test
    void shouldDeleteAllByCartId_whenItemsExist() {
        repositoryCartItems.deleteByCartId(cartId);
        final List<CartItem> foundByCartId = repositoryCartItems.findByCartId(cartId);

        assertThat(foundByCartId)
                .isEmpty();

    }

    @Test
    @Transactional(readOnly = true)
    void shouldFind_byUserIdAndItemId() {
        List<CartItem> getAllSavedItems = repositoryCartItems.findAll();
        final Long itemId = getAllSavedItems.getLast().getId();

        final Optional<CartItem> foundItem = repositoryCartItems.findByUserIdAndItemId(userId, itemId);

        assertThat(foundItem.isPresent()).isTrue();
        foundItem.ifPresent(item -> assertThat(item)
                .extracting(i -> i.getProduct().getName(), CartItem::getQuantity)
                .containsExactly("Iranian Kebab With Delicious Rice", 6));
    }

    private List<Product> createProducts() {

        return of(Product.builder()
                .name("Fish Stew With Rice")
                .price(new BigDecimal("3000"))
                .isAvailable(true)
                .build(),
                Product.builder()
                        .name("Iranian Kebab With Delicious Rice")
                        .price(new BigDecimal("85000"))
                        .isAvailable(true)
                        .build());
    }

    private void createCategoryAndAddProducts() {
        Category food = new Category("Food");
        final List<Product> products = createProducts();

        products.forEach(food::addProduct);
        repositoryCategory.save(food);
    }

    private List<CartItem> getDefaultCartItems() {
        getAllSavedProducts.addAll(repositoryProduct.findAll());

        return of(CartItem.builder()
                .product(getAllSavedProducts.getFirst())
                .quantity(3)
                .build(),
                CartItem.builder()
                        .product(getAllSavedProducts.getLast())
                        .quantity(6)
                        .build());
    }

    private Cart addAndSaveCartItems(Long userId, List<CartItem> items) {
        final Cart cart = getCartByUserId(userId);

        items.forEach(cart::addItems);
        return repositoryCart.save(cart);
    }

    private Cart getCartByUserId(Long userId) {
        return repositoryCart.findByUserId(userId)
                .orElseThrow(() -> new NotFoundCart(userId));
    }
}
