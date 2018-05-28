package com.excilys.exception.date;

import com.excilys.exception.ExceptionHelper;

import static com.excilys.exception.ExceptionCode.DATE_TRUNCATION_CODE;

public class DateTruncationException extends Exception {

    private static final long serialVersionUID = -6459166391539885269L;
    private static final String CODE = DATE_TRUNCATION_CODE.toString();

    /**
     * Affiche le message d'erreur si une LocalDate/Timestamp n'arrive pas a se
     * stocker en base de donnée.
     */
    public DateTruncationException() {
        super(new ExceptionHelper().getMessageByCode(CODE));
    }

}
