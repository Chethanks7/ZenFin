package com.ZenFin.auth;

import com.ZenFin.email.EmailService;
import com.ZenFin.email.EmailTemplateName;
import com.ZenFin.role.RoleRepository;
import com.ZenFin.security.JwtService;
import com.ZenFin.user.OTPToken;
import com.ZenFin.user.OTPTokenRepository;
import com.ZenFin.user.User;
import com.ZenFin.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OTPTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;
    @Value("${spring.mail.port}")
    private int port ;



    @Value("${spring.mailing.frontend.activation-url}")
    private String activationUrl;

    public void register(@Valid RegistrationRequest registration) throws Exception {
        var role = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Role is not in database"));

        var user = User.builder()
                .firstName(registration.getFirstname())
                .lastName(registration.getLastname())
                .email(registration.getEmail())
                .password(registration.getPassword())// later add encoding the password
                .accountLocked(false)
                .roles(List.of(role))
                .build();

        userRepository.save(user);

        sendEmailToVerify(user);

    }

    private void sendEmailToVerify(User user) throws Exception {

        var newOtp = generateAndSaveNewOtp(user);

        emailService.sendEmail(
                user.getEmail(),
                user.fullName(),
                EmailTemplateName.ACTIVATE_ACCOUNT,
                activationUrl,
                newOtp,
                "account activation",
                port
        );
    }


    public IvParameterSpec generateIv(){
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public String encryptedOtp(String otp, Key key , IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,key ,iv);
        byte[] encryptedOtp = cipher.doFinal(otp.getBytes());
        return Base64.getEncoder().encodeToString(encryptedOtp);
    }

    public SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Use 256 bits for strong encryption
        return keyGen.generateKey();
    }

    private String generateAndSaveNewOtp(User user) throws Exception {
        String otp = generateOtp();

        //here before storing opt to the database, should be encrypted
        var otpToken = OTPToken.builder()
                .otp(encryptedOtp(otp,generateKey(),generateIv()))
                .user(user)
                .createTime(LocalDateTime.now())
                .expireTime(LocalDateTime.now().plusMinutes(15))
                .build();
        otpTokenRepository.save(otpToken);
        return otp;
    }

    private String generateOtp() {
        String otpNumbers = "0123456789";
        StringBuilder otp = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 6; i++) {
            int randomIndex = random.nextInt(otpNumbers.length());
            otp.append(otpNumbers.charAt(randomIndex));
        }
        return otp.toString();
    }
}
