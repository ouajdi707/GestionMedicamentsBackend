package com.gestion.medicaments.models;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "etats_commande")
public class EtatCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String nom;

    public EtatCommande() {
    }

    public EtatCommande(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }
} 