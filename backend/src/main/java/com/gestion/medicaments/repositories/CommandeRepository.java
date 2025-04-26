package com.gestion.medicaments.repositories;

import com.gestion.medicaments.models.Commande;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {
    List<Commande> findByFournisseurId(Long fournisseurId);
    List<Commande> findByEtatId(Long etatId);
} 