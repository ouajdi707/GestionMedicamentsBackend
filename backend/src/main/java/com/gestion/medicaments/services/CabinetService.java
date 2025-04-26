package com.gestion.medicaments.services;

import com.gestion.medicaments.models.Cabinet;
import com.gestion.medicaments.models.User;
import com.gestion.medicaments.models.ERole;
import com.gestion.medicaments.repositories.CabinetRepository;
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
public class CabinetService {

    @Autowired
    private CabinetRepository cabinetRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Cabinet> getAllCabinets() {
        return cabinetRepository.findAll();
    }

    public List<User> getAllMedecins() {
        return userRepository.findAll().stream()
                .filter(user -> user.getRoles().stream()
                        .anyMatch(role -> role.getName() == ERole.ROLE_MEDECIN))
                .collect(Collectors.toList());
    }

    public Optional<Cabinet> getCabinetById(Long id) {
        return cabinetRepository.findById(id);
    }

    public Cabinet createCabinet(Cabinet cabinet) {
        User medecin = userRepository.findById(cabinet.getMedecin().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        boolean isMedecin = medecin.getRoles().stream()
                .anyMatch(role -> role.getName() == ERole.ROLE_MEDECIN);
        if (!isMedecin) {
            throw new RuntimeException("L'utilisateur sélectionné n'est pas un médecin");
        }

        cabinet.setMedecin(medecin);
        return cabinetRepository.save(cabinet);
    }

    public Cabinet updateCabinet(Long id, Cabinet cabinetDetails) {
        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cabinet not found with id: " + id));

        if (cabinetDetails.getMedecin() != null) {
            User newMedecin = userRepository.findById(cabinetDetails.getMedecin().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

            boolean isMedecin = newMedecin.getRoles().stream()
                    .anyMatch(role -> role.getName() == ERole.ROLE_MEDECIN);
            if (!isMedecin) {
                throw new RuntimeException("L'utilisateur sélectionné n'est pas un médecin");
            }
            cabinet.setMedecin(newMedecin);
        }

        cabinet.setNom(cabinetDetails.getNom());
        cabinet.setAdresse(cabinetDetails.getAdresse());
        cabinet.setNumero(cabinetDetails.getNumero());
        cabinet.setVille(cabinetDetails.getVille());
        cabinet.setEmail(cabinetDetails.getEmail());
        cabinet.setSpecialite(cabinetDetails.getSpecialite());

        return cabinetRepository.save(cabinet);
    }

    public void deleteCabinet(Long id) {
        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cabinet not found with id: " + id));
        cabinetRepository.delete(cabinet);
    }

    public List<Cabinet> getCabinetsByVille(String ville) {
        return cabinetRepository.findByVille(ville);
    }

    public List<Cabinet> getCabinetsBySpecialite(String specialite) {
        return cabinetRepository.findBySpecialite(specialite);
    }

    public List<Cabinet> getCabinetsByMedecinId(Long medecinId) {
        return cabinetRepository.findByMedecinId(medecinId);
    }
} 