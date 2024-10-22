package sample.cafekiosk.spring.domain.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.IntegrationTestSupport;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.*;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

@Transactional
class OrderRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("주문 상태와 날짜를 조건으로 주문을 조회한다.")
    @Test
    void findOrdersBy() {
        // given
        OrderStatus targetStatus = OrderStatus.PAYMENT_COMPLETED;
        LocalDateTime targetOrderDateTime = LocalDateTime.of(2024, 10, 10, 0, 0, 0);
        Order order1 = createOrder(targetOrderDateTime, targetStatus);
        Order order2 = createOrder(targetOrderDateTime, targetStatus);

        Order order3 = createOrder(targetOrderDateTime.plusDays(1), targetStatus);
        Order order4 = createOrder(targetOrderDateTime.plusDays(2), targetStatus);

        Order order5 = createOrder(targetOrderDateTime, OrderStatus.INIT);
        Order order6 = createOrder(targetOrderDateTime, OrderStatus.INIT);
        orderRepository.saveAll(List.of(order1, order2, order3, order4, order5, order6));

        LocalDateTime startDateTime = targetOrderDateTime;
        LocalDateTime endDateTime = targetOrderDateTime.plusDays(1);

        // when
        List<Order> result = orderRepository.findOrdersBy(startDateTime, endDateTime, targetStatus);

        // then
        assertThat(result).hasSize(2)
                .extracting("totalPrice", "orderStatus")
                .containsExactlyInAnyOrder(
                        tuple(15_500, targetStatus),
                        tuple(15_500, targetStatus)
                );
    }

    private Order createOrder(LocalDateTime orderDateTime, OrderStatus orderStatus) {
        Product product1 = creatProduct("001", HANDMADE, SELLING, "아메리카노", 4000);
        Product product2 = creatProduct("002", HANDMADE, HOLD, "카페라떼", 4500);
        Product product3 = creatProduct("003", HANDMADE, STOP_SELLING, "팥빙수", 7000);
        productRepository.saveAll(List.of(product1, product2, product3));

        return Order.builder()
                .products(List.of(product1, product2, product3))
                .registeredDateTime(orderDateTime)
                .orderStatus(orderStatus)
                .build();
    }

    private Product creatProduct(String productNumber, ProductType type,
                                 ProductSellingStatus sellingStatus, String name, int price) {
        return Product.builder()
                .productNumber(productNumber)
                .type(type)
                .sellingStatus(sellingStatus)
                .name(name)
                .price(price)
                .build();
    }

}