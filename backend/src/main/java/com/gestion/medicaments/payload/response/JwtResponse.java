package com.gestion.medicaments.payload.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String type;
    private Long id;
    private String username;
    private String email;
    private List<String> roles;
} 