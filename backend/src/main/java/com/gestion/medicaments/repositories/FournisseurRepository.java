package com.gestion.medicaments.repositories;

import com.gestion.medicaments.models.Fournisseur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FournisseurRepository extends JpaRepository<Fournisseur, Long> {
    boolean existsByEmail(String email);
} 