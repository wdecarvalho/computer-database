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
        return new Computer.Builder(computerDTO.getName()).id(computerDTO.getId())
                .introduced(computerDTO.getIntroDate()).discontinued(computerDTO.getDisconDate()).company(company)
                .build();
    }

}
