package com.excilys.util;

import java.util.ArrayList;
import java.util.Collection;

public class Pages<T> {

    private Collection<T> entities;
    private int pageCourante;
    private int pageMax;
    private static final int NUMBER_PER_PAGE_RESULT = 20;

    /**
     * Constructeur de Pages.
     * @param pageCourante
     *            Page demandée
     */
    public Pages(int pageCourante) {
        this.pageCourante = pageCourante;
        entities = new ArrayList<>();
    }

    /**
     * Calcule l'indice du premier résultat de la page.
     * @return indice du premier indice
     */
    private int firstResult() {
        return (this.pageCourante - 1) * NUMBER_PER_PAGE_RESULT;
    }
    /**
     * Recupere le nombre de resultat par page.
     * @return Nombre de resultat par page.
     */
    public static int getNumberPageResult() {
        return NUMBER_PER_PAGE_RESULT;
    }

    /**
     * Recupere l'indice du premier resultat.
     * @return L'indice du premier resultat
     */
    public int startResult() {
        if (pageCourante > pageMax) {
            return (pageMax - 1) * NUMBER_PER_PAGE_RESULT;
        } else {
            return firstResult();
        }
    }

    public Collection<T> getEntities() {
        return entities;
    }

    public void setPageMax(int pageMax) {
        this.pageMax = (int) Math.ceil((double) pageMax / NUMBER_PER_PAGE_RESULT);
    }

    /**
     * Permet de recuperer la page courante.
     * @return Recupere la page courante
     */
    public int getPageCourante() {
        if (pageCourante > pageMax) {
            return pageMax;
        }
        return pageCourante;
    }

    public int getPageMax() {
        return pageMax;
    }
}
