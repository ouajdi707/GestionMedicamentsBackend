package com.gestion.medicaments;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "API de Gestion des Médicaments",
    version = "1.0",
    description = "API REST pour la gestion des médicaments avec authentification JWT et réinitialisation de mot de passe"
))
public class MedicamentsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicamentsApplication.class, args);
    }
} 