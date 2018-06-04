package com.excilys.mapper;

import static org.junit.Assert.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.excilys.dto.ComputerDTO;
import com.excilys.exception.date.LocalDateExpectedException;
import com.excilys.model.Company;
import com.excilys.model.Computer;

@ExtendWith(MockitoExtension.class)
public class MapUtilTest {

    /**
     * Test la transformation d'une date valide (String) en LocalDate.
     * @throws LocalDateExpectedException
     *             Si le mapper n'arrive pas a parser la string en LocalDate.
     */
    @Test
    @DisplayName("Should return a LocalDate for YYYY-MM-JJ")
    public void parseAValidStringToLocalDate() throws LocalDateExpectedException {
        assertEquals(LocalDate.of(2010, Month.FEBRUARY, 03), MapUtil.parseStringToLocalDate("2010-02-03"));
    }

    /**
     * Verifie que si la date n'est pas valide (String) qu'une
     * LocalDateExpectedException est throw.
     */
    @Test
    @DisplayName("Should throw LocalDateExpectedException for a non valid String entry")
    public void parseANonValidStringToLocalDate() {
        assertThrows(LocalDateExpectedException.class, () -> MapUtil.parseStringToLocalDate("ee"));
        assertThrows(LocalDateExpectedException.class, () -> MapUtil.parseStringToLocalDate("02-01-2018"));
    }

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
        MapUtil mapUtil = new MapUtil() {
        };
    }

    /*
     * =============================================================================
     * = Test la transformation String List Java en String List Database
     * =============================================================================
     * =
     */

    /**
     * Prend une liste de String qui correspond a liste de long et retourne une
     * String qui correspond a la liste en base de données.
     */
    @Test
    @DisplayName("Should take a String[] that represent a list of Long and convert it to a String that represent a list in database")
    public void transformAListJavaToListDatabaseTest() {
        final String result = MapUtil.stringListIdToStringInListDatabase(new String[] {"1", "2", "3", "4" });
        assertEquals(result, "(1, 2, 3, 4)");
    }

    /**
     * Prend une liste de String qui correspond a liste de long et retourne une
     * String sans doublon qui correspond a la liste en base de données.
     */
    @Test
    @DisplayName("Should remove doublon during the transformation of the List")
    public void shouldRemoveDoublonDuringTransformation() {
        final String result = MapUtil.stringListIdToStringInListDatabase(new String[] {"1", "1", "1", "4" });
        assertEquals(result, "(1, 4)");
    }

    /**
     * Prend une liste de String qui correspond a liste de long et retourne une
     * String sans doublon qui correspond a la liste en base de données.
     */
    @Test
    @DisplayName("Should remove doublon during the transformation of the List")
    public void shouldThrowAnError() {
        assertThrows(NumberFormatException.class,
                () -> MapUtil.stringListIdToStringInListDatabase(new String[] {"a", "4" }));
    }

}
