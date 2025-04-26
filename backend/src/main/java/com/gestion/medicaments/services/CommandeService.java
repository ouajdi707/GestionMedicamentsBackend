package com.gestion.medicaments.services;

import com.gestion.medicaments.models.*;
import com.gestion.medicaments.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneCommandeRepository ligneCommandeRepository;

    @Autowired
    private LivraisonRepository livraisonRepository;

    @Autowired
    private FournisseurRepository fournisseurRepository;

    @Autowired
    private EtatCommandeRepository etatCommandeRepository;

    public List<Commande> getAllCommandes() {
        return commandeRepository.findAll();
    }

    public Optional<Commande> getCommandeById(Long id) {
        return commandeRepository.findById(id);
    }

    @Transactional
    public Commande createCommande(Commande commande) {
        if (commande.getDate() == null) {
            commande.setDate(new Date());
        }
        
        // Validate fournisseur
        Fournisseur fournisseur = fournisseurRepository.findById(commande.getFournisseur().getId())
                .orElseThrow(() -> new RuntimeException("Fournisseur not found"));
        commande.setFournisseur(fournisseur);

        // Validate etat
        EtatCommande etat = etatCommandeRepository.findById(commande.getEtat().getId())
                .orElseThrow(() -> new RuntimeException("EtatCommande not found"));
        commande.setEtat(etat);

        // Save the commande first
        Commande savedCommande = commandeRepository.save(commande);

        // Handle lignes commande
        if (commande.getLignesCommande() != null) {
            commande.getLignesCommande().forEach(ligne -> {
                ligne.setCommande(savedCommande);
                ligneCommandeRepository.save(ligne);
            });
        }

        // Handle livraison if present
        if (commande.getLivraison() != null) {
            Livraison livraison = commande.getLivraison();
            livraison.setCommande(savedCommande);
            livraisonRepository.save(livraison);
        }

        return savedCommande;
    }

    @Transactional
    public Commande updateCommande(Long id, Commande commandeDetails) {
        System.out.println("Updating commande with ID: " + id);
        
        // Find the existing commande
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + id));

        // Update basic fields
        commande.setDate(commandeDetails.getDate());

        // Update fournisseur if changed
        if (commandeDetails.getFournisseur() != null) {
            Fournisseur fournisseur = fournisseurRepository.findById(commandeDetails.getFournisseur().getId())
                    .orElseThrow(() -> new RuntimeException("Fournisseur not found"));
            commande.setFournisseur(fournisseur);
        }

        // Update etat if changed
        if (commandeDetails.getEtat() != null) {
            EtatCommande etat = etatCommandeRepository.findById(commandeDetails.getEtat().getId())
                    .orElseThrow(() -> new RuntimeException("EtatCommande not found"));
            commande.setEtat(etat);
        }

        // Update lignes commande
        if (commandeDetails.getLignesCommande() != null) {
            // Remove old lines
            ligneCommandeRepository.deleteByCommandeId(id);
            
            // Add new lines
            commandeDetails.getLignesCommande().forEach(ligne -> {
                ligne.setCommande(commande);
                ligneCommandeRepository.save(ligne);
            });
            commande.setLignesCommande(commandeDetails.getLignesCommande());
        }

        // Update livraison
        if (commandeDetails.getLivraison() != null) {
            Livraison livraison = commandeDetails.getLivraison();
            livraison.setCommande(commande);
            
            // If there's an existing livraison, update it
            if (commande.getLivraison() != null) {
                livraison.setId(commande.getLivraison().getId());
                livraisonRepository.save(livraison);
            } else {
                // Create new livraison
                livraisonRepository.save(livraison);
            }
            commande.setLivraison(livraison);
        } else if (commande.getLivraison() != null) {
            // If livraison is null in the update but exists in DB, delete it
            livraisonRepository.deleteByCommandeId(id);
            commande.setLivraison(null);
        }

        // Save and return the updated commande
        return commandeRepository.save(commande);
    }

    @Transactional
    public void deleteCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commande not found with id: " + id));

        // Delete associated livraison
        if (commande.getLivraison() != null) {
            livraisonRepository.deleteByCommandeId(id);
        }

        // Delete associated lignes commande
        ligneCommandeRepository.deleteByCommandeId(id);

        // Delete the commande
        commandeRepository.delete(commande);
    }

    public List<Commande> getCommandesByFournisseur(Long fournisseurId) {
        return commandeRepository.findByFournisseurId(fournisseurId);
    }

    public List<Commande> getCommandesByEtat(Long etatId) {
        return commandeRepository.findByEtatId(etatId);
    }
} 