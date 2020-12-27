package com.elt.bank.modal;

import javax.persistence.*;
import java.util.Date;
import java.util.zip.DataFormatException;

@Entity(name = "elt_transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Account acc;

    private Float amount;

    private String desc;

    private Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAcc() {
        return acc;
    }

    public void setAcc(Account targetAcc) {
        this.acc = targetAcc;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }
}
