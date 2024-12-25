package code.with.vanilson.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine engine;

    /**
     * Sends an email asynchronously to the specified recipient with an activation link and token.
     * <p>
     * This method constructs an email message using a template and the provided information. The email is sent
     * asynchronously using the Spring `JavaMailSender`. The following steps are performed:
     * <ul>
     *   <li>Determines the email template name based on the provided {@link EmailTemplateName}. If no template name
     *       is provided, a default "confirm-email" template is used.</li>
     *   <li>Creates a {@link MimeMessage} and a {@link MimeMessageHelper} to prepare the email content.</li>
     *   <li>Sets the recipient's email, subject, sender, and message body (using the email template).</li>
     *   <li>Populates the email template with dynamic properties such as the recipient's username, confirmation URL,
     *       and activation code.</li>
     *   <li>Sends the email using the {@link JavaMailSender}.</li>
     * </ul>
     * <p>
     * The email is sent asynchronously, which means that this method does not block the execution of other tasks.
     *
     * @param to              The email address of the recipient.
     * @param username        The username of the recipient, used in the email template.
     * @param emailTemplate   The template to be used for rendering the email content.
     * @param confirmationUrl The URL to be included in the email for confirming the user's account.
     * @param activationCode  The activation code to be included in the email.
     * @param subject         The subject of the email.
     * @throws MessagingException If there is an error while composing or sending the email.
     */
    @Async
    public void send(String to,
                     String username,
                     EmailTemplateName emailTemplate,
                     String confirmationUrl,
                     String activationCode,
                     String subject) throws MessagingException {

        try {
            var templateName = emailTemplate == null ? "confirm-email" : emailTemplate.name();

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED, UTF_8.name());
            Map<String, Object> properties = new HashMap<>();
            properties.put("username", username);
            properties.put("confirmationUrl", confirmationUrl);
            properties.put("activationCode", activationCode);

            var context = new Context();
            context.setVariables(properties);

            helper.setFrom("vanilsonmuhongo@outlook.com");
            helper.setTo(to);
            helper.setSubject(subject);

            String template = engine.process(templateName, context);
            helper.setText(template, true);

            mailSender.send(message);
            logger.info("Email sent to {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}", to, e);
            throw e;
        }
    }
}
