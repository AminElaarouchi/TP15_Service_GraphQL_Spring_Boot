package com.example.banque.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Compte {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double solde;
    private LocalDate dateCreation;
    @Enumerated(EnumType.STRING)
    private TypeCompte type;

    protected Compte() {}
    public Compte(double solde, LocalDate dateCreation, TypeCompte type) {
        this.solde = solde; this.dateCreation = dateCreation; this.type = type;
    }
    public Long getId() { return id; }
    public double getSolde() { return solde; }
    public LocalDate getDateCreation() { return dateCreation; }
    public TypeCompte getType() { return type; }
}
