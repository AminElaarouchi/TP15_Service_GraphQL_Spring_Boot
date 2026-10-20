package com.example.banque.repository;

import com.example.banque.model.Transaction;
import com.example.banque.model.TypeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByCompteIdOrderByIdAsc(Long compteId);
    @Query("select coalesce(sum(t.montant), 0) from Transaction t where t.type = :type")
    double sumByType(@Param("type") TypeTransaction type);
    @Query("select coalesce(sum(t.montant), 0) from Transaction t where t.compte.id = :compteId and t.type = :type")
    double sumByCompteAndType(@Param("compteId") Long compteId, @Param("type") TypeTransaction type);
}
