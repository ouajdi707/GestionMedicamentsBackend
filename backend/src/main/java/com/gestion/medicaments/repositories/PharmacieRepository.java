package com.gestion.medicaments.repositories;

import com.gestion.medicaments.models.Pharmacie;
import com.gestion.medicaments.models.Disponibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacieRepository extends JpaRepository<Pharmacie, Long> {
    List<Pharmacie> findByVille(String ville);
    List<Pharmacie> findByDisponibilite(Disponibilite disponibilite);
    List<Pharmacie> findByVilleAndDisponibilite(String ville, Disponibilite disponibilite);
    boolean existsByNomAndVille(String nom, String ville);
} 