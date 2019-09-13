package com.genie.job.portal;

import org.apache.commons.lang3.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Service for sending emails.
 * <p>
 * We use the @Async annotation to send emails asynchronously.
 * </p>
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    private static final String USER = "user";

    private static final String BASE_URL = "baseUrl";

    private final JavaMailSender javaMailSender;

    //private final MessageSource messageSource;

    public MailService() {

        this.javaMailSender = getJavaMailSender();
        //this.messageSource = messageSource;
    }
    
    private JavaMailSender getJavaMailSender() {
    	
    	/*
    	 * 	mailFrom: praveen.vengala@gmail.com
			mailBaseUrl: http://geniehealthjobs.com
			mailHost: email-smtp.us-east-1.amazonaws.com
			mailPort: 587
			mailUsername: AKIAJJLQLQOO2T573H4A
			mailPassword: AiWbi/6iW1UuMamWzzsGdh+4yMg+cfiyi3mGUXTiJapc
			mailProtocol: smtp
			mailTls: true
    	 */
    	
    	String mailHost = GenieProperties.getInstance().getValue("mailHost");
    	int mailPort = Integer.parseInt(GenieProperties.getInstance().getValue("mailPort"));
    	String mailUsername = GenieProperties.getInstance().getValue("mailUsername");
    	String mailPassword = GenieProperties.getInstance().getValue("mailPassword");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
         
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
         
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
         
        return mailSender;
    }

    @Async
    public void sendEmail(String to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send email[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
            isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(GenieProperties.getInstance().getValue("mailFrom"));
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent email to User '{}'", to);
        } catch (Exception e) {
            log.warn("Email could not be sent to user '{}'", to, e);
        }
    }
    
    /*public static void main(String args[]) {
    	MailService mailService = new MailService();
    	mailService.sendEmail("praveen.vengala@gmail.com", "How are you doing?", "How are you doing?", false, false);
    }*/
   
}
