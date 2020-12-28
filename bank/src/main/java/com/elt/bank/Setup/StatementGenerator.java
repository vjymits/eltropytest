package com.elt.bank.Setup;

import com.elt.bank.modal.Account;
import com.elt.bank.modal.Customer;
import com.elt.bank.modal.Transaction;


import com.elt.bank.service.AccountService;
import com.elt.bank.service.CustomerService;
import com.elt.bank.service.TransactionService;
import com.elt.bank.util.ResponseUtil;
import com.lowagie.text.Font;
import com.lowagie.text.alignment.HorizontalAlignment;
import com.lowagie.text.alignment.VerticalAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class StatementGenerator {

    private static final Logger log = LoggerFactory.getLogger(StatementGenerator.class);



    private Account account;
    private Customer customer;
    private Set<Transaction> trns;


    public StatementGenerator() {

    }

    public StatementGenerator(Account acc, Set<Transaction> transactions){
        this.setAccount(acc);
        this.setCustomer(this.getAccount().getCustomer());
        this.setTrns(transactions);
    }

    /**
     * create pdf
     * @return
     */
    public Map<String, String> generate(HttpServletResponse response) {

        try(Document document = new Document(PageSize.A4)) {


            if(getAccount() == null || getCustomer() == null) {
                return ResponseUtil.errorResponse("Not able to " +
                        "fetch Account or Customer");
            }
            // PDF doc

            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            font.setSize(18);
            font.setColor(Color.BLUE);
            // Heading
            Paragraph p = new Paragraph("Account statement", font);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);
            // Account info
            Table accTable = new Table(5);
            accTable.setWidth(100);
            accTable.setWidths(new float[] {1.5f, 3.5f, 3.0f, 3.0f, 1.5f});
            accTable.setSpacing(10);

            // Account table information
            writeAccountTableInfo(accTable);
            document.add(accTable);

            if(trns.isEmpty()){
                log.info("No transaction found " +
                        "for account: {}.", getAccount().getNo());
                return ResponseUtil.successResponse("Statement successfully created ");
            }
            p = new Paragraph("Transactions", font);
            p.setAlignment(Element.ALIGN_CENTER);
            document.add(p);

            Table trnTable = new Table(4);
            trnTable.setWidth(100);
            trnTable.setSpacing(10);
            writeTransactions(trnTable);
            document.add(trnTable);
            return ResponseUtil.successResponse("Statement successfully created ");

        }
        catch (Exception e){
            log.error("An error while creating stmt", e);
            return ResponseUtil.errorResponse("Something went wrong while " +
                    "generating statement for account: "+this.getAccount().getNo());
        }


    }

    /**
     * Write all the transactions
     * @param table
     */
    private void writeTransactions(Table table) {
        //Setting table header

        // Setting up columns
        table.addCell(new Phrase("Id"));
        table.addCell(new Phrase("Description"));
        table.addCell(new Phrase("Amount"));
        table.addCell(new Phrase("Date"));
        // Add rows
        for(Transaction trn : trns) {
            table.addCell(trn.getId()+"");
            table.addCell(trn.getDesc());
            table.addCell(trn.getAmount()+"");
            table.addCell(trn.getDate()+"");
        }
    }

    /**
     * Write account information such as customer name, account-no etc
     * @param table
     */
    private void writeAccountTableInfo(Table table) {


        // Setting up columns
        table.addCell(new Phrase("Customer Name"));
        table.addCell(new Phrase("Account No."));
        table.addCell(new Phrase("Type"));
        table.addCell(new Phrase("Balance"));
        table.addCell(new Phrase("Transactions"));
        //Setting up rows
        table.addCell(customer.getName());
        table.addCell(account.getNo()+"");
        table.addCell(account.getAccType());
        table.addCell(account.getBalance()+"");
        table.addCell(""+trns.size());

    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<Transaction> getTrns() {
        return trns;
    }

    public void setTrns(Set<Transaction> trns) {
        this.trns = trns;
    }
}
