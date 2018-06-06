package com.excilys.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.excilys.dto.ComputerDTO;
import com.excilys.model.Company;
import com.excilys.model.Computer;

public class MapUtilTest {

    /**
     * Test la transformation d'un computer en computerDTO.
     */
    @Test
    @DisplayName("Should create a computerDTO from a computer")
    public void transformAComputerToComputerDTO() {
        final Company company = new Company.Builder(1L).name("COMP_MAPPE").build();
        final Computer computer = new Computer.Builder("MAPPER_TEST").id(1L)
                .introduced(LocalDate.of(2010, Month.NOVEMBER, 25)).discontinued(LocalDate.parse("2013-02-15")).build();
        MapUtil.computerToComputerDTO(computer);
        computer.setCompany(company);
        ComputerDTO computerDTO = MapUtil.computerToComputerDTO(computer);
        assertSame(computerDTO.getId(), 1L);
        assertEquals(computerDTO.getCompanyName(), "COMP_MAPPE");
        assertEquals(new ComputerDTO(null), MapUtil.computerToComputerDTO(null));
        new MapUtil() {
        };
    }

    /**
     * Test la transformation d'un computerDTO en computer
     */
    @Test
    @DisplayName("Should create a computer from a computerDTO")
    public void transformAComputerDTOToComputer() {
        final Company company = new Company.Builder(1L).name("COMP_MAPPE").build();
        final Computer computer = new Computer.Builder("MAPPER_TEST").id(1L)
                .introduced(LocalDate.of(2010, Month.NOVEMBER, 25)).discontinued(LocalDate.parse("2013-02-15")).build();
        MapUtil.computerToComputerDTO(computer);
        computer.setCompany(company);
        ComputerDTO computerDTO = new ComputerDTO(computer);
        Computer computerResult = MapUtil.computerDTOToComputer(computerDTO);
        assertSame(computerResult.getId(), 1L);
        assertEquals(computerResult.getCompany().getName(), "COMP_MAPPE");
        new MapUtil() {
        };
        computer.setCompany(null);
        computerDTO = new ComputerDTO(computer);
        computerResult = MapUtil.computerDTOToComputer(computerDTO);
        assertSame(computerResult.getCompany(),null);
    }
}
