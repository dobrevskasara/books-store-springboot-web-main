package com.hendisantika.repository;

import com.hendisantika.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void test_findByEmail_Found() {
        User user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("test@test.com");
        user.setPassword("123456");

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByEmail("test@test.com");

        assertTrue(result.isPresent());
        assertEquals("test@test.com", result.get().getEmail());
    }
    @Test
    void test_findByEmail_NotFound() {
        Optional<User> result = userRepository.findByEmail("noone@nowhere.com");

        assertTrue(result.isEmpty());
    }
}