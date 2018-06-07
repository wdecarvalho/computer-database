package com.excilys.exceptions.api;

public class ApiError {

    private String exceptionName;

    private String message;

    private String cause;

    public ApiError(String name, String message, String cause) {
        this.exceptionName = name;
        this.message = message;
        this.cause = cause;
    }

    /**
     * @return the exceptionName
     */
    public String getExceptionName() {
        return exceptionName;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the cause
     */
    public String getCause() {
        return cause;
    }
}
