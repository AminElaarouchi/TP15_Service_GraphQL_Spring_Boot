package com.example.banque.graphql;

import com.example.banque.model.TypeTransaction;
public record TransactionRequest(Long compteId, double montant, String date, TypeTransaction type) {}
