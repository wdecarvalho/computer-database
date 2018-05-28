package com.excilys.exception.company;

import static com.excilys.exception.ExceptionCode.COMPANY_NOT_FOUND_CODE;

import com.excilys.exception.CompanyException;
import com.excilys.exception.ExceptionHelper;

public class CompanyNotFoundException extends CompanyException {

    private static final long serialVersionUID = 1L;

    private static final String CODE = COMPANY_NOT_FOUND_CODE.toString();

    /**
     * Creer une CompanyNotFoundException.
     * @param msg
     *            Message d'exception
     */
    public CompanyNotFoundException(String msg) {
        super(new ExceptionHelper().getMessageByCode(CODE, msg));
    }
}
