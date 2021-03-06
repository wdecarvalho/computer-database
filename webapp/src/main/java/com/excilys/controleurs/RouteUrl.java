package com.excilys.controleurs;

public enum RouteUrl {

    /*
     * JSP pour GET et other pour post
     */
    DASHBOARD("redirect:/dashboard"),
    DASHBOARD_JSP("dashboard"),
    ADDCOMPUTER_JSP("addComputer"),
    ADDCOMPUTER("redirect:/computer"),
    EDITCOMPUTER_JSP("editComputer"),
    EDITCOMPUTER("redirect:/computer/%s"),

    /*
     * Errors pages redirection
     */
    ERROR_PAGE_409("redirect:/static/views/409.jsp");

    private final String url;

    /**
     * Construit l'enum des routes pour le site.
     * @param url
     *            Destination
     */
    RouteUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return url;
    }
}
