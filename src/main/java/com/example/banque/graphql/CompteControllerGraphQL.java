package com.example.banque.graphql;

import com.example.banque.model.Compte;
import com.example.banque.model.Transaction;
import com.example.banque.model.TypeTransaction;
import com.example.banque.repository.CompteRepository;
import com.example.banque.repository.TransactionRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Controller
public class CompteControllerGraphQL {
    private final CompteRepository comptes;
    private final TransactionRepository transactions;
    public CompteControllerGraphQL(CompteRepository comptes, TransactionRepository transactions) {
        this.comptes = comptes; this.transactions = transactions;
    }

    @QueryMapping public List<Compte> allComptes() { return comptes.findAll(); }
    @QueryMapping public Compte compteById(@Argument Long id) { return findCompte(id); }

    @MutationMapping public Compte saveCompte(@Argument CompteRequest compte) {
        if (compte.solde() < 0 || !Double.isFinite(compte.solde())) throw new IllegalArgumentException("Solde invalide");
        if (compte.type() == null) throw new IllegalArgumentException("Type de compte requis");
        return comptes.save(new Compte(compte.solde(), parseDate(compte.dateCreation()), compte.type()));
    }

    @QueryMapping public SoldeStats totalSolde() {
        List<Compte> all = comptes.findAll();
        double sum = all.stream().mapToDouble(this::balance).sum();
        return new SoldeStats(all.size(), sum, all.isEmpty() ? 0 : sum / all.size());
    }

    @QueryMapping public List<Transaction> compteTransactions(@Argument Long id) {
        findCompte(id);
        return transactions.findByCompteIdOrderByIdAsc(id);
    }
    @QueryMapping public List<Transaction> allTransactions() { return transactions.findAll(); }
    @QueryMapping public TransactionStats transactionStats() {
        return new TransactionStats((int) transactions.count(), transactions.sumByType(TypeTransaction.DEPOT), transactions.sumByType(TypeTransaction.RETRAIT));
    }

    @Transactional
    @MutationMapping public Transaction addTransaction(@Argument TransactionRequest transaction) {
        Compte compte = findCompte(transaction.compteId());
        if (transaction.montant() <= 0 || !Double.isFinite(transaction.montant())) throw new IllegalArgumentException("Montant invalide");
        if (transaction.type() == null) throw new IllegalArgumentException("Type de transaction requis");
        if (transaction.type() == TypeTransaction.RETRAIT && balance(compte) < transaction.montant()) throw new IllegalArgumentException("Solde insuffisant");
        return transactions.save(new Transaction(transaction.montant(), parseDate(transaction.date()), transaction.type(), compte));
    }

    @SchemaMapping(typeName = "Compte", field = "soldeActuel") public double soldeActuel(Compte compte) { return balance(compte); }
    @SchemaMapping(typeName = "Compte", field = "transactions") public List<Transaction> transactions(Compte compte) {
        return transactions.findByCompteIdOrderByIdAsc(compte.getId());
    }

    private Compte findCompte(Long id) { return comptes.findById(id).orElseThrow(() -> new CompteIntrouvableException(id)); }
    private double balance(Compte compte) {
        return compte.getSolde() + transactions.sumByCompteAndType(compte.getId(), TypeTransaction.DEPOT)
            - transactions.sumByCompteAndType(compte.getId(), TypeTransaction.RETRAIT);
    }
    private LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return LocalDate.now();
        try { return LocalDate.parse(value.replace('/', '-')); }
        catch (DateTimeParseException e) { throw new IllegalArgumentException("Date attendue au format AAAA-MM-JJ"); }
    }
}
