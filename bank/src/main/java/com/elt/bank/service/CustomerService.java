package com.elt.bank.service;

import com.elt.bank.modal.Customer;

import java.util.Map;
import java.util.Optional;

public interface CustomerService {

    public Customer createCustomer(Map<String, String> m);
    public Optional<Customer> getCustomerById(long id);
    public Customer deleteCustomer(Customer c);
    public Customer updateCustomer(Customer c, Map<String, String> m);
    public Customer updateKYC(Customer c, String pan, String aadhar);

}
