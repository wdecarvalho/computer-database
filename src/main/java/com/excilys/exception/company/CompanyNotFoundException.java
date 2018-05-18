package com.excilys.exception.company;

public class CompanyNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "This company not exist : ";

    /**
     * Creer une CompanyNotFoundException.
     * @param msg Message d'exception
     */
    public CompanyNotFoundException(String msg) {
        super(MESSAGE + "" + msg);
    }

}
