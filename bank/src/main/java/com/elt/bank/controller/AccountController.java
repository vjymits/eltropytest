package com.elt.bank.controller;


import com.elt.bank.Setup.StatementGenerator;
import com.elt.bank.modal.Account;
import com.elt.bank.modal.Customer;
import com.elt.bank.modal.User;
import com.elt.bank.pojo.UserPojo;
import com.elt.bank.service.AccountService;
import com.elt.bank.service.CustomerService;
import com.elt.bank.service.TransactionService;
import com.elt.bank.util.Constants;
import com.elt.bank.util.Error;
import com.elt.bank.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping(Constants.API_BASE_URL)
public class AccountController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    private Set<String> accountTypes;

    private final String CUSTOMER_ID = "customerId";

    @PostConstruct
    public void init(){
        this.accountTypes = new HashSet<>();
        accountTypes.add("saving");
        accountTypes.add("current");
        accountTypes.add("loan");
        accountTypes.add("salary");
    }


    @PostMapping("/account")
    public ResponseEntity<Object> createAccount(@RequestAttribute("user") UserPojo currentuser,
                                        @RequestBody Map<String, Object> body){

        Object ocustId = body.get(CUSTOMER_ID);
        if (ocustId == null)
            return new ResponseEntity<>(errorResponse("customerId is mandatory."),
                    HttpStatus.BAD_REQUEST);
        long custId = Long.parseLong(body.get(CUSTOMER_ID).toString());

        String accType = body.get("accountType").toString();
        //Validate customer
        Optional<Customer> o = customerService.getCustomerById(custId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse("Cannot create account since no such customer exist."),
                HttpStatus.BAD_REQUEST);
        //Validate account type
        if(!accountTypes.contains(accType))
            return new ResponseEntity<>(errorResponse("no such account type supported."),
                    HttpStatus.BAD_REQUEST);
        Account a = accountService.createAccount(body);
        return new ResponseEntity<>(accountResponse(a), HttpStatus.OK);
    }

    private Map<String, String> errorResponse(String msg) {
        return ResponseUtil.errorResponse(msg);
    }

    @GetMapping("account/{accId}")
    public ResponseEntity<Object> getAccount(@RequestAttribute("user")UserPojo currentUser,
                                      @PathVariable("accId") Long accId){
        Optional<Account> o = accountService.getAccountByAccountId(accId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse(Error.NO_ACC),
                    HttpStatus.BAD_REQUEST);
        Account a = o.get();
        return new ResponseEntity<>(accountResponse(a), HttpStatus.OK);

    }

    @GetMapping("account/{accId}/stmt")
    public ResponseEntity<Object> getAccountStmt(@RequestAttribute("user")UserPojo currentUser,
                                             @PathVariable("accId") Long accId,
                                                 HttpServletResponse response){
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename="+accId+"_"+ System.currentTimeMillis() + "_stmt.pdf";
        response.setHeader(headerKey, headerValue);
        Optional<Account> o = accountService.getAccountByAccountId(accId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse(Error.NO_ACC),
                    HttpStatus.BAD_REQUEST);
        Account a = o.get();

        StatementGenerator stmtGen = new StatementGenerator(a, a.getTransactionSet());
        return new ResponseEntity<>(stmtGen.generate(response), HttpStatus.OK);
    }

    @DeleteMapping("account/{accId}")
    public ResponseEntity<Object> deleteAccount(@RequestAttribute("user")UserPojo currentUser,
                                         @PathVariable("accId") Long accId){
        Optional<Account> o = accountService.getAccountByAccountId(accId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse(Error.NO_ACC),
                    HttpStatus.BAD_REQUEST);
        Account a = o.get();
        accountService.deleteAccount(a);
        return new ResponseEntity<>(accountResponse(a), HttpStatus.OK);

    }


    private Map<String, Object> accountResponse(Account a) {
        return ResponseUtil.accountResponse(a);
    }
}
