package com.hendisantika.service.unit;

import com.hendisantika.entity.Mail;
import com.hendisantika.service.implementation.EmailServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class EmailServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @InjectMocks
    private EmailServiceImpl emailService;

    private Mail mail;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mail = new Mail();
        mail.setTo("recipient@example.com");
        mail.setFrom("sender@example.com");
        mail.setSubject("Test Subject");

        Map<String, Object> model = new HashMap<>();
        model.put("name", "Test User");
        mail.setModel(model);
    }

    @Test
    void test_send() throws MessagingException {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        when(templateEngine.process((String) any(), any(Context.class))).thenReturn("<html>Test Email</html>");

        emailService.send(mail);

        verify(templateEngine, times(1)).process((String) any(), any(Context.class));
        verify(mailSender, times(1)).send(mimeMessage);
    }

    @Test
    void test_send_ExceptionThrown() throws Exception {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(any(String.class), any(Context.class)))
                .thenReturn("<html>Test Email</html>");

        doThrow(new RuntimeException("Simulated Exception")).when(mailSender).send(mimeMessage);

        Assertions.assertThrows(RuntimeException.class, () -> emailService.send(mail));
    }


}

