package com.hendisantika.controller;

import com.hendisantika.entity.PasswordReset;
import com.hendisantika.entity.PasswordResetToken;
import com.hendisantika.entity.User;
import com.hendisantika.service.framework.PasswordResetTokenService;
import com.hendisantika.service.framework.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = ResetPasswordController.class)
class ResetPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PasswordResetTokenService tokenService;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageSource messageSource;

    @Test
    void viewPage_tokenNotFound_shouldShowError() throws Exception {
        when(tokenService.findByToken("invalid")).thenReturn(null);
        when(messageSource.getMessage("TOKEN_NOT_FOUND", new Object[]{}, java.util.Locale.ENGLISH))
                .thenReturn("Token not found");

        mockMvc.perform(get("/reset-password").param("token", "invalid"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Token not found"));
    }

    @Test
    void viewPage_tokenExpired_shouldShowError() throws Exception {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("expiredToken");
        token.setExpirationDate(LocalDateTime.now().minusHours(1));

        when(tokenService.findByToken("expiredToken")).thenReturn(token);
        when(messageSource.getMessage("TOKEN_EXPIRED", new Object[]{}, java.util.Locale.ENGLISH))
                .thenReturn("Token expired");

        mockMvc.perform(get("/reset-password").param("token", "expiredToken"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Token expired"));
    }

    @Test
    void viewPage_tokenValid_shouldShowToken() throws Exception {
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("validToken");
        token.setExpirationDate(LocalDateTime.now().plusHours(1));

        when(tokenService.findByToken("validToken")).thenReturn(token);

        mockMvc.perform(get("/reset-password").param("token", "validToken"))
                .andExpect(status().isOk())
                .andExpect(view().name("reset-password"))
                .andExpect(model().attributeExists("token"))
                .andExpect(model().attribute("token", "validToken"));
    }

    @Test
    void resetPassword_validationError_shouldRedirectBack() throws Exception {
        mockMvc.perform(post("/reset-password")
                        .param("token", "someToken")
                        .param("password", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/reset-password?token=someToken"));
    }

    @Test
    void resetPassword_valid_shouldUpdatePasswordAndRedirect() throws Exception {
        User user = new User();
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("validToken");
        token.setUser(user);

        when(tokenService.findByToken("validToken")).thenReturn(token);

        mockMvc.perform(post("/reset-password")
                        .param("token", "validToken")
                        .param("password", "newPassword")
                        .param("confirmPassword", "newPassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // verify the userService was called to update password
        verify(userService, times(1)).updatePassword(user);
        // optional: check that password was set
        assert(user.getPassword().equals("newPassword"));
    }
}
