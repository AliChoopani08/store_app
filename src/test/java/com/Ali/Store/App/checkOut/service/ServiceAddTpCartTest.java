package com.Ali.Store.App.checkOut.service;

import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.dto.checkout.response.UserCartDetailsDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItem;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.checkout.CartRepository;
import com.Ali.Store.App.repository.checkout.CartItemsRepository;
import com.Ali.Store.App.repository.productAndCategory.ProductRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.service.checkOut.cart.CartServiceImpl;
import com.Ali.Store.App.service.product.ItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import static com.Ali.Store.App.testHelpers.WhenHelper.whenHelper;
import static com.Ali.Store.App.service.product.ItemStatus.CREATED;
import static java.util.List.of;
import static java.util.Optional.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ServiceAddTpCartTest {

    @Mock
    private UserRepository repositoryUser;
    @Mock
    private ProductRepository repositoryProduct;
    @Mock
    private CartItemsRepository repositoryCartItems;
    @Mock
    private CartRepository repositoryCart;
    @InjectMocks
    private CartServiceImpl serviceCart;

    private Users fakeUser;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
         fakeUser = Users.builder()
                 .id(5L)
                 .username("09123456789")
                 .build();

         product = Product.builder()
                 .id(3L)
                 .name("Zar Macaron")
                 .price(new BigDecimal("2000"))
                 .quantity(10)
                 .isAvailable(true)
                 .build();

         cart = Cart.builder()
                 .id(2L)
                 .build();
         fakeUser.addCart(cart);

        CartItem cartItem = CartItem.builder()
                .id(2L)
                .quantity(3)
                .build();
        cartItem.addProduct(product);

        cart.addItems(cartItem);
    }

    @Test
    void add_Products_to_cart() {
        AddToCartRequest orderItemsRequest = new AddToCartRequest(product.getId(), 3);
        final CartItemDto expectedCartItemDto = getCartItemDto();
        UserCartDetailsDto expectedUserCartDetails = UserCartDetailsDto.builder()
                .userid(fakeUser.getId())
                .cartItemsDto(of(expectedCartItemDto))
                .build();

        whenHelper(repositoryUser.findById(any(Long.class)), Optional.of(fakeUser));
        whenHelper(repositoryProduct.findById(any(Long.class)), Optional.of(product));
        whenHelper(repositoryCart.findByUserId(any(Long.class)), Optional.of(cart));
        whenHelper(repositoryCartItems.findByProduct(any(Product.class)), empty()); // Assume this product doesn't exist in the cart
        whenHelper(repositoryCart.save(any(Cart.class)), cart);
        whenHelper(repositoryCartItems.findUserCartItemsDetails(any(Long.class)), of(expectedCartItemDto));

        final Map<String, Object> savedCart = serviceCart.addToCart(fakeUser.getId(), orderItemsRequest);

        assertThat(savedCart)
                .extractingByKey("status")
                .isInstanceOf(ItemStatus.class)
                .isEqualTo(CREATED);

        assertThat(savedCart)
                .extractingByKey("Cart details")
                .isInstanceOf(UserCartDetailsDto.class)
                .isEqualTo(expectedUserCartDetails);
    }

    private static CartItemDto getCartItemDto() {
        return CartItemDto.builder()
                .id(2L)
                .productId(3L)
                .productName("Zar Macaron")
                .productCategory("Food")
                .quantity(3)
                .totalProductPrice(new BigDecimal("6000"))
                .build();
    }
}
