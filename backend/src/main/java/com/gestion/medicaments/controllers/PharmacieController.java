package com.gestion.medicaments.controllers;

import com.gestion.medicaments.models.Pharmacie;
import com.gestion.medicaments.models.Disponibilite;
import com.gestion.medicaments.models.User;
import com.gestion.medicaments.services.PharmacieService;
import com.gestion.medicaments.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/pharmacies")
public class PharmacieController {

    @Autowired
    private PharmacieService pharmacieService;

    @GetMapping
    public List<Pharmacie> getAllPharmacies() {
        return pharmacieService.getAllPharmacies();
    }

    @GetMapping("/pharmaciens")
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllPharmaciens() {
        return pharmacieService.getAllPharmaciens();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pharmacie> getPharmacieById(@PathVariable Long id) {
        return pharmacieService.getPharmacieById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacie not found with id: " + id));
    }

    @GetMapping("/ville/{ville}")
    public List<Pharmacie> getPharmaciesByVille(@PathVariable String ville) {
        return pharmacieService.getPharmaciesByVille(ville);
    }

    @GetMapping("/disponibilite/{disponibilite}")
    public List<Pharmacie> getPharmaciesByDisponibilite(@PathVariable Disponibilite disponibilite) {
        return pharmacieService.getPharmaciesByDisponibilite(disponibilite);
    }

    @GetMapping("/search")
    public List<Pharmacie> getPharmaciesByVilleAndDisponibilite(
            @RequestParam String ville,
            @RequestParam Disponibilite disponibilite) {
        return pharmacieService.getPharmaciesByVilleAndDisponibilite(ville, disponibilite);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Pharmacie createPharmacie(@Valid @RequestBody Pharmacie pharmacie) {
        return pharmacieService.createPharmacie(pharmacie);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Pharmacie updatePharmacie(@PathVariable Long id, @Valid @RequestBody Pharmacie pharmacieDetails) {
        return pharmacieService.updatePharmacie(id, pharmacieDetails);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deletePharmacie(@PathVariable Long id) {
        pharmacieService.deletePharmacie(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/toggle-disponibilite")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN')")
    public ResponseEntity<?> toggleDisponibilite(@PathVariable Long id) {
        pharmacieService.toggleDisponibilite(id);
        return ResponseEntity.ok().build();
    }
} 