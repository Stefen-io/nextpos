package vn.edu.uit.nextpos.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import vn.edu.uit.nextpos.models.Employee;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SessionTest {

    @AfterEach
    void tearDown() {
        Session.logout();
    }

    @Test
    void isAdmin_trueWhenRoleIdIsOne() {
        Employee admin = new Employee();
        admin.setRole_id(AuthDefaults.ADMIN_ROLE_ID);
        Session.login(admin);
        assertTrue(Session.isAdmin());
    }

    @Test
    void isAdmin_falseWhenRoleIdIsNotOne() {
        Employee staff = new Employee();
        staff.setRole_id(2);
        Session.login(staff);
        assertFalse(Session.isAdmin());
    }

    @Test
    void isAdmin_falseWhenNotLoggedIn() {
        assertFalse(Session.isAdmin());
    }
}
