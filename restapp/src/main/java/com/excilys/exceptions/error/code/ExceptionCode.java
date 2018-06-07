package com.excilys.exceptions.error.code;

public enum ExceptionCode {

    SEARCH_NO_CONTENT("search.no.content");

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
