package com.elt.bank.repo;

import com.elt.bank.modal.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepo extends CrudRepository<Customer, Long> {

}
