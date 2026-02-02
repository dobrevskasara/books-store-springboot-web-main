package com.hendisantika.service.unit;


import com.hendisantika.entity.Role;
import com.hendisantika.entity.User;
import com.hendisantika.repository.RoleRepository;
import com.hendisantika.repository.UserRepository;
import com.hendisantika.service.implementation.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");

        role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
    }

    @Test
    void test_findByEmail_Found() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User found = userService.findByEmail("test@example.com");

        assertNotNull(found);
        assertEquals("test@example.com", found.getEmail());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void test_findByEmail_NotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        User found = userService.findByEmail("unknown@example.com");

        assertNull(found);
        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }

    @Test
    void test_loadUserByUsername_Found() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void test_loadUserByUsername_NotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername("unknown@example.com"));

        verify(userRepository, times(1)).findByEmail("unknown@example.com");
    }

    @Test
    void test_updatePassword() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updatePassword(user);

        assertEquals("encodedPassword", user.getPassword());
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_save_WithRole() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User saved = userService.save(user);

        assertEquals("encodedPassword", saved.getPassword());
        assertNotNull(saved.getRoles());
        assertTrue(saved.getRoles().contains(role));
        verify(passwordEncoder, times(1)).encode("password");
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_save_WithoutRole() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        User saved = userService.save(user);

        assertEquals("encodedPassword", saved.getPassword());
        assertNull(saved.getRoles());
        verify(passwordEncoder, times(1)).encode("password");
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_findByEmail_Exception() {
        when(userRepository.findByEmail("test@example.com"))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class,
                () -> userService.findByEmail("test@example.com"));

        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void test_updatePassword_Exception() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.updatePassword(user));

        assertEquals("DB error", ex.getMessage());
        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void test_save_Exception() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.save(user));

        assertEquals("DB error", ex.getMessage());
        verify(passwordEncoder, times(1)).encode("password");
        verify(roleRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

}
