package com.elt.bank.util;

import java.util.Map;
import java.util.TreeMap;

public class ResponseUtil {

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
}
