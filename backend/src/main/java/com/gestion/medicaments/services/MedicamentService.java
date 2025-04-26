package com.gestion.medicaments.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestion.medicaments.exceptions.ResourceNotFoundException;
import com.gestion.medicaments.models.Medicament;
import com.gestion.medicaments.repositories.MedicamentRepository;

@Service
@Transactional
public class MedicamentService {
    @Autowired
    private MedicamentRepository medicamentRepository;

    public List<Medicament> getAllMedicaments() {
        return medicamentRepository.findAll();
    }

    public Optional<Medicament> getMedicamentById(Long id) {
        return medicamentRepository.findById(id);
    }

    public Medicament saveMedicament(Medicament medicament) {
        return medicamentRepository.save(medicament);
    }

    public void deleteMedicament(Long id) {
        medicamentRepository.deleteById(id);
    }
    
    public List<Medicament> searchMedicamentsByName(String name) {
        return medicamentRepository.findByNomContainingIgnoreCase(name);
    }
    
    public List<Medicament> getMedicamentsByCategory(String category) {
        return medicamentRepository.findByCategorie(category);
    }
    
    public List<Medicament> getExpiredMedicaments() {
        return medicamentRepository.findByDateExpirationBefore(LocalDate.now());
    }
    
    public List<Medicament> getLowStockMedicaments(int threshold) {
        return medicamentRepository.findByQuantiteLessThanEqual(threshold);
    }
} 