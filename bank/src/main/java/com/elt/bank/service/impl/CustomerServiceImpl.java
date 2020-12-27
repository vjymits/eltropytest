package com.elt.bank.service.impl;

import com.elt.bank.modal.Customer;
import com.elt.bank.repo.CustomerRepo;
import com.elt.bank.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    private CustomerRepo customerRepo;

    public Customer createCustomer(Map<String, String> m){
        Customer c = new Customer();
        fill(c, m);
        c = customerRepo.save(c);
        return c;
    }

    public Customer updateCustomer(Customer c, Map<String, String> m) {
        fill(c, m);
        customerRepo.save(c);
        return c;
    }

    public Customer updateKYC(Customer c, String pan, String aadhar) {
        c.setPan(pan);
        c.setAadhar(aadhar);
        customerRepo.save(c);
        return c;
    }

    private Customer fill(Customer c, Map<String, String> m){
        c.setName(m.get("name"));
        c.setEmail(m.get("email"));
        c.setPhone(m.get("phone"));
        c.setAadhar(m.get("aadhar"));
        c.setPan(m.get("pan"));
        return c;
    }

    public Optional<Customer> getCustomerById(long id){

        return customerRepo.findById(id);

    }
    public Customer deleteCustomer(Customer c) {
        try{
            log.info("deleting customer");
            customerRepo.delete(c);
        }
        catch (Exception e){
            log.trace("An error while delete, ",e);
        }

        return c;
    }



}
