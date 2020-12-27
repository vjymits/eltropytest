package com.elt.bank.controller;

import com.elt.bank.modal.User;
import com.elt.bank.pojo.UserPojo;
import com.elt.bank.service.UserService;
import com.elt.bank.util.Constants;
import com.elt.bank.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@RestController
@RequestMapping(Constants.API_BASE_URL)
public class EmployeeController {

    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private UserService userService;


    @PostMapping(Constants.EMP_URL)
    public ResponseEntity<Object> createEmployee(
            @RequestAttribute("user") UserPojo currentuser,
            @RequestBody Map<String, String> body) {
        log.info("Adding emp");
        String username = body.get("username");
        String password = body.get("password");
        // validate username
        if(username == null || username.length() == 0) {

            return new ResponseEntity<>(errorResponse("Invalid Username!"), HttpStatus.BAD_REQUEST);
        }
        //validate password
        if(password == null || password.length() == 0) {

            return new ResponseEntity<>(errorResponse("Invalid password!"), HttpStatus.BAD_REQUEST);
        }
        // find the duplicate if any
        User duplicate = userService.getUserByName(username);
        if(duplicate != null) {
            log.error("Tried creating a duplicate user by username: {}",currentuser.getUsername());
            return new ResponseEntity<>(errorResponse("Duplicate username!"), HttpStatus.BAD_REQUEST);
        }
        User newEmp = userService.createEmpUser(body);

        return new ResponseEntity<>(userResponse(newEmp), HttpStatus.OK);
    }

    /**
     * Delete the emp user
     * @param currentUser
     * @param empId
     * @return
     */
    @DeleteMapping(Constants.EMP_URL+"/{empId}")
    public ResponseEntity<Object> deleteEmployee (
            @RequestAttribute("user")UserPojo currentUser,
            @PathVariable("empId") Long empId) {
        Optional<User> o = userService.getUserById(empId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse("No Such employee exist."),
                    HttpStatus.BAD_REQUEST);
        User u = o.get();
        userService.deleteUser(u);
        return new ResponseEntity<>(userResponse(u), HttpStatus.OK);
    }

    /**
     * Get emp
     * @param currentUser
     * @param empId
     * @return
     */

    @GetMapping(Constants.EMP_URL+"/{empId}")
    public ResponseEntity<Object> getEmployee (
            @RequestAttribute("user")UserPojo currentUser,
            @PathVariable("empId") Long empId) {
        Optional<User> o = userService.getUserById(empId);
        if(!o.isPresent())
            return new ResponseEntity<>(errorResponse("No Such employee exist."),
                    HttpStatus.BAD_REQUEST);
        User u = o.get();
        return new ResponseEntity<>(userResponse(u), HttpStatus.OK);
    }


    /**
     * Common method to be used to send error response.
     *
     * @param msg response to be sent as in message key
     * @return Map containing the response data
     */
    private Map<String, String> errorResponse(String msg) {
        return ResponseUtil.errorResponse(msg);
    }

    private Map<String, Object> userResponse(User u) {
        Map<String, Object> res = new TreeMap<>();
        res.put("id", u.getId());
        res.put("username", u.getUserName());
        res.put("firstname", u.getFirstName());
        res.put("lastname", u.getLastName());
        res.put("email", u.getEmail());
        res.put("userType", u.getType());
        res.put("link", Constants.EMP_URL+"/"+u.getId());
        return res;
    }


}
