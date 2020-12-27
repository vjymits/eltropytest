package com.elt.bank.repo;

import com.elt.bank.modal.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TransactionRepo extends CrudRepository<Transaction, Long> {

    public Set<Transaction> findByAcc(long accId);

}
