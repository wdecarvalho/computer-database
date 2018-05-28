package com.excilys.exception.date;

import static com.excilys.exception.ExceptionCode.LOCALDATE_FORMAT_ERROR;

import com.excilys.exception.ExceptionHelper;

public class LocalDateExpectedException extends Exception {

    private static final long serialVersionUID = -5306930537269407080L;

    private static final String CODE = LOCALDATE_FORMAT_ERROR.toString();

    /**
     * Contructeur de l'exception.
     * @param date
     *            a l'origine de l'exception
     */
    public LocalDateExpectedException(String date) {
        super(new ExceptionHelper().getMessageByCode(CODE, date));
    }

}
