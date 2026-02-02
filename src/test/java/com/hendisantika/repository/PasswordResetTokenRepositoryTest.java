package com.hendisantika.repository;

import com.hendisantika.entity.PasswordResetToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDateTime;
import java.util.Optional;

// Use JUnit 5 Assertions
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Test
    void test_findByToken_Found() {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("reset123");
        token.setExpirationDate(LocalDateTime.now().plusHours(1));

        tokenRepository.save(token);

        Optional<PasswordResetToken> result =
                tokenRepository.findByToken("reset123");

        assertTrue(result.isPresent());
        assertEquals("reset123", result.get().getToken());
    }
    @Test
    void test_findByToken_NotFound() {
        Optional<PasswordResetToken> result = tokenRepository.findByToken("invalidToken");
        assertTrue(result.isEmpty(), "Token should not be found");
    }

}

