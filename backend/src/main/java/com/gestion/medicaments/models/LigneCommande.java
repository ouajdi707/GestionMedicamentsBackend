package com.gestion.medicaments.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;


@Entity
@Table(name = "lignes_commande")
public class LigneCommande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Min(1)
    private Integer quantite;

    @NotNull
    @Min(0)
    private Double prixUnitaire;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "medicament_id", nullable = false)
    private Medicament medicament;

    public LigneCommande() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantite() {
        return quantite;
    }

    public void setQuantite(Integer quantite) {
        this.quantite = quantite;
    }

    public Double getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(Double prixUnitaire) {
        this.prixUnitaire = prixUnitaire;
    }

    public Commande getCommande() {
        return commande;
    }

    public void setCommande(Commande commande) {
        this.commande = commande;
    }

    public Medicament getMedicament() {
        return medicament;
    }

    public void setMedicament(Medicament medicament) {
        this.medicament = medicament;
    }

    public Double getTotal() {
        return quantite * prixUnitaire;
    }
} 