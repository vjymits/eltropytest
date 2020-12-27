package com.elt.bank.service;

import com.elt.bank.modal.User;

import java.util.Map;
import java.util.Optional;

public interface UserService {

    public User createEmpUser(Map<String, String> data);

    public User getUserByName(String username);

    public Optional<User> getUserById(long id);

    public User deleteUser(User u);


}
