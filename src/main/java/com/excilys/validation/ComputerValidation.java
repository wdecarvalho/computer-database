package com.excilys.validation;

import java.time.LocalDate;

import com.excilys.exception.ComputerNameNotPresentException;
import com.excilys.exception.ComputerNeedIdToBeUpdateException;
import com.excilys.exception.DateIntroShouldBeMinorthanDisconException;

public class ComputerValidation {

    /**
     * Non instanciable de l'exterieur.
     */
    private ComputerValidation() {

    }

    /**
     * Verifie que le champ obligatoire nom est bien présent.
     * @param name
     *            Nom du computer
     * @throws ComputerNameNotPresentException
     *             Le nom est obligatoire
     */
    public static void nameIsRequiredForComputer(final String name) throws ComputerNameNotPresentException {
        if (name == null || name.isEmpty()) {
            throw new ComputerNameNotPresentException();
        }
    }

    /**
     * Verifie que la date discontinued est plus recente que la date introduced.
     * @param intro
     *            LocalDate introduced
     * @param discon
     *            LocalDate discontinued
     * @throws DateIntroShouldBeMinorthanDisconException
     *             Exception si la date ne respecte pas la regle
     */
    public static void dateIntroMinorThanDateDiscon(final LocalDate intro, final LocalDate discon)
            throws DateIntroShouldBeMinorthanDisconException {
        boolean dateIntroMinorThanDateDisco = intro != null && discon != null && discon.isBefore(intro);
        if (dateIntroMinorThanDateDisco) {
            throw new DateIntroShouldBeMinorthanDisconException(intro.toString(), discon.toString());
        }
    }

    /**
     * Verifie avant mise a jour que l'ID du computer est présent (lié a la BD).
     * @param id
     *            ID du computer
     * @throws ComputerNeedIdToBeUpdateException
     *             L'ID est obligatoire pour mise a jour
     */
    public static void idIsRequiredForComputerUpdate(final Long id) throws ComputerNeedIdToBeUpdateException {
        if (id == null) {
            throw new ComputerNeedIdToBeUpdateException();
        }
    }

}
