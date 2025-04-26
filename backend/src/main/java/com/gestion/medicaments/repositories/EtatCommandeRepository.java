package com.gestion.medicaments.repositories;

import com.gestion.medicaments.models.EtatCommande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EtatCommandeRepository extends JpaRepository<EtatCommande, Long> {
    boolean existsByNom(String nom);
} 