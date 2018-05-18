package com.excilys.validation;

import java.time.LocalDate;

import com.excilys.exception.ComputerException;
import com.excilys.exception.computer.ComputerNameNotPresentException;
import com.excilys.exception.computer.ComputerNeedIdToBeUpdateException;
import com.excilys.exception.computer.DateIntroShouldBeMinorthanDisconException;
import com.excilys.model.Computer;

public class ComputerValidation {

    /**
     * Non instanciable de l'exterieur.
     */
    private ComputerValidation() {

    }

    /**
     * Verifie que le nom du computer est bien présent et que la date introduced <=
     * date discontinued.
     * @param c
     *            Computer
     * @throws ComputerException
     *             Si une erreur de validation intervient Si le nom de l'ordinateur
     *             n'est pas présent
     */
    public static void validateComputerNameAndDate(final Computer c) throws ComputerException {
        nameIsRequiredForComputer(c.getName());
        dateIntroMinorThanDateDiscon(c.getIntroduced(), c.getDiscontinued());
    }

    /**
     * Verifie que l'id est present pour un objet attaché a la BD et valide des
     * pré-requis.
     * @param c
     *            Computer
     * @throws ComputerException
     *             Si une erreur de validation intervient Si le nom de l'ordinateur
     *             n'est pas présent
     */
    public static void validateComputerAndVerifyPresenceId(final Computer c) throws ComputerException {
        idIsRequiredForComputerUpdate(c.getId());
        validateComputerNameAndDate(c);

    }

    /**
     * Verifie que le champ obligatoire nom est bien présent.
     * @param name
     *            Nom du computer
     * @throws ComputerNameNotPresentException
     *             Le nom est obligatoire
     */
    private static void nameIsRequiredForComputer(final String name) throws ComputerNameNotPresentException {
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
    private static void dateIntroMinorThanDateDiscon(final LocalDate intro, final LocalDate discon)
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
    private static void idIsRequiredForComputerUpdate(final Long id) throws ComputerNeedIdToBeUpdateException {
        if (id == null) {
            throw new ComputerNeedIdToBeUpdateException();
        }
    }

}
