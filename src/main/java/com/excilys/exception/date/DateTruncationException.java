package com.excilys.exception.date;

public class DateTruncationException extends Exception {

    private static final long serialVersionUID = -6459166391539885269L;
    private static final String MESSAGE = "Votre date est invalide ( Attention l'année doit être superieure à 1970 )";

    /**
     * Affiche le message d'erreur si une LocalDate/Timestamp n'arrive pas a se
     * stocker en base de donnée.
     */
    public DateTruncationException() {
        super(MESSAGE);
    }

}
