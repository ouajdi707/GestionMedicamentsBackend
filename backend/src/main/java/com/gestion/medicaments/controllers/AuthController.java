package com.gestion.medicaments.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestion.medicaments.payload.request.ForgotPasswordRequest;
import com.gestion.medicaments.payload.request.LoginRequest;
import com.gestion.medicaments.payload.request.ResetPasswordRequest;
import com.gestion.medicaments.payload.request.SignupRequest;
import com.gestion.medicaments.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints pour l'authentification et la gestion des utilisateurs")
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/signin")
    @Operation(summary = "Authentification d'un utilisateur")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/signup")
    @Operation(summary = "Enregistrement d'un nouvel utilisateur")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        return ResponseEntity.ok(authService.registerUser(signUpRequest));
    }
    
    @PostMapping("/forgot-password")
    @Operation(summary = "Demander un lien de réinitialisation de mot de passe")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.processForgotPassword(request.getEmail()));
    }
    
    @PostMapping("/reset-password")
    @Operation(summary = "Réinitialiser le mot de passe avec un token")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.processResetPassword(request.getToken(), request.getPassword()));
    }
} 