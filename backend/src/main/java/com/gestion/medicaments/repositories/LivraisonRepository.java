package com.gestion.medicaments.repositories;

import com.gestion.medicaments.models.Livraison;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivraisonRepository extends JpaRepository<Livraison, Long> {
    Optional<Livraison> findByCommandeId(Long commandeId);
    void deleteByCommandeId(Long commandeId);
} 