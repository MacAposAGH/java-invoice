package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class BottleOfWine extends Product {

    protected BottleOfWine(String name, BigDecimal price) {
        super(name, price.add(new BigDecimal("5.56")), new BigDecimal("0.23"));
    }
}
