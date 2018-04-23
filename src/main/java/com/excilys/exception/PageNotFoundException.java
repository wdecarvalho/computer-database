package com.excilys.exception;

public class PageNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final String MESSAGE = "Cette page n'existe pas : ";

    /**
     * Crée une pageNotFoundException avec un message.
     * @param msg Message d'erreur
     */
    public PageNotFoundException(String msg) {
        super(MESSAGE + " " + msg);
    }

}
