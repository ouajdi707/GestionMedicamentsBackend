package com.gestion.medicaments.models;

import jakarta.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "commandes")
public class Commande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @ManyToOne
    @JoinColumn(name = "fournisseur_id", nullable = false)
    private Fournisseur fournisseur;

    @ManyToOne
    @JoinColumn(name = "etat_id", nullable = false)
    private EtatCommande etat;

    @OneToMany(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<LigneCommande> lignesCommande = new HashSet<>();

    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL, orphanRemoval = true)
    private Livraison livraison;

    public Commande() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseur) {
        this.fournisseur = fournisseur;
    }

    public EtatCommande getEtat() {
        return etat;
    }

    public void setEtat(EtatCommande etat) {
        this.etat = etat;
    }

    public Set<LigneCommande> getLignesCommande() {
        return lignesCommande;
    }

    public void setLignesCommande(Set<LigneCommande> lignesCommande) {
        this.lignesCommande = lignesCommande;
    }

    public void addLigneCommande(LigneCommande ligneCommande) {
        lignesCommande.add(ligneCommande);
        ligneCommande.setCommande(this);
    }

    public void removeLigneCommande(LigneCommande ligneCommande) {
        lignesCommande.remove(ligneCommande);
        ligneCommande.setCommande(null);
    }

    public Livraison getLivraison() {
        return livraison;
    }

    public void setLivraison(Livraison livraison) {
        this.livraison = livraison;
        if (livraison != null) {
            livraison.setCommande(this);
        }
    }

    public Double getTotal() {
        return lignesCommande.stream()
                .mapToDouble(LigneCommande::getTotal)
                .sum();
    }
} 