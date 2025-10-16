package com.Ali.Store.App.checkOut.service;

import com.Ali.Store.App.dto.checkout.request.AddToCartRequest;
import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.dto.checkout.response.UserCartDetailsDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItems;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Role;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.checkout.RepositoryCart;
import com.Ali.Store.App.repository.checkout.RepositoryCartItems;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import com.Ali.Store.App.service.checkOut.cart.ServiceCartImpl;
import com.Ali.Store.App.service.product.ItemStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static com.Ali.Store.App.service.product.ItemStatus.CREATED;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServiceAddTpCartShould {

    @Mock
    private RepositoryUser repositoryUser;
    @Mock
    private RepositoryProduct repositoryProduct;
    @Mock
    private RepositoryCartItems repositoryCartItems;
    @Mock
    private RepositoryCart repositoryCart;
    @InjectMocks
    private ServiceCartImpl serviceCart;

    private Users fakeUser;

    @BeforeEach
    void setUp() {
         fakeUser = new Users(5L, "09123456789", Role.ROLE_USER);

        ProfileUser fakeProfile = new ProfileUser("Ali", "09123456789", null, LocalDate.of(2008,10,23));
        fakeProfile.setId(5L);
        fakeUser.setProfileFields(fakeProfile);

        when(repositoryUser.findById(any(Long.class)))
                .thenReturn(Optional.ofNullable(fakeUser));
    }

    @Test
    void add_Products_to_cart() {
        final Product product = new Product("Zar Macaron", 2000);
        product.setId(3L);
        product.setAvailable(true);
        product.setQuantity(10);
        AddToCartRequest orderItemsRequest = new AddToCartRequest(3L, 3);
        final Cart cart = new Cart();
        cart.setId(2L);
        fakeUser.addCart(cart);

        List<CartItems> cartItemsList = new LinkedList<>();
        cartItemsList.add(new CartItems(2L, product, cart, 3));
        cart.addCartItems(cartItemsList);

        when(repositoryProduct.findById(any(Long.class)))
                .thenReturn(of(product));
        when(repositoryCart.findByUserId(any(Long.class)))
                .thenReturn(of(cart));
        when(repositoryCartItems.findByProduct(any(Product.class)))
                .thenReturn(empty()); // Assume this product doesn't exist in the cart
        when(repositoryCart.save(any(Cart.class)))
                .thenReturn(cart);
        final List<CartItemDto> cartItemDto = List.of(new CartItemDto(2L, 3L, "Zar Macaron", "Food", 6000, 3), new CartItemDto(3L, 5L, "Iphone 13 pro max", "Mobile", 11500, 3));
        when(repositoryCartItems.findRequestedUserCartItemsDetails(any(Long.class)))
                .thenAnswer(_ -> cartItemDto);

        final Map<String, Object> savedCart = serviceCart.addToCart(5L, orderItemsRequest);

        assertThat(savedCart)
                .containsKey("status")
                .extractingByKey("status")
                .isInstanceOf(ItemStatus.class)
                .isEqualTo(CREATED);

        final UserCartDetailsDto expectedUserCartDetails = new UserCartDetailsDto(5L, List.of(new CartItemDto(2L, 3L, "Zar Macaron", "Food", 6000, 3)
                , new CartItemDto(3L, 5L, "Iphone 13 pro max", "Mobile", 11500, 3)), 17500);
        assertThat(savedCart)
                .containsKey("Cart details")
                .extractingByKey("Cart details")
                .isInstanceOf(UserCartDetailsDto.class)
                .isEqualTo(expectedUserCartDetails);
    }
}
