package com.hendisantika.service.unit;

import com.hendisantika.entity.PasswordResetToken;
import com.hendisantika.repository.PasswordResetTokenRepository;
import com.hendisantika.service.implementation.PasswordResetTokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PasswordResetTokenServiceImplTest {

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @InjectMocks
    private PasswordResetTokenServiceImpl tokenService;

    private PasswordResetToken token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        token = new PasswordResetToken();
        token.setId(1L);
        token.setToken("abc123");
    }

    @Test
    void test_findByToken_Found() {
        when(passwordResetTokenRepository.findByToken("abc123")).thenReturn(Optional.of(token));

        PasswordResetToken found = tokenService.findByToken("abc123");

        assertNotNull(found);
        assertEquals("abc123", found.getToken());
        verify(passwordResetTokenRepository, times(1)).findByToken("abc123");
    }

    @Test
    void test_findByToken_NotFound() {
        when(passwordResetTokenRepository.findByToken("unknown")).thenReturn(Optional.empty());

        PasswordResetToken found = tokenService.findByToken("unknown");

        assertNull(found);
        verify(passwordResetTokenRepository, times(1)).findByToken("unknown");
    }

    @Test
    void test_findByToken_NullInput() {
        PasswordResetToken result = tokenService.findByToken(null);
        assertNull(result);
    }

    @Test
    void test_findByToken_Exception() {
        when(passwordResetTokenRepository.findByToken("abc123"))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tokenService.findByToken("abc123"));

        assertEquals("DB error", ex.getMessage());
        verify(passwordResetTokenRepository, times(1)).findByToken("abc123");
    }

    @Test
    void test_save() {
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenReturn(token);

        PasswordResetToken saved = tokenService.save(token);

        assertNotNull(saved);
        assertEquals("abc123", saved.getToken());
        verify(passwordResetTokenRepository, times(1)).save(any(PasswordResetToken.class));
    }

    @Test
    void test_save_Exception() {
        when(passwordResetTokenRepository.save(any(PasswordResetToken.class)))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> tokenService.save(token));

        assertEquals("DB error", ex.getMessage());
        verify(passwordResetTokenRepository, times(1)).save(token);
    }

}
