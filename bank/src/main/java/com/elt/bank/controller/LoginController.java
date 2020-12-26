package com.elt.bank.controller;

import com.elt.bank.modal.User;
import com.elt.bank.repo.UserRepo;
import com.elt.bank.util.Constants;
import com.elt.bank.util.JWTUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping(Constants.API_BASE_URL+"/auth")
public class LoginController {
    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    UserRepo userRepo;

    @Autowired
    PasswordEncoder passwordEncoder;

    /**
     * Login for admin user.
     * @param reqBody
     * @return
     */
    @PostMapping("/admin")
    public ResponseEntity<Object> adminLogin(@RequestBody Map<String, String> reqBody) {
        final String username = reqBody.get("username");
        final String password = reqBody.get("password");

        if(password == null || password.length() == 0)
            return new ResponseEntity<>(errorResponse("Invalid password"),
                    HttpStatus.UNAUTHORIZED);

        User user = userRepo.findByUserName(username);
        if(user == null ||  !passwordEncoder.matches(password, user.getPassword())) {
            return new ResponseEntity<>(errorResponse("Incorrect username or password"),
                    HttpStatus.UNAUTHORIZED);
        }
        String token = JWTUtils.genrateJwtToken(user);
        Map<String, Object> authResult = new TreeMap<>();
        authResult.put("token", token);
        authResult.put("name", user.getFirstName()+" "+user.getLastName());
        authResult.put("username", user.getUserName());
        authResult.put("email", user.getEmail());
        return new ResponseEntity<>(authResult, HttpStatus.OK);
    }

    /**
     * Sign out by putting token in black list.
     * @param body
     * @return
     */
    @PutMapping("logout")
    public ResponseEntity<Object> logout(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        JWTUtils.logout(token);
        Map<String, String> res = new HashMap<>();
        res.put("token", token);
        return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
    }

    /**
     * Common method to be used to send error response.
     *
     * @param msg response to be sent as in message key
     * @return Map containing the response data
     */
    private Map<String, String> errorResponse(String msg) {
        Map<String, String> errorResult = new TreeMap<>();
        errorResult.put("status", "error");
        errorResult.put("message", msg);
        return errorResult;
    }


}
