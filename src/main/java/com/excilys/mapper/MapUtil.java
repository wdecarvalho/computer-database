package com.excilys.mapper;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.excilys.dto.ComputerDTO;
import com.excilys.exception.date.LocalDateExpectedException;
import com.excilys.model.Company;
import com.excilys.model.Computer;

public abstract class MapUtil {

    /**
     * Convertit un Timestamp en une LocalDate.
     * @param time
     *            Timestamp a convertir
     * @return LocalDate
     */
    public static LocalDate convertTimeStampToLocal(final Timestamp time) {
        if (time != null) {
            return time.toLocalDateTime().toLocalDate();
        }
        return null;

    }

    /**
     * Convertit une LocalDate en un Timestamp.
     * @param date
     *            LocalDate a convertir
     * @return TimeStamp
     */
    public static Timestamp convertLocalDateToTimeStamp(final LocalDate date) {
        if (date != null) {
            return Timestamp.valueOf(date.atStartOfDay());
        }
        return null;
    }

    /**
     * Converti une String en LocalDate.
     * @param date
     *            Date sous forme de String
     * @return LocalDate
     * @throws LocalDateExpectedException
     *             Si la string n'etait pas une date valide.
     */
    public static LocalDate parseStringToLocalDate(final String date) throws LocalDateExpectedException {
        try {
            return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new LocalDateExpectedException(date);
        }

    }

    /**
     * Creer un DTO a partir d'une entité.
     * @param computer
     *            Computer a transformé.
     * @return ComputerDTO
     */
    public static ComputerDTO computerToComputerDTO(Computer computer) {
        return new ComputerDTO(computer);
    }

    /**
     * Creer un computer a partir d'un computerDTO.
     * @param computerDTO
     *            computerDTO recu
     * @return Computer
     */
    public static Computer computerDTOToComputer(ComputerDTO computerDTO) {
        final Company company;
        if (computerDTO.getCompanyID() == null) {
            company = null;
        } else {
            company = new Company.Builder(computerDTO.getCompanyID()).name(computerDTO.getCompanyName()).build();
        }
        return new Computer.Builder(computerDTO.getName()).id(computerDTO.getId()).introduced(computerDTO.getIntroDate())
                .discontinued(computerDTO.getDisconDate()).company(company).build();
    }

    /**
     * Prend une liste de String d'ID, transforme en set pour eviter les doublons,
     * verifie que ce sont des nombres et creer une liste d'id pour la base de
     * donnée.
     * @param idsComputer
     *            Liste d'ID de computer a supprimer
     * @return Liste d'id pour base de données
     */
    public static String stringListIdToStringInListDatabase(String[] idsComputer) {
        Set<Long> computerToDelete = Arrays.stream(idsComputer).map(l -> Long.valueOf(l)).collect(Collectors.toSet());
        return computerToDelete.toString().replace("[", "(").replace("]", ")");
    }

}
