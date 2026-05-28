package pl.edu.agh.mwo.invoice.product;

import java.math.BigDecimal;

public class ExciseProduct extends Product {

    protected final BigDecimal excisePrice;

    protected ExciseProduct(String name, BigDecimal price, BigDecimal tax, BigDecimal excisePrice) {
        super(name, price, tax);
        this.excisePrice = excisePrice;
    }
}
