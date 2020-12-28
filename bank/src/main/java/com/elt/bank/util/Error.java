package com.elt.bank.util;

public interface Error {

    // Account error msg
    String NO_ACC = "No such account exist.";
    String NOT_ENOUGH_BAL = "Insufficient balance!";

    // KYC
    String MANDATORY_PAN = "PAN no is mandatory.";
    String MANDATORY_ADR = "Aadhar no is mandatory.";

}
