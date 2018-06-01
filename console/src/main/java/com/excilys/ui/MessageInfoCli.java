package com.excilys.ui;

public enum MessageInfoCli {

    /*
     * Error message
     */
    CHOIX_INCORRECTE("Choix incorrecte ! \n"),
    ID_NUMBER_ONLY("L'ID de %s doit être composé uniquement de nombre [0-9] "),
    DELETE_ERROR_COMPANY("Les computers n'ont pas été supprimés car la companie n'existe pas"),
    COMPANY_NOT_FOUND("Cette companie n'existe pas : "),
    PAGE_NUMBER_ONLY("Le numero de page doit être composé uniquement de nombre [0-9] "),

    /*
     * Success message
     */
    DELETE_SUCCESSFULL("%s d'id %s a été correctement supprimé"),

    /*
     * Others
     */
    AU_REVOIR("Au revoir ! ");

    private final String message;

    /**
     * Créer un MessageInfoCLi.
     * @param messageInfo
     *            Message a afficher pour l'utilisateur
     */
    MessageInfoCli(String messageInfo) {
        message = messageInfo;
    }

    @Override
    public String toString() {
        return message;
    }

}
