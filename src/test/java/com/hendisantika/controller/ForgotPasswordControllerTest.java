package com.hendisantika.controller;

import com.hendisantika.controller.ForgotPasswordController;
import com.hendisantika.entity.Mail;
import com.hendisantika.entity.PasswordResetToken;
import com.hendisantika.entity.User;
import com.hendisantika.service.framework.EmailService;
import com.hendisantika.service.framework.PasswordResetTokenService;
import com.hendisantika.service.framework.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(ForgotPasswordController.class)

class ForgotPasswordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordResetTokenService passwordResetTokenService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private MessageSource messageSource;

    @Test void viewPage_shouldReturnForgotPasswordView() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"))
                .andExpect(model().attributeExists("passwordForgot"));
    }

    @Test
    void testCase1_validationError_shouldReturnForgotPasswordView() throws Exception {
    // Test Path 1,2,3
    mockMvc.perform(post("/forgot-password")
                    .param("email", ""))
            .andExpect(status().isOk())
            .andExpect(view().name("forgot-password"));
    }

    @Test
    void testCase2_emailNotFound_shouldReturnViewWithEmailError() throws Exception {
        // Test Path 1,2,4,5,6
        when(userService.findByEmail("test@mail.com")).thenReturn(null);
        when(messageSource.getMessage("EMAIL_NOT_FOUND", new Object[]{}, Locale.ENGLISH))
                .thenReturn("Email not found");

        mockMvc.perform(post("/forgot-password")
                        .param("email", "test@mail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"))
                .andExpect(model().attributeExists("emailError"))
                .andExpect(model().attribute("emailError", "Email not found"));
    }

    @Test
    void testCase3_tokenNotSaved_shouldReturnViewWithTokenError() throws Exception {
        // Test Path 1,2,4,5,7,8,9
        User user = new User();
        user.setEmail("user@mail.com");

        when(userService.findByEmail("user@mail.com")).thenReturn(user);
        when(passwordResetTokenService.save(any(PasswordResetToken.class))).thenReturn(null);
        when(messageSource.getMessage("TOKEN_NOT_SAVED", new Object[]{}, Locale.ENGLISH))
                .thenReturn("Token could not be saved");

        mockMvc.perform(post("/forgot-password")
                        .param("email", "user@mail.com")
                        .requestAttr("javax.servlet.http.HttpServletRequest", mock(HttpServletRequest.class)))
                .andExpect(status().isOk())
                .andExpect(view().name("forgot-password"))
                .andExpect(model().attributeExists("tokenError"))
                .andExpect(model().attribute("tokenError", "Token could not be saved"));
    }

    @Test
    void testCase4_success_shouldSendEmailAndRedirect() throws Exception {
        // Test Path 1,2,4,5,7,8,10,11
        User user = new User();
        user.setEmail("user@mail.com");

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpirationDate(LocalDateTime.now().plusMinutes(30));

        when(userService.findByEmail("user@mail.com")).thenReturn(user);
        when(passwordResetTokenService.save(any(PasswordResetToken.class))).thenReturn(token);
        when(messageSource.getMessage("PASSWORD_RESET_TOKEN_SENT", new Object[]{}, Locale.ENGLISH))
                .thenReturn("Password reset token sent");

        mockMvc.perform(post("/forgot-password")
                        .param("email", "user@mail.com")
                        .requestAttr("javax.servlet.http.HttpServletRequest", mock(HttpServletRequest.class)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/forgot-password"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "Password reset token sent"));

        verify(emailService, times(1)).send(any(Mail.class));
        verify(passwordResetTokenService, times(1)).save(any(PasswordResetToken.class));
    }

    @Test void passwordForgot_modelAttribute_shouldExist() throws Exception {
        mockMvc.perform(get("/forgot-password"))
                .andExpect(model().attributeExists("passwordForgot"));
    }
}


