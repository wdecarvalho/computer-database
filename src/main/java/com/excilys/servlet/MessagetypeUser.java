package com.excilys.servlet;

public enum MessagetypeUser {

    DELETE_NO_COMPUTER_SELECTED("Aucun ordinateur n'a été sélectionné pour la suppresion"), DELETE_SUCCESSFULL_COMPUTER(
            "Les ordinateurs ont été correctement supprimés"), DELETE_NO_VALID_ID(
                    "Les ordinateurs doivent être composée uniquement de nombre [0-9]"),

    UPDATE_SUCCESSFULL_COMPUTER("L'ordinateur a été correctement modifié"), ADD_ERROR_COMPUTER(
            "Une erreur a empeché la création de l'ordinateur"), ADD_SUCCESSFULL_COMPUTER(
                    "L'ordinateur a été correctement sauvegardé");

    private final String message;

    /**
     * Construit un message pour l'utilisateur.
     * @param message
     *            Message destiné a l'affichage
     */
    MessagetypeUser(final String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }

}
