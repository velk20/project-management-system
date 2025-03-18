package com.mladenov.projectmanagement.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

public class AppResponseUtil {
    private static final Logger logger = LoggerFactory.getLogger(AppResponseUtil.class);
    private static HashMap<String, Object> response;

    public static AppResponseUtil created() {

        response = new HashMap<>();
        response.put("status", "created");
        response.put("code", HttpStatus.CREATED.value());
        return new AppResponseUtil();
    }

    public static AppResponseUtil success() {

        response = new HashMap<>();
        response.put("status", "success");
        response.put("code", HttpStatus.OK.value());
        return new AppResponseUtil();
    }

    public static AppResponseUtil error(HttpStatus status) {

        response = new HashMap<>();
        response.put("status", "error");
        response.put("code", status.value());
        return new AppResponseUtil();
    }

    public AppResponseUtil withMessage(String message) {
        response.put("message", message);
        return this;
    }

    public AppResponseUtil withErrors(List<String> errors){
        response.put("errors", errors);
        return this;
    }

    public AppResponseUtil withDetailedMessage(String message) {
        response.put("detailed-message", message);
        return this;
    }

    public AppResponseUtil logStackTrace(String stackTrace) {
        logger.error(stackTrace);
        return this;
    }

    public AppResponseUtil withData(Object data) {
        response.put("data", data);
        return this;
    }

    public ResponseEntity<Object> build() {

        int code = (int) response.get("code");
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(code));
    }
}
