package com.elt.bank.modal;

import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity(name="elt_account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long no;
    private float balance;
    private String accType;


    @ManyToOne
    @JoinColumn(name="customer_id", nullable=false)
    private Customer customer;


    @OneToMany(mappedBy = "acc")
    private Set<Transaction> transactionSet;

    public Set<Transaction> getTransactionSet() {
        return transactionSet;
    }

    public void setTransactionSet(Set<Transaction> transactionSet) {
        this.transactionSet = transactionSet;
    }

    public Long getNo() {
        return no;
    }

    public void setNo(Long n) {
        this.no = n;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
