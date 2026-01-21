package pl.edu.agh.mwo.invoice;

import pl.edu.agh.mwo.invoice.product.Product;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class Invoice {
    HashMap<Product, Integer> products = new HashMap<>();

    public void addProduct(Product product) {
        addProduct(product, 1);
    }

    public void addProduct(Product product, Integer quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product can't be null!");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Number of products can't be 0 or negative!");
        }
        products.put(product, quantity);
    }

    private BigDecimal reducePrice(Function<Product, BigDecimal> extractPrice ){
        return products.keySet().stream()
                .map(p -> extractPrice.apply(p)
                        .multiply(BigDecimal.valueOf(products.get(p))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSubtotal() {
        return reducePrice(Product::getPrice);
    }

    public BigDecimal getTax() {
        return getTotal().subtract(getSubtotal());
    }

    public BigDecimal getTotal() {
        return reducePrice(Product::getPriceWithTax);
    }
}
