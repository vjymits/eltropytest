package com.elt.bank.service;

import com.elt.bank.modal.Account;
import com.elt.bank.modal.Transaction;

import java.util.Map;
import java.util.Set;

public interface TransactionService {

    public Set<Transaction> getAllTransactionsByAccountId(long accId);
    public Map<String, Object> transfer(Account from, Account to, float amt);
    public Map<String, Object> withdraw(Account acc, float amt);
    public Map<String, Object> deposit(Account acc, float amt);

}
