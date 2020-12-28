package com.elt.bank.controller;

import com.elt.bank.modal.Account;
import com.elt.bank.pojo.UserPojo;
import com.elt.bank.service.AccountService;
import com.elt.bank.service.TransactionService;
import com.elt.bank.util.Constants;
import com.elt.bank.util.Error;
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

    private static final String AMT = "amount";

    /**
     * Transfer the amount from one account to another.
     * @param user
     * @param body
     * @return
     */
    @PostMapping("/transaction/transfer")
    public ResponseEntity<Object> trasferTransaction(@RequestAttribute("user") UserPojo user,
                                                    @RequestBody Map<String, String> body) {

        long sourceAccountId = Long.parseLong(body.get("sourceAccountId"));
        long targetAccountId = Long.parseLong(body.get("targetAccountId"));
        float amount = Float.parseFloat(body.get(AMT));
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
            return new ResponseEntity<>(errorResponse(Error.NOT_ENOUGH_BAL)
                    , HttpStatus.BAD_REQUEST);
        }
        if(sourceAccountId == targetAccountId) {
            return new ResponseEntity<>(errorResponse("Invalid accounts.")
                    , HttpStatus.BAD_REQUEST);
        }

        Map<String, Object> m = transactionService.transfer(source, target, amount);
        return new ResponseEntity<>(m, HttpStatus.OK);
    }

    /**
     * Withdraw the amount.
     * @param currentUser
     * @param body
     * @return
     */
    @PostMapping("/transaction/withdraw")
    public ResponseEntity<Object> witdrawTransaction(@RequestAttribute("user") UserPojo currentUser,
                                                     @RequestBody Map<String, String> body){

        long accountId = Long.parseLong(body.get("accountId"));
        float amount = Float.parseFloat(body.get(AMT));
        Optional<Account> o = accountService.getAccountByAccountId(accountId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse(Error.NO_ACC)
                    , HttpStatus.BAD_REQUEST);
        Account a = o.get();
        if(a.getBalance()-amount < 0){
            return new ResponseEntity<>(errorResponse(Error.NOT_ENOUGH_BAL)
                    , HttpStatus.BAD_REQUEST);
        }
        Map<String, Object> m = transactionService.withdraw(a, amount);
        return new ResponseEntity<>(m, HttpStatus.OK);
    }


    /**
     * Deposit into account.
     * @param currentUser
     * @param body
     * @return
     */
    @PostMapping("/transaction/deposit")
    public ResponseEntity<Object> depositTransaction(@RequestAttribute("user") UserPojo currentUser,
                                                     @RequestBody Map<String, String> body){
        long accountId = Long.parseLong(body.get("accountId"));
        float amount = Float.parseFloat(body.get(AMT));
        Optional<Account> o = accountService.getAccountByAccountId(accountId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse(Error.NO_ACC)
                    , HttpStatus.BAD_REQUEST);
        Account a = o.get();
        Map<String, Object> m = transactionService.deposit(a, amount);
        return new ResponseEntity<>(m, HttpStatus.OK);


    }


    private Map<String, String> errorResponse(String msg) {
        return ResponseUtil.errorResponse(msg);
    }
}
