package com.excilys.exception.date;

public class LocalDateExpectedException extends Exception {

    private static final long serialVersionUID = -5306930537269407080L;
    private static final String MESSAGE = "La date %s ne respecte pas le format JJ-MM-AAAA ou le jour ou le mois n'existe pas";

    /**
     * Contructeur de l'exception.
     * @param date
     *            a l'origine de l'exception
     */
    public LocalDateExpectedException(String date) {
        super(String.format(MESSAGE, date));
    }

}