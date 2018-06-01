package com.excilys.exception.computer;

import static com.excilys.exception.ExceptionCode.COMPUTER_DATEINTRO_MINOR_DATEDISCON;

import com.excilys.exception.ComputerException;
import com.excilys.exception.ExceptionHelper;

public class DateIntroShouldBeMinorthanDisconException extends ComputerException {

    private static final long serialVersionUID = 1L;
    private static final String CODE = COMPUTER_DATEINTRO_MINOR_DATEDISCON.toString();

    /**
     * Affiche le message d'exception expliquant la regle sur les dates d'un
     * computer.
     * @param introduced
     *            String de la LocalDate introduced
     * @param discontinued
     *            String de la LocalDate discontinued
     */
    public DateIntroShouldBeMinorthanDisconException(final String introduced, final String discontinued) {
        super(ExceptionHelper.getMessageByCode(CODE, introduced, discontinued));
    }
}
