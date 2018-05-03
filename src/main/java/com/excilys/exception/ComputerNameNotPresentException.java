package com.excilys.exception;

public class ComputerNameNotPresentException extends ComputerException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "Le nom du computer est un champ obligatoire";

    /**
     * Permet de creer une ComputerNameNotPresentException.
     */
    public ComputerNameNotPresentException() {
        super(MESSAGE);
    }
}
