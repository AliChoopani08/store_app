package com.Ali.Store.App.checkOut.service;

import com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserOrderDetailsDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItem;
import com.Ali.Store.App.entities.checkout.OrderItem;
import com.Ali.Store.App.entities.checkout.Orders;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.checkout.CartRepository;
import com.Ali.Store.App.repository.checkout.CartItemsRepository;
import com.Ali.Store.App.repository.checkout.OrderRepository;
import com.Ali.Store.App.repository.checkout.OrderItemsRepository;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.service.checkOut.order.OrderServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static com.Ali.Store.App.testHelpers.WhenHelper.whenHelper;
import static java.util.List.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceOrderImplTest {

    @Mock
    private OrderRepository repositoryOrder;
    @Mock
    private UserRepository repositoryUser;
    @Mock
    private CartRepository repositoryCart;
    @Mock
    private OrderItemsRepository repositoryOrderItems;
    @Mock
    private CartItemsRepository repositoryCartItems;
    @InjectMocks
    private OrderServiceImpl serviceOrder;

    private Users user;
    private Cart cart;
    private Orders order;

    @BeforeEach
    void setUp() {
        user = createUser();
        Product product = createProduct();

        cart = Cart.builder()
                .id(3L)
                .build();
        user.addCart(cart);

        createCartItem(product, cart);

        order = Orders.builder()
                .id(5L)
                .totalPrice(new BigDecimal("4000"))
                .build();
        user.addOrder(order);

        createOrderItem(order);
    }



    @Test
    void shouldCreateOrder_whenUserAndCartExist() {
        OrderItemDetailsDto expectedOrderItemDetails = createExpectedOrderItemDetails();

        whenHelper(repositoryCart.findByUserId(any(Long.class)), Optional.of(cart));
        whenHelper(repositoryOrder.save(any(Orders.class)), order);
        whenHelper(repositoryOrderItems.findUserOrderItemsDetails(anyLong(), anyLong()), of(expectedOrderItemDetails));

        final UserOrderDetailsDto userOrderDetails = serviceOrder.createOrder(user.getId());

        Assertions.assertThat(userOrderDetails)
                .extracting(o -> o.orderItemDto().getFirst().productName(),
                        o -> o.orderItemDto().getFirst().productPrice().intValue())
                .containsExactly("Sesame Oil", 2000);
    }

    private Users createUser() {
        Users user = Users.builder()
                .id(1L)
                .username("09876574563")
                .build();

                when(repositoryUser.findById(anyLong()))
                .thenReturn(Optional.of(user));
        return user;
    }
    private Product createProduct() {
        return Product.builder()
                .id(2L)
                .name("Sesame Oil")
                .price(new BigDecimal("2000"))
                .quantity(5)
                .isAvailable(true)
                .build();
    }
    private void createOrderItem(Orders order) {
        OrderItem orderItem = OrderItem.builder()
                .id(6L)
                .price(new BigDecimal("2000"))
                .quantity(2)
                .build();
        order.addItem(orderItem);
    }

    private void createCartItem(Product product, Cart cart) {
        CartItem cartItem = CartItem.builder()
                .id(4L)
                .quantity(2)
                .build();
        cartItem.addProduct(product);
        cart.addItems(cartItem);
    }
    private static OrderItemDetailsDto createExpectedOrderItemDetails() {
        return OrderItemDetailsDto.builder()
                .id(7L)
                .productId(2L)
                .productName("Sesame Oil")
                .quantity(2)
                .productPrice(new BigDecimal("2000"))
                .build();
    }
}
