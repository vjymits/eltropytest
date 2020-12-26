package com.elt.bank.repo;

import com.elt.bank.modal.Privilege;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrivilegeRepo extends CrudRepository<Privilege, Long> {

    /**
     * Find the Privilege by name
     * @param name
     * @return Matching Privilege entity
     */
    public Privilege findByName(String name);

}
