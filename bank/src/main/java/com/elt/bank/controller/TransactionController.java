package com.elt.bank.controller;

import com.elt.bank.modal.Account;
import com.elt.bank.pojo.UserPojo;
import com.elt.bank.service.AccountService;
import com.elt.bank.service.TransactionService;
import com.elt.bank.util.Constants;
import com.elt.bank.util.ResponseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(Constants.API_BASE_URL)
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountService accountService;

    @PostMapping("/transaction")
    public ResponseEntity<Object> createTransaction(@RequestAttribute("user") UserPojo user,
                                                    @RequestBody Map<String, String> body) {

        long sourceAccountId = Long.parseLong(body.get("sourceAccountId"));
        long targetAccountId = Long.parseLong(body.get("targetAccountId"));
        float amount = Float.parseFloat(body.get("amount"));
        // Validate
        Optional<Account> o = accountService.getAccountByAccountId(sourceAccountId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse("Source account does not exist.")
                    , HttpStatus.BAD_REQUEST);
        Account source = o.get();
        o = accountService.getAccountByAccountId(targetAccountId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse("Target account does not exist.")
                    , HttpStatus.BAD_REQUEST);

        Account target = o.get();
        if(source.getBalance()-amount < 0){
            return new ResponseEntity<>(errorResponse("Insufficient balance!")
                    , HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> m = transactionService.createTransaction(source, target, amount);
        return new ResponseEntity<>(m, HttpStatus.OK);
    }

    private Map<String, String> errorResponse(String msg) {
        return ResponseUtil.errorResponse(msg);
    }
}
