package com.excilys.exception;

public enum ExceptionCode {

    COMPANY_NOT_FOUND_CODE("company.not.exist"),
    DATE_TRUNCATION_CODE("date.truncation"),
    COMPUTER_NAME("computer.name"),
    PAGE_NOT_FOUND("page.not.found"),
    COMPUTER_NEED_ID_FOR_UPDATE("computer.need.id.for.update"),
    COMPUTER_SOME_NOT_DELETE("computer.some.not.delete"),
    COMPUTER_ALL_NOT_DELETE("computer.all.not.delete"),
    COMPUTER_NOT_FOUND("computer.not.found"),
    COMPUTER_NOT_UPDATE("computer.not.update"),
    COMPUTER_DATEINTRO_MINOR_DATEDISCON("computer.dateintro.minor.datediscon"),
    LOCALDATE_FORMAT_ERROR("localdate.format.error");

    private String code;

    /**
     * Constructeur des codes des fichiers properties.
     * @param code
     *            Code pour acceder aux messages du fichier properties
     */
    ExceptionCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return this.code;
    }

}
