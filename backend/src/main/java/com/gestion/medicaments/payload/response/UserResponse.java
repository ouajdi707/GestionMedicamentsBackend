package com.gestion.medicaments.payload.response;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<String> roles;
    private boolean isActive;
    private String status;

    public UserResponse(Long id, String username, String email, Set<String> roles, boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.isActive = isActive;
        this.status = isActive ? "Actif" : "Inactif";
    }
} 