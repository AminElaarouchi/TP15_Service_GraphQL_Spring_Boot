package com.example.banque.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "compte_transaction")
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double montant;
    private LocalDate date;
    @Enumerated(EnumType.STRING)
    private TypeTransaction type;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Compte compte;

    protected Transaction() {}
    public Transaction(double montant, LocalDate date, TypeTransaction type, Compte compte) {
        this.montant = montant; this.date = date; this.type = type; this.compte = compte;
    }
    public Long getId() { return id; }
    public double getMontant() { return montant; }
    public LocalDate getDate() { return date; }
    public TypeTransaction getType() { return type; }
    public Compte getCompte() { return compte; }
}
