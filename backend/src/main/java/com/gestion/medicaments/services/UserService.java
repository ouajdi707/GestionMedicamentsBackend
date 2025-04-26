package com.gestion.medicaments.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;

import com.gestion.medicaments.exceptions.ResourceNotFoundException;
import com.gestion.medicaments.models.ERole;
import com.gestion.medicaments.models.Role;
import com.gestion.medicaments.models.User;
import com.gestion.medicaments.payload.request.CreateUserRequest;
import com.gestion.medicaments.payload.request.UpdateUserRequest;
import com.gestion.medicaments.payload.response.UserResponse;
import com.gestion.medicaments.repositories.RoleRepository;
import com.gestion.medicaments.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {
        // No need for role conversion anymore since ROLE_USER is removed
        try {
            // Verify ROLE_PATIENT exists
            roleRepository.findByName(ERole.ROLE_PATIENT)
                    .orElseThrow(() -> new RuntimeException("Error: Patient role not found."));
        } catch (Exception e) {
            System.err.println("Warning during role verification: " + e.getMessage());
        }
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToUserResponse(user);
    }
    
    @Transactional
    public UserResponse createUser(CreateUserRequest createRequest) {
        // Check if username is already taken
        if (userRepository.existsByUsername(createRequest.getUsername())) {
            throw new RuntimeException("Username is already taken!");
        }

        // Check if email is already in use
        if (userRepository.existsByEmail(createRequest.getEmail())) {
            throw new RuntimeException("Email is already in use!");
        }

        // Create new user
        User user = new User(
            createRequest.getUsername(),
            createRequest.getEmail(),
            passwordEncoder.encode(createRequest.getPassword())
        );

        // Set roles
        Set<Role> roles = new HashSet<>();
        if (createRequest.getRoles() == null || createRequest.getRoles().isEmpty()) {
            // Default to ROLE_PATIENT
            Role patientRole = roleRepository.findByName(ERole.ROLE_PATIENT)
                    .orElseThrow(() -> new RuntimeException("Error: Patient role not found."));
            roles.add(patientRole);
        } else {
            createRequest.getRoles().forEach(roleName -> {
                try {
                    // Remove ROLE_ prefix if present and convert to uppercase
                    String processedRole = roleName.replace("ROLE_", "").toUpperCase();
                    ERole roleEnum = ERole.valueOf("ROLE_" + processedRole);
                    Role role = roleRepository.findByName(roleEnum)
                            .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " not found."));
                    roles.add(role);
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Error: Role " + roleName + " is not valid. Valid roles are: ROLE_ADMIN, ROLE_PHARMACIEN, ROLE_MEDECIN, ROLE_PATIENT");
                }
            });
        }
        
        user.setRoles(roles);
        // Ensure account is inactive by default
        user.setActive(false);
        User savedUser = userRepository.save(user);
        
        return convertToUserResponse(savedUser);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest updateRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        if (updateRequest.getUsername() != null) {
            if (userRepository.existsByUsername(updateRequest.getUsername()) && 
                !user.getUsername().equals(updateRequest.getUsername())) {
                throw new RuntimeException("Username is already taken!");
            }
            user.setUsername(updateRequest.getUsername());
        }

        if (updateRequest.getEmail() != null) {
            if (userRepository.existsByEmail(updateRequest.getEmail()) && 
                !user.getEmail().equals(updateRequest.getEmail())) {
                throw new RuntimeException("Email is already in use!");
            }
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
        }

        if (updateRequest.getRoles() != null && !updateRequest.getRoles().isEmpty()) {
            Set<Role> roles = updateRequest.getRoles().stream()
                    .map(roleName -> {
                        try {
                            // Remove ROLE_ prefix if present and convert to uppercase
                            String processedRole = roleName.replace("ROLE_", "").toUpperCase();
                            ERole roleEnum = ERole.valueOf("ROLE_" + processedRole);
                            return roleRepository.findByName(roleEnum)
                                    .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " is not found."));
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException("Error: Role " + roleName + " is not valid. Valid roles are: ROLE_ADMIN, ROLE_PHARMACIEN, ROLE_MEDECIN, ROLE_PATIENT");
                        }
                    })
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public UserResponse activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(true);
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(false);
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    @Transactional
    public UserResponse toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(!user.isActive());
        User updatedUser = userRepository.save(user);
        return convertToUserResponse(updatedUser);
    }

    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toSet()),
                user.isActive()
        );
    }
} 