package com.excilys.exception.computer;

import com.excilys.exception.ComputerException;

import com.excilys.exception.ExceptionHelper;

import static com.excilys.exception.ExceptionCode.COMPUTER_NAME;

public class ComputerNameNotPresentException extends ComputerException {

    private static final long serialVersionUID = 1L;

    private static final String CODE = COMPUTER_NAME.toString();

    /**
     * Permet de creer une ComputerNameNotPresentException.
     */
    public ComputerNameNotPresentException() {
        super(new ExceptionHelper().getMessageByCode(CODE));
    }
}
