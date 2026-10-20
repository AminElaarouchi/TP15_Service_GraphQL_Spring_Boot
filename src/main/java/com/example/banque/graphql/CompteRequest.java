package com.example.banque.graphql;

import com.example.banque.model.TypeCompte;
public record CompteRequest(double solde, String dateCreation, TypeCompte type) {}
