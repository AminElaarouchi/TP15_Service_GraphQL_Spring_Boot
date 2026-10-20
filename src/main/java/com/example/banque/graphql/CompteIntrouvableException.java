package com.example.banque.graphql;
public class CompteIntrouvableException extends RuntimeException {
    public CompteIntrouvableException(Long id) { super("Compte " + id + " introuvable"); }
}
