package de.anycook.user;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UserTest {

    @Test
    public void testCheckPasswordID() {
        String pass0 = "123";
        assertFalse(User.checkPassword(pass0));
        String pass1 = "123456";
        assertFalse(User.checkPassword(pass1));
        String pass2 = "abcdef";
        assertFalse(User.checkPassword(pass2));
        String pass3 = "ABCDEF";
        assertFalse(User.checkPassword(pass3));
        String pass4 = "abCD12@";
        assertTrue(User.checkPassword(pass4));
        String pass5 = "abCD12";
        assertTrue(User.checkPassword(pass5));
        String pass6 = "absd12";
        assertTrue(User.checkPassword(pass6));
    }

}
