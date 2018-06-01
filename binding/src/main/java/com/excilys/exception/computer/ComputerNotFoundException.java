package com.excilys.exception.computer;

import com.excilys.exception.ComputerException;
import com.excilys.exception.ExceptionHelper;

import static com.excilys.exception.ExceptionCode.COMPUTER_NOT_FOUND;

public class ComputerNotFoundException extends ComputerException {

    private static final long serialVersionUID = 1L;
    private static final String CODE = COMPUTER_NOT_FOUND.toString();

    /**
     * Permet de creer une computerNotFoundException.
     * @param msg Message a afficher
     */
    public ComputerNotFoundException(String msg) {
        super(ExceptionHelper.getMessageByCode(CODE, msg));
    }

}
