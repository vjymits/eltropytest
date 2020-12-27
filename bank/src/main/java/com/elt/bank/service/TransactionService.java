package com.elt.bank.service;

import com.elt.bank.modal.Account;
import com.elt.bank.modal.Transaction;

import java.util.Map;
import java.util.Set;

public interface TransactionService {

    public Set<Transaction> getAllTransactionsByAccountId(long accId);
    public Map<String, Object> createTransaction(Account from, Account to, float amt);

}
