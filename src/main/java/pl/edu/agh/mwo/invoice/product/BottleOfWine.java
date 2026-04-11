package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class BottleOfWine extends ExciseProducts {

    protected BottleOfWine(String name, BigDecimal price, BigDecimal tax, int exciseTax) {
        super(name, price, new BigDecimal("0.23"), new BigDecimal("5.56"));
    }
}
