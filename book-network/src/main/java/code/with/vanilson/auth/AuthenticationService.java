package code.with.vanilson.auth;

import code.with.vanilson.email.EmailService;
import code.with.vanilson.email.EmailTemplateName;
import code.with.vanilson.role.RoleRepository;
import code.with.vanilson.user.Token;
import code.with.vanilson.user.TokenRepository;
import code.with.vanilson.user.User;
import code.with.vanilson.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

import static code.with.vanilson.util.constants.Constant.*;

@Service
@SuppressWarnings("all")
@RequiredArgsConstructor
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;

    /**
     * Registers a new user by creating a user entity and saving it to the database.
     * The method also assigns the "USER" role to the new user and sends an email validation request.
     *
     * @param request The registration request containing user details (first name, last name, email, password).
     * @throws RoleNotFoundException If the "USER" role is not found in the role repository.
     * @throws UserSaveException     If there is an error while saving the user to the repository.
     */

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName(USER_ROLE_NAME)
                // todo - better exception handling
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
    }

    private void sendValidationEmail(User user) {
        try {
            logger.info("Generating activation token for user: {}", user.getEmail());
            var newToken = generateAndSaveActivationToken(user);
            logger.info("Sending validation email to: {}", user.getEmail());
            emailService.send(user.getEmail(), user.fullName(), EmailTemplateName.ACTIVATION_ACCOUNT,
                    ACTIVATION_URL, newToken, "Account Activation");
            logger.info("Validation email sent to {}", user.getEmail());
        } catch (MessagingException e) {
            logger.error("Failed to send validation email to {}", user.getEmail(), e);
        }
    }

    /**
     * Generates an activation token, saves it to the database, and returns the token.
     * The token is associated with a given user and has a specific expiration time.
     *
     * @param user The user for whom the activation token is generated.
     * @return The generated activation token as a String.
     * @throws TokenGenerationException If there is an error during the token generation or saving process.
     */
    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(ACTIVATION_TOKEN_LENGTH);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(MINUTES))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    /**
     * Generates a random activation code of the specified length using numeric characters.
     * The code is generated using a secure random number generator.
     *
     * @param length The length of the activation code to generate.
     * @return A random activation code as a String.
     */
    private String generateActivationCode(int length) {
        var characters = "0123456789";
        var codeBuilder = new StringBuilder();
        var secureRandom = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }
}
