package com.excilys.servlet;

public enum RouteUrl {

    DASHBOARD_SERVLET("/dashboard"),
    DASHBOARD_JSP("jsp/dashboard.jsp"),
    ADDCOMPUTER_JSP("jsp/addComputer.jsp"),
    ERROR_PAGE_404("static/views/404.html"),
    EDITCOMPUTER_JSP("jsp/editComputer.jsp");

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
