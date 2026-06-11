package vn.edu.uit.nextpos.util;

import org.junit.jupiter.api.Test;
import vn.edu.uit.nextpos.models.Invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonUtilTest {

    @Test
    void serializesInvoiceWithLocalDateTime() {
        Invoice invoice = new Invoice();
        invoice.setId(1);
        invoice.setEmployeeId(1);
        invoice.setTotal(BigDecimal.TEN);
        invoice.setCreatedAt(LocalDateTime.of(2026, 6, 11, 12, 30));

        String json = assertDoesNotThrow(() -> JsonUtil.gson().toJson(invoice));
        assertTrue(json.contains("2026-06-11T12:30"));
    }
}
