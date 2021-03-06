package com.excilys.ui;

public enum FormEntry {

    DATE_DISCONTINUED("[date discontinued - YYYY-MM-DD] : "),
    DATE_INTRODUCED("[date introduced - YYYY-MM-DD] : "),
    COMPUTER_NAME("[name] : "),
    CURRENT("[current :] "),
    COMPANY_ID("[id - 0-9] : ");

    private String name;

    /**
     * Contructeur de FormEntry.
     * @param name
     *            Nom de l'enum
     */
    FormEntry(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
