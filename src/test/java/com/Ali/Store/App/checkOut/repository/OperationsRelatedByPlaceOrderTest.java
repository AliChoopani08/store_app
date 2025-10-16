package com.Ali.Store.App.checkOut.repository;

import com.Ali.Store.App.dto.checkout.response.CartItemDto;
import com.Ali.Store.App.dto.checkout.response.OrderItemDetailsDto;
import com.Ali.Store.App.entities.checkout.Cart;
import com.Ali.Store.App.entities.checkout.CartItems;
import com.Ali.Store.App.entities.checkout.OrderItems;
import com.Ali.Store.App.entities.checkout.Orders;
import com.Ali.Store.App.entities.productAndCategory.Category;
import com.Ali.Store.App.entities.productAndCategory.Product;
import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.repository.checkout.RepositoryCart;
import com.Ali.Store.App.repository.checkout.RepositoryCartItems;
import com.Ali.Store.App.repository.checkout.RepositoryOrder;
import com.Ali.Store.App.repository.checkout.RepositoryOrderItems;
import com.Ali.Store.App.repository.productAndCategory.RepositoryCategory;
import com.Ali.Store.App.repository.productAndCategory.RepositoryProduct;
import com.Ali.Store.App.repository.userAndProfileUser.RepositoryUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@DataJpaTest
public class OperationsRelatedByPlaceOrderTest {


    @Autowired
    private RepositoryUser repositoryUser;
    @Autowired
    private RepositoryCart repositoryCart;
    @Autowired
    private RepositoryCategory repositoryCategory;
    @Autowired
    private RepositoryProduct repositoryProduct;
    @Autowired
    private RepositoryOrder repositoryOrder;
    @Autowired
    private RepositoryCartItems repositoryCartItems;
    @Autowired
    private RepositoryOrderItems repositoryOrderItems;

    private final List<Product> savedProducts = new LinkedList<>();
    private Users savedUser;

    @BeforeEach
    void add_some_products_before_operations() {
        Users user = new Users( "09123456789", "Ali425345");
        final ProfileUser profileUser = new ProfileUser("Ali", "09123456789", null, LocalDate.of(2005, 4, 23));
        user.setProfileFields(profileUser);
        user.setCreatedAt(now());
        savedUser = repositoryUser.save(user);

        Category food = new Category("Food");
        repositoryCategory.save(food);

        List<Product> products = List.of(new Product("Fish Stew With Rice", 3000, 15, true, "fish-stew-with-rice"),
                new Product("Dip Eggplant With Pickle", 2500, 6, true, "dip-eggplant-with-pickle"));

        products.forEach(food::addProduct);

        savedProducts.addAll(repositoryProduct.saveAll(products));
    }

    @Test
    void find_user_cartItems_detail() {
        CartItems cartItems = new CartItems();
        cartItems.setProduct(savedProducts.getFirst());
        cartItems.setQuantity(3);

        final Cart cart = new Cart();
        savedUser.addCart(cart);
        cart.addCartItems(List.of(cartItems));

        repositoryCart.save(cart);

        final List<CartItemDto> cartItemsOfFoundUser = repositoryCartItems.findRequestedUserCartItemsDetails(1L);

        System.out.println(cartItemsOfFoundUser + "  the first");

        assertThat(cartItemsOfFoundUser)
                .isEqualTo(List.of(new CartItemDto(1L, 1L, "Fish Stew With Rice", "Food",3000, 3)));
    }

    @Test
    void find_cart_items_detail_for_response() {
        CartItems cartItem1 = new CartItems();
        cartItem1.setProduct(savedProducts.getFirst());
        cartItem1.setQuantity(2);

        final Cart cart = new Cart();
        savedUser.addCart(cart);

        final CartItems cartItem2 = new CartItems();
        cartItem2.setProduct(savedProducts.get(1));
        cartItem2.setQuantity(4);

        cart.addCartItems(List.of(cartItem1, cartItem2));

        repositoryCart.save(cart);

        final List<CartItemDto> itemsDetailOfFoundUser = repositoryCartItems.findRequestedUserCartItemsDetails(1L);

        System.out.println(itemsDetailOfFoundUser  + "  the second");

        List<CartItemDto> exceptedCartItemsDetail = List.of(new CartItemDto(1L,1L,"Fish Stew With Rice","Food",3000,2)
                            , new CartItemDto(2L,2L,"Dip Eggplant With Pickle", "Food", 2500, 4));
        assertThat(itemsDetailOfFoundUser)
                .isEqualTo(exceptedCartItemsDetail);
    }

    @Test
    void findOrderItemsDetailForDto() {
        final Orders order1 = new Orders();
        savedUser.addOrder(order1);
        final OrderItems orderItem1 = new OrderItems();
        orderItem1.setProduct(savedProducts.getFirst());
        orderItem1.setPrice(savedProducts.getFirst().getPrice());
        orderItem1.setQuantity(5);

        order1.setBidirectionalRelationBetweenOrderAndOrderItems(List.of(orderItem1));
        order1.setCreatedAt(now());
        repositoryOrder.save(order1);

        final Orders order2 = new Orders();
        savedUser.addOrder(order2);

        final OrderItems orderItem2 = new OrderItems();
        orderItem2.setProduct(savedProducts.get(1));
        orderItem2.setPrice(savedProducts.get(1).getPrice());
        orderItem2.setQuantity(3);

        order2.setBidirectionalRelationBetweenOrderAndOrderItems(List.of(orderItem2));
        order2.setCreatedAt(now());
        repositoryOrder.save(order2);

        final List<OrderItemDetailsDto> userOrderItemsDetail2 = repositoryOrderItems.findRequestedUserOrderItemsDetails(1L, 2L);
        assertThat(userOrderItemsDetail2)
                .extracting(OrderItemDetailsDto::id, OrderItemDetailsDto::productName, OrderItemDetailsDto::quantity)
                .containsExactly(tuple(2L, "Dip Eggplant With Pickle", 3));

    }
}
