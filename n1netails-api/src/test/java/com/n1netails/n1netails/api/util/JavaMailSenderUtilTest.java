package com.n1netails.n1netails.api.util;

import com.n1netails.n1netails.api.exception.type.EmailTemplateNotFoundException;
import com.n1netails.n1netails.api.model.entity.EmailNotificationTemplateEntity;
import com.n1netails.n1netails.api.model.request.SendMailRequest;
import com.n1netails.n1netails.api.repository.EmailNotificationTemplateRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JavaMailSenderUtilTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private EmailNotificationTemplateRepository emailNotificationTemplateRepository;

    @InjectMocks
    private JavaMailSenderUtil javaMailSenderUtil;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(javaMailSenderUtil, "emailEnabled", true);
        ReflectionTestUtils.setField(javaMailSenderUtil, "from", "test@n1netails.com");
    }

    @Test
    void sendMail_EmailDisabled_ReturnsCompletedFuture() throws MessagingException, EmailTemplateNotFoundException {
        ReflectionTestUtils.setField(javaMailSenderUtil, "emailEnabled", false);
        SendMailRequest request = new SendMailRequest();

        CompletableFuture<Void> result = javaMailSenderUtil.sendMail(request);

        assertTrue(result.isDone());
        verifyNoInteractions(mailSender);
        verifyNoInteractions(emailNotificationTemplateRepository);
    }

    @Test
    void sendMail_TemplateNotFound_ThrowsException() {
        SendMailRequest request = new SendMailRequest();
        request.setNotificationTemplateName("unknown-template");
        when(emailNotificationTemplateRepository.findByName("unknown-template")).thenReturn(Optional.empty());

        assertThrows(EmailTemplateNotFoundException.class, () -> javaMailSenderUtil.sendMail(request));
    }

    @Test
    void sendMail_Success() throws MessagingException, EmailTemplateNotFoundException, ExecutionException, InterruptedException {
        SendMailRequest request = new SendMailRequest();
        request.setNotificationTemplateName("welcome");
        request.setTo("user@example.com");
        request.setSubjectParams(Map.of("username", "John"));
        request.setBodyParams(Map.of("link", "http://link.com"));
        request.setCc(List.of("cc@example.com"));
        request.setBcc(List.of("bcc@example.com"));

        EmailNotificationTemplateEntity template = new EmailNotificationTemplateEntity();
        template.setSubject("Welcome {{username}}");
        template.setHtmlBody("Click here: {{link}}");

        MimeMessage mimeMessage = mock(MimeMessage.class);

        when(emailNotificationTemplateRepository.findByName("welcome")).thenReturn(Optional.of(template));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        CompletableFuture<Void> result = javaMailSenderUtil.sendMail(request);

        assertTrue(result.isDone());
        assertNull(result.get());
        verify(mailSender).send(mimeMessage);
    }
}
