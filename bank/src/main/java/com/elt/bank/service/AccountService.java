package com.elt.bank.service;

import com.elt.bank.modal.Account;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface AccountService {

    public Account createAccount(Map<String, Object> m);
    public Account upadteAccount(Account a);
    public Set<Account> getAccountsByCustomerId(long custId);
    public Optional<Account> getAccountByAccountId(long id);
    public Account deleteAccount(Account a);
}
