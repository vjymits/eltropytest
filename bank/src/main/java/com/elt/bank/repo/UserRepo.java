package com.elt.bank.repo;

import com.elt.bank.modal.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends CrudRepository<User, Long> {

    /**
     * Find user by user name.
     * @param username
     * @return
     */
    public User findByUserName(String username);

}
