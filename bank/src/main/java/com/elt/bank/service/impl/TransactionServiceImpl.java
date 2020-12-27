package com.elt.bank.service.impl;

import com.elt.bank.modal.Account;
import com.elt.bank.modal.Transaction;
import com.elt.bank.repo.AccountRepo;
import com.elt.bank.repo.TransactionRepo;
import com.elt.bank.service.TransactionService;
import org.hibernate.annotations.Synchronize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.MessageFormat;
import java.util.*;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepo transactionRepo;

    @Autowired
    private AccountRepo accountRepo;

    public Set<Transaction> getAllTransactionsByAccountId(long accId){
        return transactionRepo.findByAcc(accId);
    }

    @Transactional
    public synchronized Map<String, Object> createTransaction(Account from, Account to, float amt){
        Transaction fromTrn = new Transaction();
        Transaction toTrn = new Transaction();
        long time = System.currentTimeMillis();
        //build source
        fromTrn.setAmount(amt*-1);
        fromTrn.setAcc(from);
        fromTrn.setDate(new Date(time));
        fromTrn.setDesc(MessageFormat.format("{} transferred to account {} ", amt, to.getNo()));
        //build target
        toTrn.setAmount(amt);
        toTrn.setAcc(to);
        toTrn.setDate(new Date(time));
        toTrn.setDesc(MessageFormat.format("{} received from account {}", amt, from.getNo()));
        //refresh
        Optional<Account> o = accountRepo.findById(from.getNo());
        if(!o.isPresent())
            return null;
        from = o.get();

        o = accountRepo.findById(to.getNo());
        if(!o.isPresent())
            return null;
        to = o.get();

        // check balance
        if(from.getBalance()-amt < 0)
            return null;
        from.setBalance(from.getBalance()-amt);
        to.setBalance(to.getBalance()+amt);

        List<Transaction> trns = new ArrayList<>();
        trns.add(fromTrn);
        trns.add(toTrn);

        List<Account> accs = new ArrayList<>();
        accs.add(from);
        accs.add(to);

        accountRepo.saveAll(accs);
        transactionRepo.saveAll(trns);
        return transactionReport(from, to, fromTrn, toTrn);
    }

    /**
     * Create transaction report
     * @param source
     * @param target
     * @param strn
     * @param ttrn
     * @return
     */
    private Map<String, Object> transactionReport(Account source, Account target,
                                                  Transaction strn, Transaction ttrn) {
        Map<String, Object> m = new HashMap<>();
        m.put("sourceAccountBalanceNow", source.getBalance());
        m.put("targetAccountBalanceNow", target.getBalance());
        m.put("sourceTrnId", strn.getId());
        m.put("targetTrnId", ttrn.getId());
        m.put("amount", ttrn.getAmount());
        m.put("timestamp", ttrn.getDate());
        return m;
    }
}
