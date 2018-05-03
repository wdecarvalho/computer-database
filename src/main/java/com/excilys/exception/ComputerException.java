package com.excilys.exception;

public class ComputerException extends Exception {

    /**
     * Gere toutes les exceptions propre au computer.
     * @param msg
     *            Message d'exception a afficher
     */
    public ComputerException(String msg) {
        super(msg);
    }
}
