# TP15 — Service GraphQL avec Spring Boot

Projet Maven Java 17 basé sur le [TP15 du cours](https://mliaedu.toubkalit.com/student/courses/7/labs/168). Il expose les comptes, les transactions et leurs statistiques par GraphQL avec H2 et Spring Data JPA.

## Démarrer et tester

```bash
mvn clean test
mvn spring-boot:run
```

GraphiQL : `http://localhost:8082/graphiql` ; endpoint : `POST http://localhost:8082/graphql` ; console H2 : `/h2-console` (`jdbc:h2:mem:banque`, utilisateur `sa`, mot de passe vide).

```graphql
mutation { saveCompte(compte: {solde: 1500, dateCreation: "2024-11-18", type: COURANT}) { id solde soldeActuel } }
query { allComptes { id solde soldeActuel type } totalSolde { count sum average } }
mutation { addTransaction(transaction: {compteId: 1, montant: 500, type: DEPOT}) { id montant compte { id } } }
query { compteTransactions(id: 1) { id montant date type } transactionStats { count sumDepots sumRetraits } }
```

`solde` est le solde initial ; `soldeActuel` ajoute les dépôts et soustrait les retraits. `totalSolde` agrège les soldes actuels. Les retraits supérieurs au solde disponible sont refusés. Les dates utilisent `AAAA-MM-JJ` (les `/` sont aussi acceptés pour les exemples du TP). Les erreurs de compte introuvable ou de saisie invalide sont retournées en erreurs GraphQL explicites.
