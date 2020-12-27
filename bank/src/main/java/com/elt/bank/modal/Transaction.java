package com.elt.bank.modal;

import javax.persistence.*;

@Entity(name = "elt_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Account sourceAcc;

    @ManyToOne
    private Account targetAcc;

    private long amount;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getSourceAcc() {
        return sourceAcc;
    }

    public void setSourceAcc(Account sourceAcc) {
        this.sourceAcc = sourceAcc;
    }

    public Account getTargetAcc() {
        return targetAcc;
    }

    public void setTargetAcc(Account targetAcc) {
        this.targetAcc = targetAcc;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }
}
