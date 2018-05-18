package com.excilys.exception.computer;

import com.excilys.exception.ComputerException;

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
