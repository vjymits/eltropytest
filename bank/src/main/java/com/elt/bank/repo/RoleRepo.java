package com.elt.bank.repo;

import com.elt.bank.modal.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepo extends CrudRepository<Role, Long> {

    /**
     * Find the Role by name
     * @param name
     * @return Matching Role entity
     */
    public Role findByName(String name);
}
