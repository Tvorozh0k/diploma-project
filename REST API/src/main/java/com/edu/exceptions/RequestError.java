package com.edu.exceptions;

import lombok.Data;

import java.util.Date;

@Data
public class RequestError {
    private int status;
    private String message;
    private Date timestamp;

    public RequestError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = new Date();
    }
}
