package com.excilys.exception;

import static com.excilys.exception.ExceptionCode.PAGE_NOT_FOUND;

public class PageNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    public static final String CODE = PAGE_NOT_FOUND.toString();

    /**
     * Crée une pageNotFoundException avec un message.
     * @param msg
     *            Message d'erreur
     */
    public PageNotFoundException(String msg) {
        super(new ExceptionHelper().getMessageByCode(CODE, msg));
    }

}
