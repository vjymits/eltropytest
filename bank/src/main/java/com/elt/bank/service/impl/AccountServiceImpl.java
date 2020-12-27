package com.elt.bank.service.impl;

import com.elt.bank.modal.Account;
import com.elt.bank.modal.Customer;
import com.elt.bank.repo.AccountRepo;
import com.elt.bank.repo.CustomerRepo;
import com.elt.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private CustomerRepo customerRepo;

    public Account createAccount(Map<String, Object> m){
        Account a = new Account();
        long cid = Long.parseLong(m.get("customerId").toString());
        a.setAccType(m.get("accountType").toString());
        a.setBalance(Long.parseLong(m.get("balance").toString()));
        Optional<Customer> o = customerRepo.findById(cid);
        // assume customer is there
        a.setCustomer(o.get());
        return accountRepo.save(a);
    }
    public Account upadteAccount(Account a){
        return accountRepo.save(a);
    }
    public Set<Account> getAccountsByCustomerId(long custId){
        return accountRepo.findAllByCustomerId(custId);

    }
    public Optional<Account> getAccountByAccountId(long id){
        return accountRepo.findById(id);
    }

    public Account deleteAccount(Account a){
        accountRepo.delete(a);
        return a;
    }
}
