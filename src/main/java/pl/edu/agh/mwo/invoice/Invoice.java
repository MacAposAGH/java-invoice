package pl.edu.agh.mwo.invoice;

import pl.edu.agh.mwo.invoice.product.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

public class Invoice {
    private static final Random RANDOM = new Random();

    private final int number;
    HashMap<Product, Integer> products = new HashMap<>();

    public Invoice() {
        int origin = (int) Math.pow(10, 5);
        this.number = RANDOM.nextInt(origin, origin * 10);
    }

    public ArrayList<String> getInvoiceText() {
        if (products.isEmpty()) {
            throw new IllegalStateException("Invoice has no products.");
        }
        String s = "%-10s %-15s %-10s";
        ArrayList<String> arrayList = new ArrayList<>(List.of(
                String.valueOf(number),
                s.formatted("Nazwa", "Liczba sztuk", "Cena")
        ));
        products.forEach((k, v) -> arrayList.add(s.formatted(k.getName(), v, k.getPrice())));
        arrayList.add("Liczba pozycji: %s".formatted(products.values().stream().reduce(0, Integer::sum)));
        return arrayList;
    }

    public void print() {
        System.out.println(String.join("\n", getInvoiceText()));
    }

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
        products.merge(product, quantity, Integer::sum);
    }

    private BigDecimal reducePrice(Function<Product, BigDecimal> extractPrice) {
        return products.keySet().stream()
                .map(p -> extractPrice.apply(p).multiply(BigDecimal.valueOf(products.get(p))))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getNumber() {
        return number;
    }

    public HashMap<Product, Integer> getProducts() {
        return products;
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
