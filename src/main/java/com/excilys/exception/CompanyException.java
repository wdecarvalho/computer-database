package com.excilys.exception;

public class CompanyException extends Exception {

    private static final long serialVersionUID = -6767205956858598407L;

    /**
     * Gere toutes les exceptions propre au computer.
     * @param msg
     *            Message d'exception a afficher
     */
    public CompanyException(String msg) {
        super(msg);
    }
}
