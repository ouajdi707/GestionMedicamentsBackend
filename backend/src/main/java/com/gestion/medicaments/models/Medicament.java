package com.gestion.medicaments.models;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Lob;

@Entity
@Table(name = "medicaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom ne peut pas être vide")
    private String nom;

    @Column(length = 1000)
    private String description;

    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private int quantite;

    @NotNull(message = "La date d'expiration est obligatoire")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateExpiration;

    private String categorie;
    
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private double prix;

    @Lob
    @Column(name = "image_data", columnDefinition = "LONGBLOB")
    private byte[] imageData;

    @Column(name = "image_type")
    private String imageType;

    @Transient
    private double totalPrice;

    public double getTotalPrice() {
        return this.prix * this.quantite;
    }
} 