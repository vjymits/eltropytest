package com.elt.bank.repo;

import com.elt.bank.modal.Transaction;
import org.springframework.data.repository.CrudRepository;

public interface TransactionRepo extends CrudRepository<Transaction, Long> {

}
