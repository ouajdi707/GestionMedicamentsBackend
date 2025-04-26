package com.gestion.medicaments.controllers;

import com.gestion.medicaments.models.Cabinet;
import com.gestion.medicaments.models.User;
import com.gestion.medicaments.services.CabinetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cabinets")
@CrossOrigin(origins = "*")
public class CabinetController {

    @Autowired
    private CabinetService cabinetService;

    @GetMapping
    public List<Cabinet> getAllCabinets() {
        return cabinetService.getAllCabinets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cabinet> getCabinetById(@PathVariable Long id) {
        return cabinetService.getCabinetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Cabinet createCabinet(@RequestBody Cabinet cabinet) {
        return cabinetService.createCabinet(cabinet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cabinet> updateCabinet(@PathVariable Long id, @RequestBody Cabinet cabinetDetails) {
        try {
            Cabinet updatedCabinet = cabinetService.updateCabinet(id, cabinetDetails);
            return ResponseEntity.ok(updatedCabinet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCabinet(@PathVariable Long id) {
        try {
            cabinetService.deleteCabinet(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ville/{ville}")
    public List<Cabinet> getCabinetsByVille(@PathVariable String ville) {
        return cabinetService.getCabinetsByVille(ville);
    }

    @GetMapping("/specialite/{specialite}")
    public List<Cabinet> getCabinetsBySpecialite(@PathVariable String specialite) {
        return cabinetService.getCabinetsBySpecialite(specialite);
    }

    @GetMapping("/medecin/{medecinId}")
    public List<Cabinet> getCabinetsByMedecinId(@PathVariable Long medecinId) {
        return cabinetService.getCabinetsByMedecinId(medecinId);
    }

    @GetMapping("/medecins")
    public List<User> getAllMedecins() {
        return cabinetService.getAllMedecins();
    }
} 