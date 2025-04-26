package com.gestion.medicaments.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.gestion.medicaments.models.Medicament;
import com.gestion.medicaments.services.MedicamentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/medicaments")
@CrossOrigin(origins = "*")
@Tag(name = "Medicaments", description = "Endpoints pour la gestion des médicaments")
@SecurityRequirement(name = "bearerAuth")
public class MedicamentController {

    @Autowired
    private MedicamentService medicamentService;

    @GetMapping
    @Operation(summary = "Récupérer tous les médicaments")
    public ResponseEntity<List<Medicament>> getAllMedicaments() {
        return ResponseEntity.ok(medicamentService.getAllMedicaments());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un médicament par son ID")
    public ResponseEntity<Medicament> getMedicamentById(@PathVariable Long id) {
        return medicamentService.getMedicamentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Créer un nouveau médicament")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN')")
    public ResponseEntity<Medicament> createMedicament(
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("quantite") int quantite,
            @RequestParam("dateExpiration") String dateExpiration,
            @RequestParam("categorie") String categorie,
            @RequestParam("prix") double prix,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Medicament medicament = new Medicament();
            medicament.setNom(nom);
            medicament.setDescription(description);
            medicament.setQuantite(quantite);
            medicament.setDateExpiration(java.time.LocalDate.parse(dateExpiration));
            medicament.setCategorie(categorie);
            medicament.setPrix(prix);

            if (image != null && !image.isEmpty()) {
                medicament.setImageData(image.getBytes());
                medicament.setImageType(image.getContentType());
            }

            return ResponseEntity.ok(medicamentService.saveMedicament(medicament));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un médicament")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN')")
    public ResponseEntity<Medicament> updateMedicament(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam("description") String description,
            @RequestParam("quantite") int quantite,
            @RequestParam("dateExpiration") String dateExpiration,
            @RequestParam("categorie") String categorie,
            @RequestParam("prix") double prix,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            return medicamentService.getMedicamentById(id)
                    .map(existingMedicament -> {
                        existingMedicament.setNom(nom);
                        existingMedicament.setDescription(description);
                        existingMedicament.setQuantite(quantite);
                        existingMedicament.setDateExpiration(java.time.LocalDate.parse(dateExpiration));
                        existingMedicament.setCategorie(categorie);
                        existingMedicament.setPrix(prix);

                        if (image != null && !image.isEmpty()) {
                            try {
                                existingMedicament.setImageData(image.getBytes());
                                existingMedicament.setImageType(image.getContentType());
                            } catch (IOException e) {
                                return null;
                            }
                        }

                        return medicamentService.saveMedicament(existingMedicament);
                    })
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un médicament")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN')")
    public ResponseEntity<Void> deleteMedicament(@PathVariable Long id) {
        medicamentService.deleteMedicament(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des médicaments par nom")
    public ResponseEntity<List<Medicament>> searchMedicaments(@RequestParam String name) {
        return ResponseEntity.ok(medicamentService.searchMedicamentsByName(name));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Obtenir des médicaments par catégorie")
    public ResponseEntity<List<Medicament>> getMedicamentsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(medicamentService.getMedicamentsByCategory(category));
    }

    @GetMapping("/expired")
    @Operation(summary = "Obtenir tous les médicaments expirés")
    public ResponseEntity<List<Medicament>> getExpiredMedicaments() {
        return ResponseEntity.ok(medicamentService.getExpiredMedicaments());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Obtenir les médicaments en stock bas")
    public ResponseEntity<List<Medicament>> getLowStockMedicaments(@RequestParam(defaultValue = "5") int threshold) {
        return ResponseEntity.ok(medicamentService.getLowStockMedicaments(threshold));
    }
} 