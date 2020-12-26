package com.elt.bank.util;

public interface Constants {

    String API_BASE_URL = "bank/api/v1";

    // Security constants

    String SECRET = "SecretEltro12y";
    //long EXPIRATION_TIME = 864_000_000; // 10 days
    long EXPIRATION_TIME = 60000;   // 1 min
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";

    //JWT token keys
    String JWT_TOKEN_TIMESTAMP = "timestamp";
    String JWT_TOKEN_ROLES = "roles";
    String JWT_TOKEN_EMAIL = "email";


}
