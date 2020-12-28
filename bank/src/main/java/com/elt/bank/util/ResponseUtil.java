package com.elt.bank.util;

import com.elt.bank.modal.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ResponseUtil {

    private ResponseUtil(){}

    /**
     * Common method to be used to send error response.
     *
     * @param msg response to be sent as in message key
     * @return Map containing the response data
     */
    public static Map<String, String> errorResponse(String msg) {
        Map<String, String> errorResult = new TreeMap<>();
        errorResult.put("status", "error");
        errorResult.put("message", msg);
        return errorResult;
    }


    /**
     * Common method to be used to send success response.
     *
     * @param msg response to be sent as in message key
     * @return Map containing the response data
     */
    public static Map<String, String> successResponse(String msg) {
        Map<String, String> res = new TreeMap<>();
        res.put("status", "success");
        res.put("message", msg);
        return res;
    }

    /**
     * Extract account into map.
     * @param a
     * @return
     */
    public static Map<String, Object> accountResponse(Account a) {
        Map<String, Object> m = new HashMap<>();
        m.put("accountType", a.getAccType());
        m.put("balance", a.getBalance());
        m.put("accountNo", a.getNo());
        m.put("customerId", a.getCustomer().getId());
        m.put("customerName", a.getCustomer().getName());
        m.put("link", "/"+Constants.API_BASE_URL+"/account/"+a.getNo());
        return m;
    }
}
