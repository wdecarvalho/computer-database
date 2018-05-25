package com.excilys.controleurs;

public enum AttributeToSend {
    /*
     * ControleurMain
     */
    COMPUTERS("computers"),
    NB_COMPUTERS("nbComputers"),
    LIMIT("limit"),
    PAGE_COURANTE("pageCourante"),

    /*
     * ControleurComputer
     */
    COMPANYS("companys");
    private final String param;

    /**
     * Constructeur des requestParams.
     * @param s
     *            Nom du requestParam
     */
    AttributeToSend(final String s) {
        this.param = s;
    }

    @Override
    public String toString() {
        return this.param;
    }
}
