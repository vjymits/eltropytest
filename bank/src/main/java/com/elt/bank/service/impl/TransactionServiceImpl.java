package com.elt.bank.service.impl;

import com.elt.bank.modal.Account;
import com.elt.bank.modal.Transaction;
import com.elt.bank.repo.AccountRepo;
import com.elt.bank.repo.TransactionRepo;
import com.elt.bank.service.TransactionService;
import com.elt.bank.util.Error;
import com.elt.bank.util.ResponseUtil;
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

    /**
     * Transfer the amount from one account to another.
     * @param from
     * @param to
     * @param amt
     * @return
     */
    @Transactional
    public synchronized Map<String, Object> transfer(Account from, Account to, float amt){
        Transaction fromTrn = new Transaction();
        Transaction toTrn = new Transaction();
        long time = System.currentTimeMillis();
        //build source
        fromTrn.setAmount(amt*-1);
        fromTrn.setAcc(from);
        fromTrn.setTimestamp(time);
        fromTrn.setDate(new Date(time));
        fromTrn.setDesc("Amount: "+amt+ " transferred to account: "+to.getNo());
        //build target
        toTrn.setAmount(amt);
        toTrn.setAcc(to);
        toTrn.setDate(new Date(time));
        toTrn.setTimestamp(time);
        toTrn.setDesc("Amount: "+amt+" received from account: "+from.getNo());
        //refresh
        Optional<Account> o = accountRepo.findById(from.getNo());
        if(!o.isPresent())
            return errorReport(Error.NO_ACC+": "+from.getNo());
        from = o.get();

        o = accountRepo.findById(to.getNo());
        if(!o.isPresent())
            return errorReport(Error.NO_ACC+": "+to.getNo());
        to = o.get();

        // check balance
        if(from.getBalance()-amt < 0)
            return errorReport(Error.NOT_ENOUGH_BAL);
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
     * Withdraw the amount from the account.
     * @param a
     * @param amt
     * @return
     */
    @Transactional
    public synchronized Map<String, Object> withdraw(Account a, float amt){
        Transaction t = new Transaction();
        // Build transaction
        t.setTimestamp(System.currentTimeMillis());
        t.setDate(new Date(t.getTimestamp()));
        t.setAcc(a);
        t.setAmount(amt*-1);
        t.setDesc("Withdraw amount: "+ amt);
        // validate & refresh the account
        Optional<Account> o = accountRepo.findById(a.getNo());
        if(!o.isPresent())
            return errorReport(Error.NO_ACC);
        a = o.get();
        if(a.getBalance()-amt < 0)
            return errorReport(Error.NOT_ENOUGH_BAL);
        a.setBalance(a.getBalance()-amt);

        transactionRepo.save(t);
        accountRepo.save(a);
        return transactionReport(a, t);
    }

    /**
     * Deposit amount into account.
     * @param a
     * @param amt
     * @return
     */
    @Transactional
    public synchronized Map<String, Object> deposit(Account a, float amt){
        Transaction t = new Transaction();
        // Build transaction
        t.setTimestamp(System.currentTimeMillis());
        t.setDate(new Date(t.getTimestamp()));
        t.setAcc(a);
        t.setAmount(amt);
        t.setDesc("Deposit amount: "+ amt);
        // validate & refresh the account
        Optional<Account> o = accountRepo.findById(a.getNo());
        if(!o.isPresent())
            return errorReport(Error.NO_ACC);
        a = o.get();
        a.setBalance(a.getBalance()+amt);
        transactionRepo.save(t);
        accountRepo.save(a);
        return transactionReport(a, t);

    }

    /**
     * Create transaction report for transfer.
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

    /**
     * Build map report for withdraw/deposit.
     * @param a
     * @param trn
     * @return
     */
    private Map<String, Object> transactionReport(Account a, Transaction trn) {
        Map<String, Object> m = new HashMap<>();
        m.put("accountBalanceNow", a.getBalance());
        float amt = trn.getAmount();
        m.put("amount", amt);
        m.put("TrnId", trn.getId());
        m.put("timestamp", trn.getDate());
        m.put("description", trn.getDesc());
        return m;

    }

    /**
     * Reprt the transaction error.
     * @param msg
     * @return
     */
    private Map<String, Object> errorReport(String msg) {
        Map<String, Object> m = new TreeMap<>();
        Map<String, String> e = ResponseUtil.errorResponse(msg);
        m.putAll(e);
        return m;
    }
}
