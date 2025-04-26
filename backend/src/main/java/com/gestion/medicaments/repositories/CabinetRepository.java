package com.gestion.medicaments.repositories;

import com.gestion.medicaments.models.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CabinetRepository extends JpaRepository<Cabinet, Long> {
    List<Cabinet> findByVille(String ville);
    List<Cabinet> findBySpecialite(String specialite);
    List<Cabinet> findByMedecinId(Long medecinId);
} 