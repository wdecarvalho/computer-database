package com.excilys.exceptions;

import static com.excilys.exceptions.error.code.ExceptionCode.CONFLICT_UPDATE;

import com.excilys.exception.ExceptionHelper;

public class ConflictException extends RuntimeException {

    private static final long serialVersionUID = -5059998817808946857L;
    private static final String CODE = CONFLICT_UPDATE.toString();

    public ConflictException(String msg) {
        this();
    }

    public ConflictException() {
        super(ExceptionHelper.getMessageByCode(CODE));
    }
}
