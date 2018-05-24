package com.excilys.servlet;

public enum RouteUrl {

    DASHBOARD("redirect:/dashboard"),
    DASHBOARD_JSP("dashboard"),
    ADDCOMPUTER_JSP("addComputer"),
    ERROR_PAGE_404("static/views/404.html"),
    EDITCOMPUTER_JSP("editComputer");

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
