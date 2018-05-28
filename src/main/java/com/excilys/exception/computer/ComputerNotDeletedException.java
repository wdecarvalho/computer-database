package com.excilys.exception.computer;

import com.excilys.exception.ComputerException;
import com.excilys.exception.ExceptionHelper;

import static com.excilys.exception.ExceptionCode.COMPUTER_ALL_NOT_DELETE;
import static com.excilys.exception.ExceptionCode.COMPUTER_SOME_NOT_DELETE;

public class ComputerNotDeletedException extends ComputerException {

    private static final long serialVersionUID = 8121294179930154255L;

    private static final String CODE_WARNING = COMPUTER_SOME_NOT_DELETE.toString();

    private static final String CODE_ERROR = COMPUTER_ALL_NOT_DELETE.toString();

    /**
     * Constructeur de ComputerNotDeletedException pour errors.
     * @param msg
     *            Message d'erreur
     */
    public ComputerNotDeletedException(String msg) {
        super(new ExceptionHelper().getMessageByCode(CODE_ERROR));
    }

    /**
     * Constructeur de ComputerNotDeletedException pour warnings.
     */
    public ComputerNotDeletedException() {
        super(new ExceptionHelper().getMessageByCode(CODE_WARNING));
    }

}
