package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class Product {
    protected final String name;

    protected final BigDecimal price;

    protected final BigDecimal taxPercent;

    protected Product(String name, BigDecimal price, BigDecimal tax) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name can't be null or empty!");
        }
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Price can't be null or negative!");
        }

        this.name = name;
        this.price = price;
        this.taxPercent = tax;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public BigDecimal getPriceWithTax() {
        BigDecimal priceWithTax = this instanceof ExciseProduct product ? price.add(product.excisePrice) : price;
        return priceWithTax.multiply(taxPercent.add(BigDecimal.ONE));
    }
}
