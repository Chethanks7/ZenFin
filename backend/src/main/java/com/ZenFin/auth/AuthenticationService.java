package com.ZenFin.auth;

import com.ZenFin.email.EmailService;
import com.ZenFin.email.EmailTemplateName;
import com.ZenFin.email.MailModel;
import com.ZenFin.role.RoleRepository;
import com.ZenFin.security.EncryptionKey;
import com.ZenFin.security.UserOtpStatus;
import com.ZenFin.security.UserOtpStatusRepository;
import com.ZenFin.user.OTPToken;
import com.ZenFin.user.OTPTokenRepository;
import com.ZenFin.user.User;
import com.ZenFin.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OTPTokenRepository otpTokenRepository;
    private final EncryptionKey encryptionKey;
    private final EmailService emailService;

    private static final byte MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCKOUT_TIME = 60 * 1000L;
    private final UserOtpStatusRepository userOtpStatusRepository;


    @Value("${application.security.secreteName}")
    private String secretName;


    @Value("${spring.mailing.frontend.activation-url}")
    private String activationUrl;

    public User register(@Valid RegistrationRequest registration) throws Exception {
        var role = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalArgumentException("Role is not in database"));

        var user = User.builder()
                .firstName(registration.getFirstname())
                .lastName(registration.getLastname())
                .email(registration.getEmail())
                .password(registration.getPassword())// later add encoding the password
                .accountLocked(false)
                .roles(new ArrayList<>(List.of(role)))
                .build();


        sendEmailToVerify(user);
        var userLockStatus = UserOtpStatus.builder()
                .userID(user.getId())
                .maxFailedAttempts(MAX_FAILED_ATTEMPTS)
                .build();
        userOtpStatusRepository.save(userLockStatus);
       return  userRepository.save(user);

    }

    private void sendEmailToVerify(User user) throws Exception {

        var newOtp = generateAndSaveNewOtp(user);

        var mailInfo = MailModel.builder()
                .to(user.getEmail())
                .username(user.fullName())
                .templateName(EmailTemplateName.ACTIVATE_ACCOUNT)
                .activationCode(newOtp)
                .subject("Activate account ")
                .build();
        emailService.sendMail(
                mailInfo
                , 587
        );
    }


    public IvParameterSpec generateIv() {
        byte[] keyBytes = new byte[16]; // For 128-bit AES
        new SecureRandom().nextBytes(keyBytes);
        return new IvParameterSpec(keyBytes);

    }

    public SecretKey generateKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // 256-bit AES key
        return keyGen.generateKey();
    }

    public String encryptedOtp(String otp, Key key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        byte[] encryptedOtp = cipher.doFinal(otp.getBytes());
        return Base64.getEncoder().encodeToString(encryptedOtp);
    }

    public Key getKey() throws IOException {
        String base64EncodedKey = encryptionKey.getEncryptionKey(secretName);
        byte[] keyBytes = Base64.getDecoder().decode(base64EncodedKey);
        return new SecretKeySpec(keyBytes, "AES");
    }


    private String generateAndSaveNewOtp(User user) throws Exception {
        String otp = generateOtp();


        //here before storing opt to the database, should be encrypted

        IvParameterSpec iv = generateIv();
        Key key = getKey();
        var otpToken = OTPToken.builder()
                .otp(encryptedOtp(otp,
                        key
                        , iv)
                )
                .key(Base64.getEncoder().encodeToString(key.getEncoded()))
                .ivParameterSpec(Base64.getEncoder().encodeToString(iv.getIV()))
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

    public String verifyOtp(String otp, String userId) throws MessagingException {


        var userOtpStatus = userOtpStatusRepository.findByUserID(userId);

        if (!isUserLockedOut(userId)) {
            OTPToken otpToken = otpTokenRepository.findByOtp(otp)
                    .orElseThrow(() -> new IllegalArgumentException("otp is not valid " + otp));

            var user = otpToken.getUser();


            // String decryptOtp= decryptOtp(otpToken.getOtp(),otpToken.getIvParameterSpec(), otpToken.getKey());


            byte maxAttempts = userOtpStatus.getMaxFailedAttempts();
            if (
                    !otpToken.getOtp().trim().equals(otp.trim()) ||
                            otpToken.getExpireTime().plusMinutes(15).isBefore(LocalDateTime.now())
            ) {
                if (userOtpStatus.getMaxFailedAttempts() > 0) {
                    String newOtp = generateOtp();
                    var mailInfo = MailModel.builder()
                            .to(user.getEmail())
                            .username(user.fullName())
                            .templateName(EmailTemplateName.ACTIVATE_ACCOUNT)
                            .activationCode(newOtp)
                            .subject("Activate account ")
                            .build();
                    userOtpStatus.setMaxFailedAttempts(--maxAttempts);
                    userOtpStatusRepository.save(userOtpStatus);
                    otpTokenRepository.save(otpToken);
                    emailService.sendMail(
                            mailInfo
                            , 587
                    );
                    return "email sent to registered email";
                } else {
                    otpTokenRepository.save(otpToken);
                    userOtpStatus.setMaxFailedAttempts(MAX_FAILED_ATTEMPTS);
                    userOtpStatus.setLockTime(LocalDateTime.now().plusMinutes(LOCKOUT_TIME));
                    return "you attempted maximum failed attempts. Try after one minute ";

                }
            }

            user.setEnabled(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            return "email verification success now you can login";
        } else {
            return "Your account has been locked due to multiple failed login attempts. Please try again after " +
                    userOtpStatus.getLockTime() + " minutes";
        }
    }

    private boolean isUserLockedOut(String id) {
        UserOtpStatus status = userOtpStatusRepository.findByUserID(id);
        if(status.getLockTime() !=null) return status.getLockTime().isBefore(LocalDateTime.now().minusMinutes(1));

        return false;
    }


}
