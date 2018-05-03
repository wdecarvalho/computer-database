package com.excilys.exception;

public class ComputerNotFoundException extends ComputerException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "This computer not exist : ";

    /**
     * Permet de creer une computerNotFoundException.
     * @param msg Message a afficher
     */
    public ComputerNotFoundException(String msg) {
        super(MESSAGE + "" + msg);
    }

}
