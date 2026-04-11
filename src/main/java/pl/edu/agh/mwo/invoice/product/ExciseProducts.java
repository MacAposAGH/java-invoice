package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public abstract class ExciseProducts extends Product {
    BigDecimal excise;

    protected ExciseProducts(String name, BigDecimal price, BigDecimal tax, BigDecimal exciseTax) {
        super(name, price, tax);
        this.excise = exciseTax;
    }

    public BigDecimal getPriceWithExcise() {
        return price.multiply(taxPercent.add(BigDecimal.ONE)).add(excise);
    }
}
