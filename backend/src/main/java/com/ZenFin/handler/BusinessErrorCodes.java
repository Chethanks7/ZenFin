package com.ZenFin.handler;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

import static org.springframework.http.HttpStatus.*;
@Getter
public enum BusinessErrorCodes {

    NO_CODE(0,NOT_IMPLEMENTED, "No code"),
    INCORRECT_CURRENT_PASSWORD(300,BAD_REQUEST, "Incorrect current password"),
    NEW_PASSWORD_DOES_NOT_MATCH(301,BAD_REQUEST, "new Password does not match"),
    ACCOUNT_DISABLED(303,BAD_REQUEST, "user account is disabled"),
    ACCOUNT_LOCKED(302,FORBIDDEN, "User Account is locked"),
    BAD_CREDENTIALS(304,BAD_REQUEST, "Bad credentials");

    private final int code ;
    private final String description;
    private final HttpStatusCode httpStatusCode;
    BusinessErrorCodes(int code, HttpStatusCode httpStatusCode,String description ) {
        this.code = code;
        this.description = description;
        this.httpStatusCode = httpStatusCode;
    }
}
