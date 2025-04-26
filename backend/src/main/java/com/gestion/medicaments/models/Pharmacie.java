package com.gestion.medicaments.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pharmacies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pharmacie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String nom;

    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 200)
    private String adresse;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "proprietaire_id", nullable = false)
    private User proprietaire;

    @NotBlank(message = "Le numéro est obligatoire")
    @Size(max = 20)
    private String numero;

    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100)
    private String ville;

    @NotBlank(message = "L'email est obligatoire")
    @Size(max = 100)
    @Email(message = "L'email doit être valide")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Disponibilite disponibilite;
} 