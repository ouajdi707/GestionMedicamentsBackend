package com.gestion.medicaments.services;

import com.gestion.medicaments.models.Pharmacie;
import com.gestion.medicaments.models.Disponibilite;
import com.gestion.medicaments.models.User;
import com.gestion.medicaments.models.ERole;
import com.gestion.medicaments.models.Role;
import com.gestion.medicaments.repositories.PharmacieRepository;
import com.gestion.medicaments.repositories.UserRepository;
import com.gestion.medicaments.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PharmacieService {
    
    @Autowired
    private PharmacieRepository pharmacieRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Pharmacie> getAllPharmacies() {
        return pharmacieRepository.findAll();
    }

    public List<User> getAllPharmaciens() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName() == ERole.ROLE_PHARMACIEN))
                .collect(Collectors.toList());
    }

    public Optional<Pharmacie> getPharmacieById(Long id) {
        return pharmacieRepository.findById(id);
    }

    public List<Pharmacie> getPharmaciesByVille(String ville) {
        return pharmacieRepository.findByVille(ville);
    }

    public List<Pharmacie> getPharmaciesByDisponibilite(Disponibilite disponibilite) {
        return pharmacieRepository.findByDisponibilite(disponibilite);
    }

    public List<Pharmacie> getPharmaciesByVilleAndDisponibilite(String ville, Disponibilite disponibilite) {
        return pharmacieRepository.findByVilleAndDisponibilite(ville, disponibilite);
    }

    public Pharmacie createPharmacie(Pharmacie pharmacie) {
        if (pharmacieRepository.existsByNomAndVille(pharmacie.getNom(), pharmacie.getVille())) {
            throw new RuntimeException("Une pharmacie avec ce nom existe déjà dans cette ville");
        }

        User proprietaire = userRepository.findById(pharmacie.getProprietaire().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        boolean isPharmacien = proprietaire.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_PHARMACIEN);
        if (!isPharmacien) {
            throw new RuntimeException("L'utilisateur sélectionné n'est pas un pharmacien");
        }

        pharmacie.setProprietaire(proprietaire);
        return pharmacieRepository.save(pharmacie);
    }

    public Pharmacie updatePharmacie(Long id, Pharmacie pharmacieDetails) {
        Pharmacie pharmacie = pharmacieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacie not found with id: " + id));

        // Vérifier si le nouveau nom existe déjà dans la même ville (sauf pour la pharmacie actuelle)
        if (!pharmacie.getNom().equals(pharmacieDetails.getNom()) && 
            !pharmacie.getVille().equals(pharmacieDetails.getVille()) &&
            pharmacieRepository.existsByNomAndVille(pharmacieDetails.getNom(), pharmacieDetails.getVille())) {
            throw new RuntimeException("Une pharmacie avec ce nom existe déjà dans cette ville");
        }

        // Vérifier si le nouveau propriétaire existe et est un pharmacien
        if (pharmacieDetails.getProprietaire() != null) {
            User newProprietaire = userRepository.findById(pharmacieDetails.getProprietaire().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

            boolean isPharmacien = newProprietaire.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_PHARMACIEN);
            if (!isPharmacien) {
                throw new RuntimeException("L'utilisateur sélectionné n'est pas un pharmacien");
            }
            pharmacie.setProprietaire(newProprietaire);
        }

        pharmacie.setNom(pharmacieDetails.getNom());
        pharmacie.setAdresse(pharmacieDetails.getAdresse());
        pharmacie.setNumero(pharmacieDetails.getNumero());
        pharmacie.setVille(pharmacieDetails.getVille());
        pharmacie.setEmail(pharmacieDetails.getEmail());
        pharmacie.setDisponibilite(pharmacieDetails.getDisponibilite());

        return pharmacieRepository.save(pharmacie);
    }

    public void deletePharmacie(Long id) {
        Pharmacie pharmacie = pharmacieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacie not found with id: " + id));
        pharmacieRepository.delete(pharmacie);
    }

    public void toggleDisponibilite(Long id) {
        Pharmacie pharmacie = pharmacieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pharmacie not found with id: " + id));
        
        pharmacie.setDisponibilite(
            pharmacie.getDisponibilite() == Disponibilite.JOUR ? 
            Disponibilite.NUIT : Disponibilite.JOUR
        );
        
        pharmacieRepository.save(pharmacie);
    }
} 