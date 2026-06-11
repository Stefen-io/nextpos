package vn.edu.uit.nextpos.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import vn.edu.uit.nextpos.models.Invoice;
import vn.edu.uit.nextpos.models.InvoiceItem;
import vn.edu.uit.nextpos.models.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfEnvironmentVariable(named = "NEXTPOS_DB_TEST", matches = "true")
class InvoiceDAOTest {

    @Test
    void checkout_persistsInvoiceAndItems() {
        ProductDAO productDAO = new ProductDAO();
        List<Product> products = productDAO.getAllProducts();
        assertFalse(products.isEmpty(), "seed data must include at least one product");

        Product product = products.getFirst();
        int stockBefore = product.getQuantity();

        Invoice invoice = new Invoice();
        invoice.setCustomerId(0);
        invoice.setEmployeeId(1);
        invoice.setTableId(null);
        invoice.setDiscountId(null);
        invoice.setTotal(BigDecimal.valueOf(product.getPrice()));
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setItems(List.of(
                new InvoiceItem(0, 0, product.getId(), 1, BigDecimal.valueOf(product.getPrice()))
        ));

        InvoiceDAO invoiceDAO = new InvoiceDAO();
        assertTrue(invoiceDAO.checkout(invoice));

        Invoice saved = invoiceDAO.findById(invoice.getId());
        assertNotNull(saved);
        assertEquals(1, saved.getItems().size());
        assertEquals(product.getId(), saved.getItems().getFirst().getProductId());

        Product after = productDAO.getProductById(product.getId());
        assertNotNull(after);
        assertEquals(stockBefore - 1, after.getQuantity());
    }

    @Test
    void checkout_rollsBackOnInvalidProduct() {
        Invoice invoice = new Invoice();
        invoice.setCustomerId(0);
        invoice.setEmployeeId(1);
        invoice.setTotal(BigDecimal.TEN);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setItems(List.of(
                new InvoiceItem(0, 0, 999_999, 1, BigDecimal.TEN)
        ));

        InvoiceDAO invoiceDAO = new InvoiceDAO();
        int countBefore = invoiceDAO.getAllInvoices().size();

        assertFalse(invoiceDAO.checkout(invoice));
        assertEquals(countBefore, invoiceDAO.getAllInvoices().size());
    }
}
