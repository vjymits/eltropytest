package com.elt.bank.controller;

import com.elt.bank.modal.Account;
import com.elt.bank.modal.Customer;
import com.elt.bank.modal.User;
import com.elt.bank.service.CustomerService;
import com.elt.bank.util.Constants;
import com.elt.bank.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

@RestController
@RequestMapping(Constants.API_BASE_URL)
public class CustomerController {
    private static final Logger log = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private CustomerService customerService;


    @PostMapping("/customer")
    public ResponseEntity createCustomer(@RequestAttribute("user") User user,
                                         @RequestBody Map<String, String> body) {
        String name = body.get("name");
        // validate name
        if(name == null || name.length() == 0){
            return new ResponseEntity(errorResponse("Invalid customer name!"), HttpStatus.BAD_REQUEST);
        }
        // create customer
        Customer c  = customerService.createCustomer(body);
        return new ResponseEntity(customerResponse(c), HttpStatus.OK);

    }

    @GetMapping("customer/{custId}")
    public ResponseEntity getCustomer(@RequestAttribute("user")User currentUser,
                                      @PathVariable("custId") Long custId){
        Optional<Customer> o = customerService.getCustomerById(custId);
        if(!o.isPresent())
            return new ResponseEntity(errorResponse("No Such customer exist."),
                    HttpStatus.BAD_REQUEST);
        Customer c = o.get();
        return new ResponseEntity(customerResponse(c), HttpStatus.OK);

    }

    @DeleteMapping("customer/{custId}")
    public ResponseEntity deleteCustomer(@RequestAttribute("user")User currentUser,
                                      @PathVariable("custId") Long custId){
        log.info("delete customer, id: "+custId );
        Optional<Customer> o = customerService.getCustomerById(custId);
        if(!o.isPresent())
            return new ResponseEntity(errorResponse("No Such customer exist."),
                    HttpStatus.BAD_REQUEST);
        Customer c = o.get();
        Set<Account> acc= c.getAccounts();
        if(acc != null && acc.size() == 0){
            return new ResponseEntity(errorResponse("Customer have Accounts, Delete account first."),
                    HttpStatus.BAD_REQUEST);
        }
        c = customerService.deleteCustomer(c);
        return new ResponseEntity(customerResponse(c), HttpStatus.OK);

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
        return m;
    }

}
