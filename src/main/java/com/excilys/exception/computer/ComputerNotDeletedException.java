package com.excilys.exception.computer;

import com.excilys.exception.ComputerException;

public class ComputerNotDeletedException extends ComputerException {

    private static final long serialVersionUID = 8121294179930154255L;

    private static final String MESSAGE_WARNING = "Un ou plusieurs computers n'ont pas pû être supprimés !";

    private static final String MESSAGE_ERROR = "Aucun computer n'a pû être supprimés ! ";

    /**
     * Constructeur de ComputerNotDeletedException pour errors.
     * @param msg
     *            Message d'erreur
     */
    public ComputerNotDeletedException(String msg) {
        super(MESSAGE_ERROR);
    }

    /**
     * Constructeur de ComputerNotDeletedException pour warnings.
     */
    public ComputerNotDeletedException() {
        super(MESSAGE_WARNING);
    }

}
