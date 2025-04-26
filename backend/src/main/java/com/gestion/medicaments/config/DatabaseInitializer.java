package com.gestion.medicaments.config;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.gestion.medicaments.models.ERole;
import com.gestion.medicaments.models.Role;
import com.gestion.medicaments.models.User;
import com.gestion.medicaments.models.EtatCommande;
import com.gestion.medicaments.repositories.RoleRepository;
import com.gestion.medicaments.repositories.UserRepository;
import com.gestion.medicaments.repositories.EtatCommandeRepository;

@Component
public class DatabaseInitializer implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EtatCommandeRepository etatCommandeRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize roles if they don't exist
        initRoles();
        
        // Create default admin user if not exists
        createDefaultAdmin();

        // Initialize etats commande if they don't exist
        initEtatsCommande();
    }
    
    private void initRoles() {
        if (roleRepository.count() == 0) {

            
            Role adminRole = new Role();
            adminRole.setName(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            
            Role pharmacienRole = new Role();
            pharmacienRole.setName(ERole.ROLE_PHARMACIEN);
            roleRepository.save(pharmacienRole);
            
            Role patientRole = new Role();
            patientRole.setName(ERole.ROLE_PATIENT);
            roleRepository.save(patientRole);
            
            Role medecinRole = new Role();
            medecinRole.setName(ERole.ROLE_MEDECIN);
            roleRepository.save(medecinRole);
            
            System.out.println("Roles initialized");
        }
    }
    
    private void createDefaultAdmin() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActive(true);
            
            Set<Role> roles = new HashSet<>();
            roleRepository.findByName(ERole.ROLE_ADMIN).ifPresent(roles::add);
            admin.setRoles(roles);
            
            userRepository.save(admin);
            System.out.println("Default admin user created");
        }
    }

    private void initEtatsCommande() {
        if (etatCommandeRepository.count() == 0) {
            String[] etats = {"En attente", "En cours", "Livrée", "Annulée"};
            
            for (String etatNom : etats) {
                EtatCommande etat = new EtatCommande(etatNom);
                etatCommandeRepository.save(etat);
            }
            
            System.out.println("Etats commande initialized");
        }
    }
} 