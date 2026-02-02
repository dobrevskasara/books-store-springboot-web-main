package com.hendisantika.controller;

import com.hendisantika.entity.User;
import com.hendisantika.service.framework.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(SignupController.class)
class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private MessageSource messageSource;

    @Test
    void viewPage_shouldReturnSignupView() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void saveUser_validationError_shouldReturnSignupView() throws Exception {
        mockMvc.perform(post("/signup")
                        .param("email", "")
                        .param("password", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"));
    }

    @Test
    void saveUser_emailAlreadyExists_shouldReturnSignupViewWithError() throws Exception {
        User existingUser = new User();
        existingUser.setEmail("test@example.com");

        when(userService.findByEmail("test@example.com")).thenReturn(existingUser);
        when(messageSource.getMessage("EMAIL_EXISTS", new Object[]{}, Locale.ENGLISH))
                .thenReturn("Email already exists");

        mockMvc.perform(post("/signup")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "test@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Email already exists"));
    }



    @Test
    void saveUser_saveFails_shouldReturnSignupViewWithError() throws Exception {
        when(userService.findByEmail("new@example.com")).thenReturn(null);
        when(userService.save(any(User.class))).thenReturn(null);
        when(messageSource.getMessage("EMAIL_NOT_SAVED", new Object[]{}, Locale.ENGLISH))
                .thenReturn("User not saved");

        mockMvc.perform(post("/signup")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "new@example.com")
                        .param("password", "password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "User not saved"));


    }
    @Test
    void saveUser_success_shouldRedirectWithFlashAttribute() throws Exception {
        User newUser = new User();
        newUser.setEmail("new@example.com");

        when(userService.findByEmail("new@example.com")).thenReturn(null);
        when(userService.save(any(User.class))).thenReturn(newUser);
        when(messageSource.getMessage("EMAIL_SAVED", new Object[]{}, Locale.ENGLISH))
                .thenReturn("User saved successfully");

        mockMvc.perform(post("/signup")
                        .param("firstName", "John")
                        .param("lastName", "Doe")
                        .param("email", "new@example.com")
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success", "User saved successfully"));

        verify(userService, times(1)).save(any(User.class));
    }


    @Test
    void user_modelAttribute_shouldExist() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(model().attributeExists("user"));
    }
}
