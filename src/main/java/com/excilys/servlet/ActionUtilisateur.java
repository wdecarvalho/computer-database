package com.excilys.servlet;

public enum ActionUtilisateur {

    ADDFORM("addForm"),
    ADD("add"),
    EDITFORM("editForm"),
    EDIT("edit"),
    DELETE("delete"),
    DEFAULT("default");

    private String action;

    /**
     * Construit l'enum d'action utilisateur.
     * @param action
     *            Action utilisateur
     */
    ActionUtilisateur(String action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return action;
    }

    /**
     * Recupere l'enum pour la string correspondante.
     * @param toconvert
     *            String a transformer en enum
     * @return Retoure l'enum ou null si non trouvé.
     */
    public static ActionUtilisateur getEnum(final String toconvert) {
        for (ActionUtilisateur messagetypeUser : values()) {
            if (messagetypeUser.action.equals(toconvert)) {
                return messagetypeUser;
            }
        }
        return DEFAULT;
    }

}
