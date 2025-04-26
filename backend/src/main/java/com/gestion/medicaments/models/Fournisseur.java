package com.gestion.medicaments.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;



@Entity
@Table(name = "fournisseurs")
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String nom;

    @NotBlank
    @Size(max = 100)
    private String adresse;

    private int telephone;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    public Fournisseur() {
    }

    public Fournisseur(String nom, String adresse, int telephone, String email) {
        this.nom = nom;
        this.adresse = adresse;
        this.telephone = telephone;
        this.email = email;
    }

    // Getters and Setters
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getTelephone() {
        return telephone;
    }

    public void setTelephone(int telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
} 