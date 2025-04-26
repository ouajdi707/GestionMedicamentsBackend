package com.gestion.medicaments.services;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.medicaments.exceptions.TokenRefreshException;
import com.gestion.medicaments.models.ERole;
import com.gestion.medicaments.models.Role;
import com.gestion.medicaments.models.User;
import com.gestion.medicaments.payload.request.LoginRequest;
import com.gestion.medicaments.payload.request.SignupRequest;
import com.gestion.medicaments.payload.response.JwtResponse;
import com.gestion.medicaments.payload.response.MessageResponse;
import com.gestion.medicaments.repositories.RoleRepository;
import com.gestion.medicaments.repositories.UserRepository;
import com.gestion.medicaments.security.jwt.JwtUtils;
import com.gestion.medicaments.security.services.UserDetailsImpl;

@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    EmailService emailService;
    
    @Value("${app.reset-password.token.expiration}")
    private long resetPasswordTokenExpirationMs;
    
    @Value("${app.url}")
    private String appUrl;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        try {
            // Find user first to check if account exists and is active
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + loginRequest.getUsername()));

            if (!user.isActive()) {
                throw new RuntimeException("Account is not activated. Please contact an administrator.");
            }

            // Check if user has ROLE_USER and update it to ROLE_PATIENT
            boolean needsRoleUpdate = user.getRoles().stream()
                    .anyMatch(role -> role.getName().name().equals("ROLE_USER"));
            
            if (needsRoleUpdate) {
                Set<Role> newRoles = new HashSet<>();
                Role patientRole = roleRepository.findByName(ERole.ROLE_PATIENT)
                        .orElseThrow(() -> new RuntimeException("Error: Patient role not found."));
                newRoles.add(patientRole);
                user.setRoles(newRoles);
                user = userRepository.save(user);
            }

            // Attempt authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            // Set authentication in context
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            String jwt = jwtUtils.generateJwtToken(authentication);

            // Get user details
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            
            // Get user roles
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return new JwtResponse(jwt, 
                                "Bearer", 
                                userDetails.getId(), 
                                userDetails.getUsername(), 
                                userDetails.getEmail(),
                                roles);
        } catch (UsernameNotFoundException e) {
            throw new RuntimeException("Error: Username or password is incorrect.");
        } catch (Exception e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            throw new RuntimeException("Error: At least one role must be specified (ROLE_ADMIN, ROLE_PHARMACIEN, ROLE_MEDECIN, or ROLE_PATIENT).");
        }

        strRoles.forEach(role -> {
            try {
                // Remove ROLE_ prefix if present
                String processedRole = role.replace("ROLE_", "").toUpperCase();
                ERole roleEnum = ERole.valueOf("ROLE_" + processedRole);
                Role userRole = roleRepository.findByName(roleEnum)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                roles.add(userRole);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Error: Role " + role + " is not valid. Valid roles are: ROLE_ADMIN, ROLE_PHARMACIEN, ROLE_MEDECIN, ROLE_PATIENT");
            }
        });

        user.setRoles(roles);
        user.setActive(false);
        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }
    
    public MessageResponse processForgotPassword(String email) {
        return userRepository.findByEmail(email)
                .map(user -> {
                    String token = UUID.randomUUID().toString();
                    user.setResetToken(token);
                    user.setResetTokenExpiration(LocalDateTime.now().plusSeconds(resetPasswordTokenExpirationMs / 1000));
                    userRepository.save(user);
                    
                    String resetUrl = appUrl + "/reset-password?token=" + token;
                    emailService.sendPasswordResetEmail(user.getEmail(), resetUrl);
                    
                    return new MessageResponse("Password reset link has been sent to your email");
                })
                .orElse(new MessageResponse("If your email exists in our system, you will receive a password reset link"));
    }
    
    public MessageResponse processResetPassword(String token, String newPassword) {
        return userRepository.findByResetToken(token)
                .map(user -> {
                    if (!user.isResetTokenValid()) {
                        throw new TokenRefreshException(token, "Reset password token was expired!");
                    }
                    
                    user.setPassword(encoder.encode(newPassword));
                    user.setResetToken(null);
                    user.setResetTokenExpiration(null);
                    userRepository.save(user);
                    
                    return new MessageResponse("Password has been reset successfully");
                })
                .orElseThrow(() -> new TokenRefreshException(token, "Invalid reset password token!"));
    }

    @Transactional
    public User activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));
        user.setActive(true);
        return userRepository.save(user);
    }

    @Transactional
    public User deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + id));
        user.setActive(false);
        return userRepository.save(user);
    }
} 