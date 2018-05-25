package com.excilys.servlet;

public enum MessagetypeUser {

    /*
     * Message to send to user
     */
    DELETE_NO_COMPUTER_SELECTED("Aucun ordinateur n'a été sélectionné pour la suppresion"),
    DELETE_SUCCESSFULL_COMPUTER("Les ordinateurs ont été correctement supprimés"),
    UPDATE_SUCCESSFULL_COMPUTER("L'ordinateur a été correctement modifié"),
    ADD_ERROR_COMPUTER("Une erreur a empeché la création de l'ordinateur"),
    ADD_SUCCESSFULL_COMPUTER("L'ordinateur a été correctement sauvegardé"),

    /*
     * To construct message .
     */
    TYPE_MESSAGE("typeMessage"),
    MESSAGE_USER("messageUser");

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
