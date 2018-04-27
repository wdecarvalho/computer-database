package com.excilys.tags;

public enum TypeAlerte {
    SUCCESS("Success "), INFO("Info "), WARNING("Warning "), ERROR("Error ");

    private String name;

    /**
     * Constructeur de TypeAlerte.
     * @param name
     *            Nom de l'alerte
     */
    TypeAlerte(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

}
