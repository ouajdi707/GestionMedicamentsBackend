package com.gestion.medicaments.controllers;

import com.gestion.medicaments.models.Commande;
import com.gestion.medicaments.services.CommandeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    @Autowired
    private CommandeService commandeService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN') or hasRole('MEDECIN')")
    public List<Commande> getAllCommandes() {
        return commandeService.getAllCommandes();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN') or hasRole('MEDECIN')")
    public ResponseEntity<Commande> getCommandeById(@PathVariable Long id) {
        return commandeService.getCommandeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN')")
    public ResponseEntity<Commande> createCommande(@Valid @RequestBody Commande commande) {
        try {
            Commande newCommande = commandeService.createCommande(commande);
            return ResponseEntity.ok(newCommande);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN')")
    public ResponseEntity<Commande> updateCommande(@PathVariable Long id,@Valid @RequestBody Commande commandeDetails) {
        try {
            Commande updatedCommande = commandeService.updateCommande(id, commandeDetails);
            return ResponseEntity.ok(updatedCommande);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCommande(@PathVariable Long id) {
        try {
            commandeService.deleteCommande(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/fournisseur/{fournisseurId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN') or hasRole('MEDECIN')")
    public List<Commande> getCommandesByFournisseur(@PathVariable Long fournisseurId) {
        return commandeService.getCommandesByFournisseur(fournisseurId);
    }

    @GetMapping("/etat/{etatId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('PHARMACIEN') or hasRole('MEDECIN')")
    public List<Commande> getCommandesByEtat(@PathVariable Long etatId) {
        return commandeService.getCommandesByEtat(etatId);
    }
} 