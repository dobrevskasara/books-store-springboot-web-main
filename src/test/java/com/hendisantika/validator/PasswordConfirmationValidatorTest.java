package com.hendisantika.validator;

import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Payload;
import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;

class PasswordConfirmationValidatorTest {

    private PasswordConfirmationValidator validator;

    // Dummy class за тест
    @Getter
    static class DummyPassword {
        private String password;
        private String confirmPassword;

        public void setPassword(String password) {
            this.password = password;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }

    @BeforeEach
    void setup() {
        validator = new PasswordConfirmationValidator();
        validator.initialize(new PasswordConfirmation() {
            @Override
            public String password() {
                return "password";
            }

            @Override
            public String confirmPassword() {
                return "confirmPassword";
            }

            @Override
            public String message() {
                return "Passwords must match!";
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return PasswordConfirmation.class;
            }
        });
    }

    @Test
    void testPasswordsMatch() {
        DummyPassword d = new DummyPassword();
        d.setPassword("123456");
        d.setConfirmPassword("123456");

        boolean result = validator.isValid(d, null);

        assertTrue(result);
    }

    @Test
    void testPasswordsDoNotMatch() {
        DummyPassword d = new DummyPassword();
        d.setPassword("123456");
        d.setConfirmPassword("abcdef");

        boolean result = validator.isValid(d, null);

        assertFalse(result);
    }

    @Test
    void testBothNull() {
        DummyPassword d = new DummyPassword();
        d.setPassword(null);
        d.setConfirmPassword(null);

        boolean result = validator.isValid(d, null);

        assertTrue(result);
    }

    @Test
    void testPasswordNullConfirmNotNull() {
        DummyPassword d = new DummyPassword();
        d.setPassword(null);
        d.setConfirmPassword("123");

        boolean result = validator.isValid(d, null);

        assertFalse(result);
    }

    @Test
    void testPasswordNotNullConfirmNull() {
        DummyPassword d = new DummyPassword();
        d.setPassword("123");
        d.setConfirmPassword(null);

        boolean result = validator.isValid(d, null);

        assertFalse(result);
    }
}
