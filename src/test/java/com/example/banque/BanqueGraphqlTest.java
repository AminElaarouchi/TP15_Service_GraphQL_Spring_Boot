package com.example.banque;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BanqueGraphqlTest {
    @Autowired TestRestTemplate http;
    @Autowired ObjectMapper mapper;
    @LocalServerPort int port;

    private JsonNode execute(String query) throws Exception {
        String response = http.postForObject("http://localhost:" + port + "/graphql", Map.of("query", query), String.class);
        return mapper.readTree(response);
    }

    @Test void createsAccountAndTransactionsAndComputesStats() throws Exception {
        JsonNode created = execute("mutation { saveCompte(compte: {solde: 1000, type: COURANT}) { id soldeActuel } }");
        assertThat(created.path("errors").isMissingNode()).isTrue();
        long id = created.path("data").path("saveCompte").path("id").asLong();
        JsonNode deposit = execute("mutation { addTransaction(transaction: {compteId: " + id + ", montant: 250, type: DEPOT}) { id montant } }");
        assertThat(deposit.path("errors").isMissingNode()).isTrue();
        JsonNode account = execute("{ compteById(id: " + id + ") { solde soldeActuel transactions { montant type } } }");
        assertThat(account.path("data").path("compteById").path("soldeActuel").asDouble()).isEqualTo(1250.0);
        assertThat(account.path("data").path("compteById").path("transactions").size()).isEqualTo(1);
        JsonNode stats = execute("{ totalSolde { count sum average } transactionStats { count sumDepots sumRetraits } }");
        assertThat(stats.path("data").path("transactionStats").path("sumDepots").asDouble()).isGreaterThanOrEqualTo(250.0);
        JsonNode error = execute("{ compteById(id: 99999) { id } }");
        assertThat(error.path("errors").get(0).path("message").asText()).contains("introuvable");
    }
}
