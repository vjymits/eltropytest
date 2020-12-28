package com.elt.bank.controller;

import com.elt.bank.modal.Account;
import com.elt.bank.modal.Customer;

import com.elt.bank.pojo.UserPojo;
import com.elt.bank.service.CustomerService;
import com.elt.bank.util.Constants;
import com.elt.bank.util.Error;
import com.elt.bank.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping(Constants.API_BASE_URL)
public class CustomerController {
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;

    private final String NO_CUST_FOUND = "No such customer exist.";


    @PostMapping("/customer")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Object> createCustomer(@RequestAttribute("user") UserPojo user,
                                         @RequestBody Map<String, String> body) {
        String name = body.get("name");
        // validate name
        if(name == null || name.length() == 0){
            return new ResponseEntity<>(errorResponse("Invalid customer name!"), HttpStatus.BAD_REQUEST);
        }
        // create customer
        Customer c  = customerService.createCustomer(body);
        return new ResponseEntity<>(customerResponse(c), HttpStatus.OK);

    }

    @GetMapping("customer/{custId}")
    public ResponseEntity<Object> getCustomer(@RequestAttribute("user")UserPojo currentUser,
                                      @PathVariable("custId") Long custId){
        Optional<Customer> o = customerService.getCustomerById(custId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse(NO_CUST_FOUND),
                    HttpStatus.BAD_REQUEST);
        Customer c = o.get();
        return new ResponseEntity<>(customerResponse(c), HttpStatus.OK);

    }

    @PutMapping("customer/{custId}")
    public ResponseEntity<Object> upadateCustomer(@RequestAttribute("user")UserPojo currentUser,
                                                @PathVariable("custId") Long custId,
                                                @RequestBody Map<String, String> body){
        Optional<Customer> o = customerService.getCustomerById(custId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse(NO_CUST_FOUND),
                    HttpStatus.BAD_REQUEST);
        Customer c = o.get();
        customerService.updateCustomer(c, body);
        return new ResponseEntity<>(customerResponse(c), HttpStatus.OK);

    }

    @PutMapping("customer/{custId}/kyc")
    public ResponseEntity<Object> updateKyc(@RequestAttribute("user")UserPojo currentUser,
                                                 @PathVariable("custId") Long custId,
                                                 @RequestBody Map<String, String> body){
        Optional<Customer> o = customerService.getCustomerById(custId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse(NO_CUST_FOUND),
                    HttpStatus.BAD_REQUEST);
        Customer c = o.get();
        String pan = body.get("pan");
        String aadhar = body.get("aadhar");
        boolean invalidPan = pan == null || pan.isEmpty();
        boolean invalidAdr = aadhar == null || aadhar.isEmpty();

        if(invalidPan) {
            log.warn("Invalid PAN supplied");
            return new ResponseEntity<>(errorResponse(Error.MANDATORY_PAN), HttpStatus.BAD_REQUEST);
        }

        if(invalidAdr){
            log.warn("Invalid Aadhar no supplied");
            return new ResponseEntity<>(errorResponse(Error.MANDATORY_ADR), HttpStatus.BAD_REQUEST);
        }
        customerService.updateKYC(c, pan, aadhar);
        return new ResponseEntity<>(customerResponse(c), HttpStatus.OK);

    }

    @DeleteMapping("customer/{custId}")
    public ResponseEntity<Object> deleteCustomer(@RequestAttribute("user") UserPojo currentUser,
                                      @PathVariable("custId") Long custId){

        Optional<Customer> o = customerService.getCustomerById(custId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse(NO_CUST_FOUND),
                    HttpStatus.BAD_REQUEST);
        Customer c = o.get();
        Set<Account> acc= c.getAccounts();
        if(acc != null && acc.isEmpty()){
            return new ResponseEntity<>(errorResponse("Customer have Accounts, Delete account first."),
                    HttpStatus.BAD_REQUEST);
        }
        c = customerService.deleteCustomer(c);
        return new ResponseEntity<>(customerResponse(c), HttpStatus.OK);

    }

    private Map<String, String> errorResponse(String msg) {
        return ResponseUtil.errorResponse(msg);
    }

    private Map<String, Object> customerResponse(Customer c){
        Map<String, Object> m = new TreeMap<>();
        m.put("Id", c.getId());
        m.put("email", c.getEmail());
        m.put("phone", c.getPhone());
        m.put("pan", c.getPan());
        m.put("aadhar", c.getAadhar());
        m.put("link", Constants.API_BASE_URL+"/customer/"+c.getId());
        Set<Account> accounts = c.getAccounts();
        if(null == accounts)
            accounts = new HashSet<>();
        Set<Map<String, Object>> accountSet = new HashSet<>();
        for(Account a: accounts) {
            accountSet.add(ResponseUtil.accountResponse(a));
        }
        m.put("accounts", accountSet);
        return m;
    }

}
