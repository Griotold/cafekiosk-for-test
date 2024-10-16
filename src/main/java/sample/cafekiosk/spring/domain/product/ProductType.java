package sample.cafekiosk.spring.domain.product;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum ProductType {

    HANDMADE("제조 음료"),
    BOTTLE("병 음료"),
    BAKERY("베이커리");

    public static final List<ProductType> TYPES_FOR_STOCK = List.of(BOTTLE, BAKERY);

    private final String text;

    public static boolean containsStockType(ProductType type) {
        return TYPES_FOR_STOCK.contains(type);
    }
}
