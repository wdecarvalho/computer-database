package com.excilys.exception.computer;

import com.excilys.exception.ComputerException;
import com.excilys.exception.ExceptionHelper;

import static com.excilys.exception.ExceptionCode.COMPUTER_NEED_ID_FOR_UPDATE;

public class ComputerNeedIdToBeUpdateException extends ComputerException {

    private static final long serialVersionUID = 1L;
    private static final String CODE = COMPUTER_NEED_ID_FOR_UPDATE.toString();

    /**
     * Permet de creer une computerNeedIdToBeUpdateException.
     */
    public ComputerNeedIdToBeUpdateException() {
        super(new ExceptionHelper().getMessageByCode(CODE));
    }
}
