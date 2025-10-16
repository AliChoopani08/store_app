package com.Ali.Store.App.checkOut.service;

import com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto;
import com.Ali.Store.App.dto.checkout.response.UserOrderDetailsDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItems;
import com.Ali.Store.App.entities.checkout.OrderItems;
import com.Ali.Store.App.entities.checkout.Orders;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Role;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.checkout.RepositoryCart;
import com.Ali.Store.App.repository.checkout.RepositoryCartItems;
import com.Ali.Store.App.repository.checkout.RepositoryOrder;
import com.Ali.Store.App.repository.checkout.RepositoryOrderItems;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.Ali.Store.App.service.checkOut.order.ServiceOrderImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceCreateOrderShould {

    @Mock
    private RepositoryOrder repositoryOrder;
    @Mock
    private RepositoryUser repositoryUser;
    @Mock
    private RepositoryProduct repositoryProduct;
    @Mock
    private RepositoryCart repositoryCart;
    @Mock
    private RepositoryCartItems repositoryCartItems;
    @Mock
    private RepositoryOrderItems repositoryOrderItems;
    @InjectMocks
    private ServiceOrderImpl serviceOrder;

    private List<Product> products;
    private Cart cart;
    private Orders order;

    @BeforeEach
    void setUp() {
        Users mockCurrentUser = getMockCurrentUser();
        products = List.of(new Product(4L, "Sesame Oil", 2000, 5, true, "sesame-oil"),
                new Product(12L, "Garlic Shampoo", 3000, 12,true, "garlic-shampoo"));
        cart = new Cart();
        cart.setId(2L);
        List<CartItems> cartItems = List.of(new CartItems(1L, products.getFirst(), cart, 3) ,
                new CartItems(2L, products.get(1), cart, 6));
        cart.addCartItems(cartItems);
        mockCurrentUser.addCart(cart);

        order = new Orders();
        order.setId(1L);
        mockCurrentUser.addOrder(order);
        order.setOrderItems(List.of(new OrderItems(1L, order, products.getFirst(), 2000, 3)
                , new OrderItems(2L,order,products.get(1), 3000, 6)));
        order.setTotalPrice(24000);
    }

    @Test
    void create_an_order() {
        List<OrderItemDetailsDto> orderItemsDetails = List.of(new OrderItemDetailsDto(1L, 4L, "Sesame Oil", "sesame-oil",2000, 3),
                new OrderItemDetailsDto(1L, 12L, "Garlic Shampoo", "garlic-shampoo",3000, 6));

        when(repositoryCart.findByUserId(any(Long.class)))
                .thenReturn(ofNullable(cart));
        when(repositoryProduct.findAllById(any(Iterable.class)))
                .thenReturn(products);
        when(repositoryOrder.save(any(Orders.class)))
                .thenReturn(order);
        when(repositoryProduct.saveAll(any(Iterable.class)))
                .thenAnswer(_ -> List.of(
                        new Product(4L, "Sesame Oil", 2000, 2, true, "sesame-oil"),
                        new Product(12L, "Garlic Shampoo", 3000, 6,true, "garlic-shampoo")
                ));
        when(repositoryOrderItems.findRequestedUserOrderItemsDetails(any(Long.class), any(Long.class)))
                .thenReturn(orderItemsDetails);

        final UserOrderDetailsDto userOrderDetails = serviceOrder.createOrder(3L);

        Assertions.assertThat(userOrderDetails)
                .extracting(o -> o.orderItemDto().getFirst().productSlug(), o -> o.orderItemDto().getLast().productSlug())
                .containsExactly("sesame-oil", "garlic-shampoo");
    }

    private Users getMockCurrentUser() {
        Users currentUser = new Users(3L, "09876574563", Role.ROLE_USER);

        ProfileUser fakeProfile = new ProfileUser("Ali", "09876574563", null, LocalDate.of(2009, 2,23));
        fakeProfile.setId(3L);
        currentUser.setProfileFields(fakeProfile);

        when(repositoryUser.findById(any(Long.class)))
                .thenReturn(of(currentUser));
        return currentUser;
    }
}
