package com.excilys.service;

public class ServiceUtil {

    public static final int NB_PAGE = ServiceCdb.NB_PAGE;

    /**
     * Verifie que la page demandée existe et si elle n'existe pas donne la page la
     * plus cohérente par rapport à celle demandée.
     * @param nbPageMax
     *            Page maximum atteignable
     * @param pageRequired
     *            Page demandée
     * @return Page demandée ou possible
     */
    private static int verifyPageRequestedIsValidOrPutOne(final long nbPageMax, int pageRequired) {
        int pageRequested = pageRequired - 1;
        if (pageRequested > nbPageMax) {
            pageRequested = (int) nbPageMax;
        } else if (pageRequested < 0) {
            pageRequested = 0;
        }
        return pageRequested;
    }

    /**
     * Verifie que le nombre de resultat est renseignée, s'il ne l'ai pas prend
     * celui par défaut.
     * @param nbComputer
     *            Nombre de computer
     * @param pageAndNumberResult
     *            Page demandée et nombre de resultat
     * @return page Maximum atteignable
     */
    private static long verifyVaragsExistAndSetNbPageMax(final long nbComputer, int... pageAndNumberResult) {
        final long nbPageMax;
        if (pageAndNumberResult.length > 1) {
            nbPageMax = nbComputer / pageAndNumberResult[1];
        } else {
            nbPageMax = nbComputer / NB_PAGE;
        }
        return nbPageMax;
    }

    /**
     * Verifie que le nombre de resultat est renseignée et prend la page demandée ou
     * la page la plus appropriée si celle demandée n'existe pas.
     * @param nbComputer
     *            Nombre de computer
     * @param pageAndNumberResult
     *            Page demandée et nombre de résultat
     * @return .
     */
    public static int getTheRequestPageOrTheBestAppropriate(final long nbComputer, int... pageAndNumberResult) {
        final long nbPageMax = verifyVaragsExistAndSetNbPageMax(nbComputer, pageAndNumberResult);
        return verifyPageRequestedIsValidOrPutOne(nbPageMax, pageAndNumberResult[0]);
    }
}
