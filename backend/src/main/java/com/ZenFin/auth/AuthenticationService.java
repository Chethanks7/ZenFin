package com.ZenFin.auth;

import com.ZenFin.email.EmailService;
import com.ZenFin.email.EmailTemplateName;
import com.ZenFin.email.MailModel;
import com.ZenFin.role.RoleRepository;
import com.ZenFin.security.EncryptionKey;
import com.ZenFin.security.JwtService;
import com.ZenFin.security.UserOtpStatus;
import com.ZenFin.security.UserOtpStatusRepository;
import com.ZenFin.user.OTPToken;
import com.ZenFin.user.OTPTokenRepository;
import com.ZenFin.user.User;
import com.ZenFin.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OTPTokenRepository otpTokenRepository;
    private final EncryptionKey encryptionKey;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;

    private static final byte MAX_FAILED_ATTEMPTS = 5;
    private static final long MAX_LOCKOUT_TIME = 5 * 60 * 1000L;
    private final UserOtpStatusRepository userOtpStatusRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;


    @Value("${application.security.secreteName}")
    private String secretName;


    @Value("${spring.mailing.frontend.activation-url}")
    private String activationUrl;

    public User register(@Valid RegistrationRequest registration) throws Exception {
        var role = roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Role is not in database"));

        if (userRepository.findByEmail(registration.getEmail()).isPresent())
            throw new Exception("Email is already registered. you can login ");

        var user = User.builder()
                .firstName(registration.getFirstname())
                .lastName(registration.getLastname())
                .email(registration.getEmail())
                .password(passwordEncoder.encode(registration.getPassword()))// later add encoding the password
                .accountLocked(false)
                .roles(new ArrayList<>(List.of(role)))
                .build();


        sendEmailToVerify(user);
        var userLockStatus = UserOtpStatus.builder()
                .userID(user.getUserId())
                .maxFailedAttempts(MAX_FAILED_ATTEMPTS)
                .build();
        userOtpStatusRepository.save(userLockStatus);
        return userRepository.save(user);

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


        UserOtpStatus userOtpStatus = userOtpStatusRepository.findByUserID(userId)
                .orElseThrow(() -> new EntityNotFoundException("User is not found. Please register first."));


        if (isUserLockedOut(userId)) {
            OTPToken otpToken = otpTokenRepository.findByOtp(otp)
                    .orElseThrow(() -> new EntityNotFoundException("otp is not valid " + otp));

            var user = otpToken.getUser();
            byte maxAttempts = userOtpStatus.getMaxFailedAttempts();
            if (LocalDateTime.now().isAfter(otpToken.getExpireTime())) {
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
                    userOtpStatus.setMaxFailedAttempts(MAX_FAILED_ATTEMPTS);
                    userOtpStatus.setLockTime(LocalDateTime.now().plusMinutes(MAX_LOCKOUT_TIME));
                    userOtpStatusRepository.save(userOtpStatus);
                    return "you attempted maximum failed attempts. Try after " + userOtpStatus.getLockTime().getMinute() + " minute ";
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
        UserOtpStatus status = userOtpStatusRepository.findByUserID(id)
                .orElseThrow(() -> new EntityNotFoundException("user not found. Please register first"));
        if (status.getLockTime() != null)
            return !status.getLockTime().isBefore(LocalDateTime.now().minusMinutes(MAX_LOCKOUT_TIME));

        return true;
    }

    public String resendOtp(String userId) throws Exception {
        UserOtpStatus status = userOtpStatusRepository.findByUserID(userId)
                .orElseThrow(() -> new EntityNotFoundException("user not found. Please register first"));
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("something went wrong, please try again"));
        if (isUserLockedOut(userId)) {
            if (user.getResendAttempts() < 5) {
                String otp = generateAndSaveNewOtp(user);
                var mailInfo = MailModel.builder()
                        .to(user.getEmail())
                        .username(user.fullName())
                        .templateName(EmailTemplateName.ACTIVATE_ACCOUNT)
                        .activationCode(otp)
                        .subject("Activate account ")
                        .build();
                emailService.sendMail(
                        mailInfo, 587
                );
                user.setLastEmailSentTime(LocalDateTime.now());
                user.setResendAttempts((byte) (user.getResendAttempts() + 1));
                userRepository.save(user);
                return "email sent to your registered email";
            } else {
                status.setLockTime(LocalDateTime.now().plusMinutes(2));
                user.setResendAttempts((byte) 0);
                userOtpStatusRepository.save(status);
                userRepository.save(user);
                return "Your account has been locked due to multiple failed attempts. Please try again after " +
                        status.getLockTime().getMinute() + " minutes";
            }
        }
        return "Your account has been locked due to multiple failed attempts. Please try again after " +
                status.getLockTime() + " minutes";
    }

    public EmailAuthResponse verifyEmail(String email) {

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found. Please register first"));

        if (!isUserLockedOut(user.getUserId())) {
            System.err.println("Bug");
            return buildResponse(email, HttpStatus.FORBIDDEN,
                    "Your account is locked due to multiple failed login attempts. Please reset your password or contact support.");
        }

        if (!user.isEnabled()) {
            return buildResponse(email, HttpStatus.BAD_REQUEST,
                    "This email address is not yet verified. Please verify your email before logging in.");
        }
        user.setEmailVerified(true);
        userRepository.save(user);
        return buildResponse(email, HttpStatus.OK,
                "Email verified successfully. Now enter your password.");
    }

    private EmailAuthResponse buildResponse(String email, HttpStatus status, String message) {
        return EmailAuthResponse.builder()
                .email(email)
                .message(message)
                .status(status)
                .build();
    }


    public Object authenticate(@NotNull AuthenticationRequest request) throws IOException, ExecutionException, InterruptedException {
        long startTime = System.currentTimeMillis();
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("user not found"));

        if (!user.isEnabled()) {
            return buildResponse(request.getEmail(), HttpStatus.BAD_REQUEST,
                    "This account is not enabled, verify your email with otp");
        }

        if (!isUserLockedOut(user.getUserId())) {
            return buildResponse(request.getEmail(), HttpStatus.FORBIDDEN,
                    "Your account is locked due to multiple failed login attempts. Please reset your password or contact support.");
        }
        if (!user.isEmailVerified()) {
            return buildResponse(request.getEmail(), HttpStatus.UNAUTHORIZED,
                    "verify your email before entering password");
        }


        CompletableFuture<Boolean> authFuture = CompletableFuture.supplyAsync(() -> {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            return true;
        });

        CompletableFuture<String> jwtFuture = CompletableFuture.supplyAsync(() -> {
            var claims = new HashMap<String, Object>();
            claims.put("fullName", user.fullName());
            claims.put("UserID", user.getUserId());
            try {
                return jwtService.generateToken(claims, user);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });



        CompletableFuture.allOf(authFuture, jwtFuture).join();
        long endTime = System.currentTimeMillis();
        System.err.println("Step took"+(endTime - startTime)+"ms");
        updateUserProfile(user);

        return AuthenticationResponse.builder()
                .status(HttpStatus.OK)
                .message("Successfully logged in")
                .token(jwtFuture.get())
                .build();
    }

    @Async
    public void updateUserProfile(User user) {
        user.setResendAttempts((byte) 0);
        user.setFailedLoginAttempts((byte) 0);
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }
}
