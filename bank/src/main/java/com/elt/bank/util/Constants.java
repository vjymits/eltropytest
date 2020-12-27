package com.elt.bank.util;

import java.util.HashSet;
import java.util.Set;

public interface Constants {

    String API_BASE_URL = "bank/api/v1";
    String LOGIN_URL = API_BASE_URL+"/auth";
    String EMP_URL = "/employees";
    String CUST_URL = API_BASE_URL+"/customers";
    String ACC_URL = API_BASE_URL+"/accounts";

    // Security constants

    String SECRET = "SecretEltro12y";
    //long EXPIRATION_TIME = 864_000_000; // 10 days
    long EXPIRATION_TIME = 60000*10;   // 10 min
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";

    //JWT token keys
    String JWT_TOKEN_TIMESTAMP = "timestamp";
    String JWT_TOKEN_ROLES = "roles";
    String JWT_TOKEN_EMAIL = "email";
    String JWT_ISSUE_TIME = "issueTime";
    String JWT_EXPIRY_TIME = "expiryTime";
    String JWT_USER_NAME = "username";

    // Admin privileges
    String ADD_EMP_PRIVILEGE = "ADD_EMP_PRIVILEGE";
    String DEL_EMP_PRIVILEGE = "DEL_EMP_PRIVILEGE";

    // Emp privileges
    String ADD_CUST = "ADD_CUSTOMER";
    String DEL_CUST = "DEL_CUST";
    String UPDATE_CUST = "UPDATE_CUST";
    String READ_CUST= "READ_CUST";

    String ADD_ACC = "ADD_ACC";
    String DEL_ACC = "DEL_ACC";
    String UPDATE_ACC = "UPADTE_ACC";
    String READ_ACC = "READ_ACC";

    //TRN Prvs
    String ADD_TRN = "ADD_TRN";
    String READ_TRN = "READ_TRN";

    //USER TYPE
    String ADMIN_USER_TYPE = "a";
    String EMP_USER_TYPE = "e";



}
