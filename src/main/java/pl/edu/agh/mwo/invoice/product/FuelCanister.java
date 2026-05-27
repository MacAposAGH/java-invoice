package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class FuelCanister extends Product{
    protected FuelCanister(String name, BigDecimal price, BigDecimal tax, BigDecimal excise) {
        super(name, price.add(excise), tax);
    }
}
