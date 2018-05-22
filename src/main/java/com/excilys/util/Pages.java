package com.excilys.util;

import java.util.ArrayList;
import java.util.Collection;

public class Pages<T> {

    private Collection<T> entities;
    private int pageCourante;
    private int pageMax;
    private int maxComputers;
    private int numberPerPageResult = 10;

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
        return (this.pageCourante - 1) * numberPerPageResult;
    }

    /**
     * Recupere l'indice du premier resultat.
     * @return L'indice du premier resultat
     */
    public int startResult() {
        if (pageCourante > pageMax) {
            final int nbResult = (pageMax - 1) * numberPerPageResult;
            return nbResult <= 0 ? 0 : nbResult;
        } else {
            return firstResult();
        }
    }

    public Collection<T> getEntities() {
        return entities;
    }

    /**
     * Recupere le nombre de computer trouvée et calcule le nombre de page maximum
     * possible.
     * @param maxComputers
     *            Nombre de computers
     */
    public void setPageMax(int maxComputers) {
        this.maxComputers = maxComputers;
        this.pageMax = (int) Math.ceil((double) maxComputers / numberPerPageResult);
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

    public int getMaxComputers() {
        return maxComputers;
    }

    public void setMaxComputers(int maxComputers) {
        this.maxComputers = maxComputers;
    }

    public int getNumberPerPageResult() {
        return numberPerPageResult;
    }

    public void setNumberPerPageResult(int numberPerPageResult) {
        this.numberPerPageResult = numberPerPageResult;
    }

}
