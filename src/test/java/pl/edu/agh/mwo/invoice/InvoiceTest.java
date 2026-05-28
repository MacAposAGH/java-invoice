package pl.edu.agh.mwo.invoice;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import pl.edu.agh.mwo.invoice.product.*;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class InvoiceTest {
    private Invoice invoice;

    @Before
    public void createEmptyInvoiceForTheTest() {
        invoice = new Invoice();
    }

    @Test
    public void testInvoiceHasUniqueNumber() {
        Invoice sceondInvoice = new Invoice();
        int firstNumber = invoice.getNumber();
        int secondNumber = sceondInvoice.getNumber();
        assertNotEquals(firstNumber, secondNumber);
    }

    @Test(expected = IllegalStateException.class)
    public void testPrintingInvoiceWithNoProducts() {
        invoice.print();
    }

    @Test
    public void testTextOfInvoiceWithProducts() {
        invoice.addProduct(new TaxFreeProduct("Mąka", new BigDecimal("100")));
        invoice.addProduct(new DairyProduct("Jajka", new BigDecimal("200")));
        invoice.addProduct(new OtherProduct("Mleko", new BigDecimal("300")));
        ArrayList<String> invoiceText = invoice.getInvoiceText();
        invoice.print();
        assertEquals(6, invoiceText.size());
        assertTrue(invoiceText.get(0).matches("\\d+"));
        assertTrue(invoiceText.get(1).matches("Nazwa\\s+Liczba sztuk\\s+Cena\\s+"));
        assertEquals("Liczba pozycji: 3", invoiceText.get(5));
    }

    @Test
    public void testInvoicesQuantityWithDuplicatedProducts() {
        Product product = new OtherProduct("Mąka", new BigDecimal("100"));
        invoice.addProduct(product);
        invoice.addProduct(product);
        assertEquals(Integer.valueOf(2), invoice.getProducts().get(product));
    }

    @Test
    public void testInvoicesQuantityWithProductsOfDifferentTypeAndSameName() {
        Product firstProduct = new DairyProduct("Mleczko", new BigDecimal("100")); // mleczko do picia
        Product secondProduct = new OtherProduct("Mleczko", new BigDecimal("100")); // mleczko do butów
        invoice.addProduct(firstProduct);
        invoice.addProduct(secondProduct);
        assertEquals(Integer.valueOf(1), invoice.getProducts().get(firstProduct));
        assertEquals(Integer.valueOf(1), invoice.getProducts().get(secondProduct));
    }

    @Test
    public void testInvoicesSubtotalWithExciseProducts() {
        BottleOfWine firstProduct = new BottleOfWine("Château Leroy", BigDecimal.valueOf(100));
        BottleOfWine secondProduct = new BottleOfWine("Amarena", BigDecimal.valueOf(100));
        FuelCanister thirdProduct = new FuelCanister("Pb95", BigDecimal.valueOf(100), BigDecimal.valueOf(5.56));
        FuelCanister fourthProduct = new FuelCanister("Diesel", BigDecimal.valueOf(100), BigDecimal.valueOf(5.56));
        invoice.addProduct(firstProduct);
        invoice.addProduct(secondProduct);
        invoice.addProduct(thirdProduct);
        invoice.addProduct(fourthProduct);
        assertEquals(new BigDecimal("400"), invoice.getSubtotal());
    }

    @Test
    public void testInvoicesTotalWithExciseProducts() {
        BottleOfWine firstProduct = new BottleOfWine("Amarena", BigDecimal.valueOf(94.44));
        FuelCanister secondProduct = new FuelCanister("Diesel", BigDecimal.valueOf(94.44), BigDecimal.valueOf(5.56));
        invoice.addProduct(firstProduct);
        invoice.addProduct(secondProduct);
        assertThat(new BigDecimal("231"), Matchers.comparesEqualTo(invoice.getTotal()));
    }

//    po zniesieniu podatku
    @Test
    public void testInvoicesTotalWithoutExciseForProducts() {
        FuelCanister firstProduct = new FuelCanister("Pb95", BigDecimal.valueOf(100));
        FuelCanister secondProduct = new FuelCanister("Diesel", BigDecimal.valueOf(100));
        invoice.addProduct(firstProduct);
        invoice.addProduct(secondProduct);
        assertThat(new BigDecimal("216"), Matchers.comparesEqualTo(invoice.getTotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptySubtotal() {
        assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTaxAmount() {
        assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTax()));
    }

    @Test
    public void testEmptyInvoiceHasEmptyTotal() {
        assertThat(BigDecimal.ZERO, Matchers.comparesEqualTo(invoice.getTotal()));
    }

    @Test
    public void testInvoiceSubtotalWithTwoDifferentProducts() {
        Product onions = new TaxFreeProduct("Warzywa", new BigDecimal("10"));
        Product apples = new TaxFreeProduct("Owoce", new BigDecimal("10"));
        invoice.addProduct(onions);
        invoice.addProduct(apples);
        assertThat(new BigDecimal("20"), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceSubtotalWithManySameProducts() {
        Product onions = new TaxFreeProduct("Warzywa", BigDecimal.valueOf(10));
        invoice.addProduct(onions, 100);
        assertThat(new BigDecimal("1000"), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceHasTheSameSubtotalAndTotalIfTaxIsZero() {
        Product taxFreeProduct = new TaxFreeProduct("Warzywa", new BigDecimal("199.99"));
        invoice.addProduct(taxFreeProduct);
        assertThat(invoice.getTotal(), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceHasProperSubtotalForManyProducts() {
        invoice.addProduct(new TaxFreeProduct("Owoce", new BigDecimal("200")));
        invoice.addProduct(new DairyProduct("Maslanka", new BigDecimal("100")));
        invoice.addProduct(new OtherProduct("Wino", new BigDecimal("10")));
        assertThat(new BigDecimal("310"), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceHasProperTaxValueForManyProduct() {
        // tax: 0
        invoice.addProduct(new TaxFreeProduct("Pampersy", new BigDecimal("200")));
        // tax: 8
        invoice.addProduct(new DairyProduct("Kefir", new BigDecimal("100")));
        // tax: 2.30
        invoice.addProduct(new OtherProduct("Piwko", new BigDecimal("10")));
        assertThat(new BigDecimal("10.30"), Matchers.comparesEqualTo(invoice.getTax()));
    }

    @Test
    public void testInvoiceHasProperTotalValueForManyProduct() {
        // price with tax: 200
        invoice.addProduct(new TaxFreeProduct("Maskotki", new BigDecimal("200")));
        // price with tax: 108
        invoice.addProduct(new DairyProduct("Maslo", new BigDecimal("100")));
        // price with tax: 12.30
        invoice.addProduct(new OtherProduct("Chipsy", new BigDecimal("10")));
        assertThat(new BigDecimal("320.30"), Matchers.comparesEqualTo(invoice.getTotal()));
    }

    @Test
    public void testInvoiceHasPropoerSubtotalWithQuantityMoreThanOne() {
        // 2x kubek - price: 10
        invoice.addProduct(new TaxFreeProduct("Kubek", new BigDecimal("5")), 2);
        // 3x kozi serek - price: 30
        invoice.addProduct(new DairyProduct("Kozi Serek", new BigDecimal("10")), 3);
        // 1000x pinezka - price: 10
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        assertThat(new BigDecimal("50"), Matchers.comparesEqualTo(invoice.getSubtotal()));
    }

    @Test
    public void testInvoiceHasPropoerTotalWithQuantityMoreThanOne() {
        // 2x chleb - price with tax: 10
        invoice.addProduct(new TaxFreeProduct("Chleb", new BigDecimal("5")), 2);
        // 3x chedar - price with tax: 32.40
        invoice.addProduct(new DairyProduct("Chedar", new BigDecimal("10")), 3);
        // 1000x pinezka - price with tax: 12.30
        invoice.addProduct(new OtherProduct("Pinezka", new BigDecimal("0.01")), 1000);
        assertThat(new BigDecimal("54.70"), Matchers.comparesEqualTo(invoice.getTotal()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithZeroQuantity() {
        invoice.addProduct(new TaxFreeProduct("Tablet", new BigDecimal("1678")), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvoiceWithNegativeQuantity() {
        invoice.addProduct(new DairyProduct("Zsiadle mleko", new BigDecimal("5.55")), -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddingNullProduct() {
        invoice.addProduct(null);
    }
}
