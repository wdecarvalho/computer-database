package com.excilys.exception.computer;

import com.excilys.constants.commons.message.MessageValidationAndException;
import com.excilys.exception.ComputerException;

public class ComputerNameNotPresentException extends ComputerException {

    private static final long serialVersionUID = 1L;

    /**
     * Permet de creer une ComputerNameNotPresentException.
     */
    public ComputerNameNotPresentException() {
        super(MessageValidationAndException.NAME_IS_REQUIRED);
    }
}
