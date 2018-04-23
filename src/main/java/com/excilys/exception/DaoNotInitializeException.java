package com.excilys.exception;

public class DaoNotInitializeException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Aucune DAO n'a été initialisé";

    /**
     * Permet de creer une DaoNotInitializeException.
     */
    public DaoNotInitializeException() {
        super(MESSAGE);
    }

    /**
     * Permet de creer une DaoNotInitializeException avec un message.
     * @param msg Message d'exception
     */
    public DaoNotInitializeException(String msg) {
        super(msg);
    }

}
