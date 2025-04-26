package com.gestion.medicaments.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.gestion.medicaments.payload.request.CreateUserRequest;
import com.gestion.medicaments.payload.request.UpdateUserRequest;
import com.gestion.medicaments.payload.response.MessageResponse;
import com.gestion.medicaments.payload.response.UserResponse;
import com.gestion.medicaments.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Endpoints pour la gestion des utilisateurs")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir la liste de tous les utilisateurs")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir un utilisateur par son ID")
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un nouvel utilisateur")
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest createRequest) {
        return userService.createUser(createRequest);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre à jour un utilisateur")
    public UserResponse updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest updateRequest) {
        return userService.updateUser(id, updateRequest);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer un compte utilisateur")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        try {
            UserResponse user = userService.activateUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Failed to activate user. " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un compte utilisateur")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            UserResponse user = userService.deactivateUser(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Failed to deactivate user. " + e.getMessage()));
        }
    }

    @PostMapping("/{id}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Basculer le statut d'un compte utilisateur (actif/inactif)")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        try {
            UserResponse user = userService.toggleUserStatus(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(new MessageResponse("Error: Failed to toggle user status. " + e.getMessage()));
        }
    }
} 