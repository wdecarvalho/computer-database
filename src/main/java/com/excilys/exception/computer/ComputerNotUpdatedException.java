package com.excilys.exception.computer;

import com.excilys.exception.ComputerException;
import com.excilys.exception.ExceptionHelper;

import static com.excilys.exception.ExceptionCode.COMPUTER_NOT_UPDATE;

public class ComputerNotUpdatedException extends ComputerException {

    private static final long serialVersionUID = 4659275341316203999L;
    private static final String CODE = COMPUTER_NOT_UPDATE.toString();

    /**
     * Crée l'exception avec le bon message pour le bon ID.
     * @param msg
     *            ID de l'ordinateur
     */
    public ComputerNotUpdatedException(String msg) {
        super(new ExceptionHelper().getMessageByCode(CODE, msg));
    }
}
