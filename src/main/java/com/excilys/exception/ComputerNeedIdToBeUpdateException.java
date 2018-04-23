package com.excilys.exception;

public class ComputerNeedIdToBeUpdateException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Un ID est requis pour pouvoir mettre à jour le computer souhaité";

    /**
     * Permet de creer une computerNeedIdToBeUpdateException.
     */
    public ComputerNeedIdToBeUpdateException() {
        super(MESSAGE);
    }
}
