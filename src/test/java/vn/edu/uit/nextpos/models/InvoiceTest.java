package vn.edu.uit.nextpos.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceTest {

    @Test
    void isUnsetOptionalId_intTreatsZeroAndNegativeAsUnset() {
        assertTrue(Invoice.isUnsetOptionalId(0));
        assertTrue(Invoice.isUnsetOptionalId(-1));
        assertFalse(Invoice.isUnsetOptionalId(1));
    }

    @Test
    void isUnsetOptionalId_integerTreatsNullAndNonPositiveAsUnset() {
        assertTrue(Invoice.isUnsetOptionalId((Integer) null));
        assertTrue(Invoice.isUnsetOptionalId(0));
        assertFalse(Invoice.isUnsetOptionalId(2));
    }
}
