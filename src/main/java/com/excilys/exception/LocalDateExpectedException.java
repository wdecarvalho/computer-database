package com.excilys.exception;

public class LocalDateExpectedException extends Exception {

    private static final String MESSAGE = "La date %s ne respecte pas le format JJ-MM-AAAA";

    /**
     * Contructeur de l'exception.
     * @param date
     *            a l'origine de l'exception
     */
    public LocalDateExpectedException(String date) {
        super(String.format(MESSAGE, date));
    }

}
