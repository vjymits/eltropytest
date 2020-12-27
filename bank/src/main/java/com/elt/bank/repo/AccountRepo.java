package com.elt.bank.repo;

import com.elt.bank.modal.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface AccountRepo extends CrudRepository<Account, Long> {

    Set<Account> findAllByCustomerId(long custId);

}
