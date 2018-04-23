package com.excilys.exception;

public class CompanyNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "This computer not exist : ";

    /**
     * Creer une CompanyNotFoundException.
     * @param msg Message d'exception
     */
    public CompanyNotFoundException(String msg) {
        super(MESSAGE + "" + msg);
    }

}
