package com.excilys.exception;

public class DateIntroShouldBeMinorthanDisconException extends ComputerException {

    private static final long serialVersionUID = 1L;
    private static final String MESSAGE = "La date %s (introduced) doit être plus vieille que la date %s (discontinued)";

    /**
     * Affiche le message d'exception expliquant la regle sur les dates d'un
     * computer.
     * @param introduced
     *            String de la LocalDate introduced
     * @param discontinued
     *            String de la LocalDate discontinued
     */
    public DateIntroShouldBeMinorthanDisconException(final String introduced, final String discontinued) {
        super(String.format(MESSAGE, introduced, discontinued));
    }
}
