package com.excilys.exception;

public class DateTruncationException extends Exception {

    private static final String MESSAGE = "Votre date est invalide ( Attention l'année doit être superieure à 1970 )";

    /**
     * Affiche le message d'erreur si une LocalDate/Timestamp n'arrive pas a se
     * stocker en base de donnée.
     */
    public DateTruncationException() {
        super(MESSAGE);
    }

}
