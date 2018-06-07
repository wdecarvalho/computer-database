package com.excilys.exceptions;

import static com.excilys.exceptions.error.code.ExceptionCode.SEARCH_NO_CONTENT;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.excilys.exception.ExceptionHelper;

public class NoContentFoundException extends Exception {
    
    private static final long serialVersionUID = 1518884909462010816L;
    
    private static final String CODE = SEARCH_NO_CONTENT.toString();
    
    public NoContentFoundException(String msg) {
        this();
    }
    
    public NoContentFoundException() {
        super(ExceptionHelper.getMessageByCode(CODE));
    }

}
