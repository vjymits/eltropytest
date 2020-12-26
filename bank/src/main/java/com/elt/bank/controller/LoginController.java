package com.elt.bank.controller;

import com.elt.bank.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(Constants.API_BASE_URL+"/login")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    /**
     * Login for admin user.
     * @param reqBody
     * @return
     */
    @PostMapping("/adm")
    public ResponseEntity<Object> adminLogin(@RequestBody Map<String, String> reqBody) {
        final String username = reqBody.get("username");
        final String password = reqBody.get("password");
        Map<String, String> res = new HashMap<>();
        res.put("token", "Bearer jdw49sn934sdb7die3849fns7882nc0vs7tf");
        log.info("Login request from user: "+username);
        return new ResponseEntity<>(res, HttpStatus.OK);

    }


}
