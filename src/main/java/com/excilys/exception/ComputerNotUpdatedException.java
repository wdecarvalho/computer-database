package com.excilys.exception;

public class ComputerNotUpdatedException extends ComputerException {

    private static final long serialVersionUID = 4659275341316203999L;
    private static final String MESSAGE = "L'ordinateur d'ID %s n'a pas été mit à jour car une erreur est apparu (Verifier que l'ID existe en BD)";

    /**
     * Crée l'exception avec le bon message pour le bon ID.
     * @param msg
     *            ID de l'ordinateur
     */
    public ComputerNotUpdatedException(String msg) {
        super(String.format(MESSAGE, msg));
    }
}
