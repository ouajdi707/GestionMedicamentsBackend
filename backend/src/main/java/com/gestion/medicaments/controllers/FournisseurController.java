package com.gestion.medicaments.controllers;

import com.gestion.medicaments.models.Fournisseur;
import com.gestion.medicaments.services.FournisseurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurController {

    @Autowired
    private FournisseurService fournisseurService;

    @GetMapping
    public List<Fournisseur> getAllFournisseurs() {
        return fournisseurService.getAllFournisseurs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fournisseur> getFournisseurById(@PathVariable Long id) {
        return fournisseurService.getFournisseurById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Fournisseur createFournisseur(@RequestBody Fournisseur fournisseur) {
        return fournisseurService.createFournisseur(fournisseur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fournisseur> updateFournisseur(@PathVariable Long id, @RequestBody Fournisseur fournisseurDetails) {
        try {
            Fournisseur updatedFournisseur = fournisseurService.updateFournisseur(id, fournisseurDetails);
            return ResponseEntity.ok(updatedFournisseur);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFournisseur(@PathVariable Long id) {
        try {
            fournisseurService.deleteFournisseur(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 