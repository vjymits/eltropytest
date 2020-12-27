package com.elt.bank.controller;


import com.elt.bank.modal.Account;
import com.elt.bank.modal.Customer;
import com.elt.bank.modal.User;
import com.elt.bank.service.AccountService;
import com.elt.bank.service.CustomerService;
import com.elt.bank.util.Constants;
import com.elt.bank.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping(Constants.API_BASE_URL)
public class AccountController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;


    @PostMapping("/account")
    public ResponseEntity createAccount(@RequestAttribute("user") User currentuser,
                                        @RequestBody Map<String, Object> body){

        Object ocustId = body.get("customerId");
        if (ocustId == null)
            return new ResponseEntity(errorResponse("customerId is mandatory."),
                    HttpStatus.BAD_REQUEST);
        long custId = Long.parseLong(body.get("customerId").toString());
        Set<String> accountTypes = new HashSet<>();
        accountTypes.add("saving");
        accountTypes.add("current");
        accountTypes.add("loan");
        accountTypes.add("salary");

        String accType = body.get("accountType").toString();
        //Validate customer
        Optional<Customer> o = customerService.getCustomerById(custId);
        if(!o.isPresent())
            return new ResponseEntity(errorResponse("Cannot create account since no such customer exist."),
                HttpStatus.BAD_REQUEST);
        //Validate account type
        if(!accountTypes.contains(accType))
            return new ResponseEntity(errorResponse("no such account type supported."),
                    HttpStatus.BAD_REQUEST);
        Account a = accountService.createAccount(body);
        return new ResponseEntity(accountResponse(a), HttpStatus.OK);
    }

    private Map<String, String> errorResponse(String msg) {
        return ResponseUtil.errorResponse(msg);
    }

    @GetMapping("account/{accId}")
    public ResponseEntity getAccount(@RequestAttribute("user")User currentUser,
                                      @PathVariable("accId") Long accId){
        Optional<Account> o = accountService.getAccountByAccountId(accId);
        if(!o.isPresent())
            return new ResponseEntity(errorResponse("No Such Account exist."),
                    HttpStatus.BAD_REQUEST);
        Account a = o.get();
        return new ResponseEntity(accountResponse(a), HttpStatus.OK);

    }

    @DeleteMapping("account/{accId}")
    public ResponseEntity deleteAccount(@RequestAttribute("user")User currentUser,
                                         @PathVariable("accId") Long accId){
        Optional<Account> o = accountService.getAccountByAccountId(accId);
        if(!o.isPresent())
            return new ResponseEntity(errorResponse("No Such account exist."),
                    HttpStatus.BAD_REQUEST);
        Account a = o.get();
        accountService.deleteAccount(a);
        return new ResponseEntity(accountResponse(a), HttpStatus.OK);

    }


    private Map<String, Object> accountResponse(Account a) {
        Map<String, Object> m = new HashMap<>();
        m.put("accountType", a.getAccType());
        m.put("balance", a.getBalance());
        m.put("accountNo", a.getNo());
        m.put("customerId", a.getCustomer().getId());
        m.put("customerName", a.getCustomer().getName());
        m.put("link", Constants.API_BASE_URL+"/account/"+a.getNo());
        return m;
    }
}
