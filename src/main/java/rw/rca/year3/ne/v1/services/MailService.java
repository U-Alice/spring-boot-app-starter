package rw.rca.year3.ne.v1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import rw.rca.year3.ne.v1.exceptions.BadRequestException;
import rw.rca.year3.ne.v1.models.User;
import rw.rca.year3.ne.v1.utils.Mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String appEmail;

    @Value("${swagger.app_name}")
    private String appName;

    @Value("${client.host}")
    private String clientHost;

    @Autowired
    public MailService(SpringTemplateEngine templateEngine, JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Async
    public void sendResetPasswordMail(User user) {
        Mail mail = new Mail(
                appName,
                "Welcome to "+appName+", You requested to reset your password",
                user.getFullName(), user.getEmail(), "reset-password-email", user.getActivationCode());

        sendEmail(mail);
    }

    @Async
    public void sendAccountVerificationEmail(User user) {
        String link = clientHost + "/verify-email?email=" + user.getEmail() + "&code=" + user.getActivationCode();

        Mail mail = new Mail(
                appName,
                "Welcome to "+appName+", Verify your email to continue",
                user.getFullName(), user.getEmail(), "verify-email",link);
        sendEmail(mail);
    }

    @Async
    public void sendWelcomeEmail(User user) {
        Mail mail = new Mail(
                appName,
                "Welcome to "+appName+", Your account is approved",
                user.getFullName(), user.getEmail(), "welcome-email",appName);
        sendEmail(mail);
    }

    @Async
    public void sendAccountRejectedMail(User user) {
        Mail mail = new Mail(
                appName,
                appName+": Sorry Your Account is rejected",
                user.getFullName(), user.getEmail(), "account-rejected",appName,user.getRejectionDescription());
        sendEmail(mail);
    }

    @Async
    public void sendEmail(Mail mail) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariable("app_name",mail.getAppName());
            context.setVariable("data", mail.getData());
            context.setVariable("name", mail.getFullNames());
            context.setVariable("otherData", mail.getOtherData());

            String html = templateEngine.process(mail.getTemplate(), context);
            helper.setTo(mail.getToEmail());
            helper.setText(html, true);
            helper.setSubject(mail.getSubject());
            helper.setFrom(appEmail);
            mailSender.send(message);


        } catch (MessagingException exception) {
            throw new BadRequestException("Failed To Send An Email");
        }
    }
}