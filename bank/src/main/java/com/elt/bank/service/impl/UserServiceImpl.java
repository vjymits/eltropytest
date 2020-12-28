package com.elt.bank.service.impl;

import com.elt.bank.modal.Role;
import com.elt.bank.modal.User;
import com.elt.bank.repo.RoleRepo;
import com.elt.bank.repo.UserRepo;
import com.elt.bank.service.UserService;
import com.elt.bank.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepo roleRepo;


    @Override
    public User createEmpUser(Map<String, String> data) {
        User newEmp = new User();
        newEmp.setType(Constants.EMP_USER_TYPE);
        newEmp.setUserName(data.get("username"));
        newEmp.setFirstName(data.get("firstname"));
        newEmp.setLastName(data.get("lastname"));
        newEmp.setEmail(data.get("email"));
        newEmp.setPassword(passwordEncoder.encode(data.get("password")));
        Role empRole = roleRepo.findByName("ROLE_EMP");
        log.info("Role : {}",empRole);
        newEmp.setRoles(Arrays.asList(empRole));
        newEmp = userRepo.save(newEmp);
        return newEmp;

    }

    @Override
    public User getUserByName(String username) {
        return  userRepo.findByUserName(username);
    }

    @Override
    public Optional<User> getUserById(long id){
       return userRepo.findById(id);
    }

    @Override
    public User deleteUser(User u) {
        userRepo.delete(u);
        return u;
    }


}
