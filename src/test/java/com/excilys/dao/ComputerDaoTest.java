package com.excilys.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Month;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.excilys.config.ServerConfiguration;
import com.excilys.exception.computer.ComputerNeedIdToBeUpdateException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.util.Pages;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(classes = ServerConfiguration.class)
public class ComputerDaoTest {

    @InjectMocks
    private ComputerDao mockComputerDao;

    @Autowired
    private ComputerDao computerDao;

    private Computer computer;

    /**
     * SetUp la classe de test en initialisant la computerDao et en preparant la
     * base de données.
     */
    @BeforeAll
    public void setUp() {
        computer = new Computer.Builder("aa").id(12L).build();
    }

    /*
     * Test find en base de donnée
     */

    /**
     * Verifie que la recherche d'un ordinateur existant fonctionne.
     */
    @Test
    @DisplayName("Should return the computer with ID 1L")
    public void findExistingComputerTest() {
        final Company company = new Company.Builder(1L).name("Apple Inc.").build();
        assertEquals(new Computer.Builder("MackBook Pro 15.4 inch").id(1L).introduced(null).discontinued(null)
                .company(company).build(), computerDao.find(1L).get());
    }

    /**
     * Verifie que la recherche d'un ordinateur qui n'existe pas déclenche une
     * NoSuchElementException.
     */
    @Test
    @DisplayName("Should throw NoSuchElementException for id 111L (ID not existing)")
    public void findNotExistingComputerTest() {
        assertThrows(NoSuchElementException.class, () -> computerDao.find(111L).get());
    }

    /**
     * Verifie que la recherche d'un ordinateur avec un ID impossible déclenche une
     * NoSuchElementException.
     */
    @Test
    @DisplayName("Should throw NoSuchElementException for id -1L (ID not possible)")
    public void findComputerWithInvalidIDTest() {
        assertThrows(NoSuchElementException.class, () -> computerDao.find(-1L).get());
    }

    /**
     * Verifie que findAll recupere tout les computers.
     */
    @Test
    @DisplayName("Should return true when find all computer")
    public void findAllcomputerTest() {
        assertTrue(computerDao.findAll().size() >= 11);
    }

    /**
     * Verifie que la recuperation par page fonctionne meme si on va trop loin
     * (retour a la derniere).
     */
    @Test
    @DisplayName("Should return the page requested by the user (1 and 2 are requested and found)")
    public void findcomputerByPageTest() {
        assertEquals(1, computerDao.findPerPage(1).getPageCourante());
        assertEquals(2, computerDao.findPerPage(2).getPageCourante());
        Pages<Computer> pages = computerDao.findPerPage(2);
        assertTrue(pages.getPageMax() >= 2);
        assertTrue(pages.getMaxComputers() >= 15);
        pages.setMaxComputers(23);
        assertTrue(pages.getMaxComputers() == 23);
    }

    /**
     * Verifie qu'on recupere toujours la derniere page possible.
     */
    @Test
    @DisplayName("Test pagination when asked 4 and 3 is the last should return 3")
    public void testAlwaysGettheLastPagePossible() {
        Pages<Computer> pages = new Pages<>(4);
        pages.setPageMax(30);
        assertEquals(3, pages.getPageCourante());
        pages.startResult();
    }

    /**
     * Verifie que la recuperation de page incorrecte affiche bien la premeire page.
     */
    @Test
    @DisplayName("Should return the page 1 when the pages requested are under 1")
    public void findComputerByPageNotPossibleTest() {
        assertEquals(1, computerDao.findPerPage(0).getPageCourante());
        assertEquals(1, computerDao.findPerPage(-1).getPageCourante());
    }

    /**
     * Verifie que si l'on demande la page 0 avec 20 resultat, on obtient la page 1
     * et 12 elements (nombre max d'element en BD).
     */
    @Test
    @DisplayName("Should return the numbers of computers requested for the page (Page 1 and more than 10 elements are return)")
    public void findPerPageWithNumberResultTest() {
        assertTrue(computerDao.findPerPage(0, 20).getEntities().size() > 10);
    }

    /**
     * Verifie que la recherche par page fonctionne.
     */
    @Test
    @DisplayName("Should return 2 for page 1 search requested")
    public void findPerPageReseach() {
        assertEquals(2, computerDao.findPerPage("try", 0, 20).getEntities().size());
        assertEquals(1, computerDao.findPerPage("try", 0, 1).getEntities().size());
        assertEquals(1, computerDao.findPerPage("try", 2, 1).getEntities().size());
        assertEquals(2, computerDao.findPerPage("try", 3, 20).getEntities().size());
    }

    /*
     * Test create en base de donnée
     */

    /**
     * Verifie que la creation d'un computer sans companie fonctionne.
     * @throws DateTruncationException
     *             Si une erreur de date apparait
     */
    @Test
    @DisplayName("Should create a computer when is correctly filled without company")
    public void createValidComputerWithoutCompanyTest() throws DateTruncationException {
        final Long id = computerDao.create(
                new Computer.Builder("PC_NAME").introduced(LocalDate.now()).discontinued(LocalDate.now()).build());
        assertNotEquals(-1L, id);
    }

    /**
     * Verifie que la création d'un ordinateur avec un ID de companie qui n'existe
     * pas, met la companie a null.
     * @throws DateTruncationException
     *             Si une erreur de date apparait
     */
    @Test
    @DisplayName("Should not create a computer when ID 111L for a company is used (111L not exist)")
    public void createValidComputerWithCompanyNotExistingTest() throws DateTruncationException {
        final Company company = new Company.Builder(111L).build();
        final Long id = computerDao.create(new Computer.Builder("PC_COMPANY_NOT").introduced(LocalDate.now())
                .discontinued(LocalDate.now()).company(company).build());
        assertEquals(Optional.empty(), computerDao.find(id));
    }

    /**
     * Verifie que la création d'un computer avec une companie qui existe
     * fonctionne.
     * @throws DateTruncationException
     *             Si une erreur de date apparait
     */
    @Test
    @DisplayName("Should create a computer when is correctly filled with a valid company")
    public void createValidComputerWithCompanyExistingTest() throws DateTruncationException {
        final Company company = new Company.Builder(1L).build();
        final Long id = computerDao.create(new Computer.Builder("PC_COMPANY").introduced(LocalDate.now())
                .discontinued(LocalDate.now()).company(company).build());
        assertNotNull(computerDao.find(id).get().getCompany());
    }

    /**
     * Verifie que la création d'un computer "leger" fonctionne.
     * @throws DateTruncationException
     *             Si une erreur de date apparait
     */
    @Test
    @DisplayName("Should create a light computer (with minimum required)")
    public void createValidLightComputerTest() throws DateTruncationException {
        final Long id = computerDao.create(new Computer.Builder(null).build());
        assertNotEquals(-1L, id);
    }

    /*
     * Test update
     */

    /**
     * Verifie que la mise a jour d'un ordinateur qui n'est pas en BD deleche soit
     * un NullPointerException car la presence de l'ID est gerer au niveau service.
     * Soit un optional vide car l'ID est set mais n'existe pas.
     * @throws ComputerNeedIdToBeUpdateException
     *             Si l'ID n'est pas présent
     * @throws DateTruncationException
     *             Si une erreur de date apparait
     */
    @Test
    @DisplayName("Should not update computer with no ID and throw ComputerNeedIdToBeUpdateException and should not update computer with ID 1111L (no present in database)")
    public void updateComputerTransientTest() throws ComputerNeedIdToBeUpdateException, DateTruncationException {
        final Computer computer = new Computer.Builder(null).build();
        System.out.println(computer);
        assertEquals(Optional.empty(), computerDao.update(computer));
        final Computer computer3 = new Computer.Builder("name").id(1111L).build();
        assertEquals(Optional.empty(), computerDao.update(computer3));
    }

    /**
     * Verifie que la mise a jour d'un ordinateur qui existe en BD fonctionne.
     * @throws ComputerNeedIdToBeUpdateException
     *             Si le computer n'a pas d'ID
     * @throws DateTruncationException
     *             Si une erreur de date apparait
     */
    @Test
    @DisplayName("Should update the computer 1L (existing in dtabase)")
    public void updateComputerExistingTest() throws ComputerNeedIdToBeUpdateException, DateTruncationException {
        final Computer computer = new Computer.Builder("rename").id(9L).introduced(LocalDate.now())
                .discontinued(LocalDate.of(2015, Month.FEBRUARY, 02)).company(new Company.Builder(1L).build()).build();
        computerDao.update(computer);
        final Optional<Computer> updated = computerDao.update(computer);
        assertEquals("rename", updated.get().getName());
        assertSame(9L, updated.get().getId());
        assertEquals(LocalDate.of(2015, Month.FEBRUARY, 02), updated.get().getDiscontinued());
    }

    /*
     * Test de suppression
     */

    /**
     * Test la suppresion d'un computer qui existe et d'un qui n'existe pas.
     */
    @Test
    @DisplayName("Should delete the computer 12L (existing in database) and not delete the computer 111L (not existing in database)")
    public void suppresionComputerExisting() {
        assertFalse(computerDao.delete(111L));
        assertTrue(computerDao.delete(12L));
    }

    /**
     * Test la suppresion d'une liste de computers.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs computer n'ont pas été supprimé.
     */
    @Test
    @DisplayName("Should delete computers with ID 20 and 21")
    public void deleteComputerByList() throws ComputerNotDeletedException {
        final String listeIdString = "(20,21)";
        assertTrue(computerDao.delete(listeIdString));
    }

    /**
     * Test la suppresion d'une liste de computers.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs computer n'ont pas été supprimé.
     */
    @Test
    @DisplayName("Should delete computers with ID 16 and not delete computer with id -1")
    public void deleteSomeComputerByList() {
        final String listeIdString = "(22,-1)";
        assertThrows(ComputerNotDeletedException.class, () -> computerDao.delete(listeIdString));
    }

    /**
     * Test la supression d'une liste de computer ou les ID n'existe pas.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs computer n'ont pas été supprimé.
     */
    @Test
    @DisplayName("Should not delete computers with ID -5 and -1")
    public void deleteComputerByListNotExisting() throws ComputerNotDeletedException {
        final String listeIdString = "(-5,-1)";
        assertFalse(computerDao.delete(listeIdString));
    }
}