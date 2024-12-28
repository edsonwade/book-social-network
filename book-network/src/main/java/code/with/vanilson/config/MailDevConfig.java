

package code.with.vanilson.config;
/**
 * Author: vanilson muhongo
 * Date:28/12/2024
 * Time:20:53
 * Version:1
 */

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailDevConfig {

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("localhost");
        mailSender.setPort(1025);
        mailSender.setUsername(""); // No authentication needed for MailDev
        mailSender.setPassword(""); // No authentication needed for MailDev

        return mailSender;
    }
}
