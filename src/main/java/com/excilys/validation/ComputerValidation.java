package com.excilys.validation;

import java.time.LocalDate;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.exception.ComputerException;
import com.excilys.exception.computer.ComputerNeedIdToBeUpdateException;
import com.excilys.exception.computer.DateIntroShouldBeMinorthanDisconException;
import com.excilys.model.Computer;

public class ComputerValidation {

    private static final String ERREUR_DE_VALIDATION = "Une erreur de validation d'integrité est apparu !";
    private static ValidatorFactory factory;
    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerValidation.class);

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
     * @throws DateIntroShouldBeMinorthanDisconException
     * @throws ComputerException
     *             Si une erreur de validation intervient Si le nom de l'ordinateur
     *             n'est pas présent
     */
    public static void validateComputerIntegrityAndDate(final Computer c) throws ComputerException {
        validationIntegrityModel(c);
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
    public static void validateComputerIntegrityAndVerifyPresenceId(final Computer c) throws ComputerException {
        idIsRequiredForComputerUpdate(c.getId());
        validateComputerIntegrityAndDate(c);
    }

    /**
     * Verifie que l'id est present pour un objet attaché a la BD et valide les
     * pré-requis.
     * @param c
     *            Computer
     * @throws ComputerException
     *             Si une exception de validation intervient
     */
    public static void validateComputerAndVerifyPresenceId(final Computer c) throws ComputerException {
        idIsRequiredForComputerUpdate(c.getId());
        dateIntroMinorThanDateDiscon(c.getIntroduced(), c.getDiscontinued());
    }

    /**
     * Verifie que le computer respecte les contraintes de validité et d'integrité
     * par rapport a la BD et logger si elles ne sont pas respectés.
     * @param computer
     *            Computer a valider
     * @throws ComputerException
     *             Si une erreur de validation intervient Si le nom de l'ordinateur
     *             n'est pas présent
     */
    private static void validationIntegrityModel(final Computer computer) throws ComputerException {
        if (factory == null) {
            factory = Validation.buildDefaultValidatorFactory();
        }
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<Computer>> violationSet = validator.validate(computer);
        if (!violationSet.isEmpty()) {
            for (ConstraintViolation<Computer> constraintViolation : violationSet) {
                LOGGER.error(constraintViolation.getMessage());
            }
            throw new ComputerException(ERREUR_DE_VALIDATION);
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
    private static void idIsRequiredForComputerUpdate(final Long id) throws ComputerNeedIdToBeUpdateException {
        if (id == null) {
            throw new ComputerNeedIdToBeUpdateException();
        }
    }

}
