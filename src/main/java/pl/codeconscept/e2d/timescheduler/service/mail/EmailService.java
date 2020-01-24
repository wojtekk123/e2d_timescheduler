package pl.codeconscept.e2d.timescheduler.service.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class EmailService {


    private JavaMailSender javaMailSender;

    private SpringTemplateEngine springTemplateEngine;


    @Autowired
    public EmailService(JavaMailSender javaMailSender,SpringTemplateEngine springTemplateEngine) {
        this.javaMailSender = javaMailSender;
        this.springTemplateEngine=springTemplateEngine;
    }

    public void sendSimpleMessage (Mail mail) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        Context context = new Context();
        context.setVariables(mail.getModel());
        String html = springTemplateEngine.process("mailTemplate",context);

        helper.setTo(mail.getTo());
        helper.setFrom(mail.getFrom());
        helper.setText(html,true);
        helper.setSubject(mail.getSubject());
        javaMailSender.send(message);
        log.info("sending email to queue");

    }
}
