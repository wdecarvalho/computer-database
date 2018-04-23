package com.excilys.dao;

public enum DaoType {
    COMPUTER_DAO("computer_dao"), COMPANY_DAO("company_dao");

    private String name;

    /**
     * Permet de creer un Daotype avec un nom.
     * @param name
     *            Nom de l'enum
     */
    DaoType(String name) {
        this.name = name;
    }

    /**
     * Permet d'afficher l'enum en String.
     * @return Nom de l'enum
     */
    public String toString() {
        return name;
    }
}
