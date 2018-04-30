package com.excilys.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.excilys.dao.CompanyDao;
import com.excilys.dao.ComputerDao;
import com.excilys.exception.CompanyNotFoundException;
import com.excilys.exception.ComputerNameNotPresentException;
import com.excilys.exception.ComputerNeedIdToBeUpdateException;
import com.excilys.exception.ComputerNotFoundException;
import com.excilys.exception.DaoNotInitializeException;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.util.Pages;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ServiceCdbTest {

    @Mock
    CompanyDao companyDao;

    @Mock
    ComputerDao computerDao;

    @InjectMocks
    private ServiceCdb servicecdb;

    private Computer computer;

    private Computer computer2;

    private Company company;

    private Optional<Computer> computerOptional;

    /**
     * Set up la classe de test en initialisant la couche de service.
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas correctement initialisée
     * @throws SQLException
     *             Si une erreur SQL intervient
     */
    @BeforeAll
    public void setUp() throws SQLException, DaoNotInitializeException {
        MockitoAnnotations.initMocks(this);
        company = new Company.Builder(1L).name("COMP_NAME").build();
        computer = new Computer.Builder("PC_NAME").introduced(LocalDate.now()).discontinued(LocalDate.now())
                .company(company).build();
        computerOptional = Optional.of(computer);
        // creation d'un computer d'instance differente pour tester l'equals.
        computer2 = new Computer.Builder("PC_NAME").introduced(LocalDate.now()).discontinued(LocalDate.now())
                .company(new Company.Builder(1L).name("COMP_NAME").build()).build();

    }

    /*
     * Test find computers and companies
     */

    /**
     * Recupere les infos d'un ordinateur.
     * @throws ComputerNotFoundException
     *             Si on ne trouve pas de computer
     */
    @Test
    @DisplayName("Test to get computer details for an existing")
    public void getComputerDetailsTest() throws ComputerNotFoundException {
        Mockito.when(computerDao.find(1L)).thenReturn(computerOptional);
        assertEquals(computer2, servicecdb.getComputerDaoDetails(1L));
        Mockito.verify(computerDao).find(1L);
    }

    /**
     * Throw une exception car on essaye de recuperer des infos sur un ordinateur
     * qui n'existe pas.
     * @throws ComputerNotFoundException
     *             Si on ne trouve pas de computer
     */
    @Test
    @DisplayName("Test getting information for a computer that does not exist")
    public void getComputerDetailsWhenNotExistTest() throws ComputerNotFoundException {
        Mockito.when(computerDao.find(1L)).thenReturn(Optional.empty());
        assertThrows(ComputerNotFoundException.class, () -> servicecdb.getComputerDaoDetails(1L));
        Mockito.verify(computerDao).find(1L);
    }

    /**
     * Recupere tout les ordinateurs et toutes les companies en base.
     */
    @Test
    @DisplayName("Test to get all computers and all companies in database")
    public void getAllComputersAndCompaniesTest() {
        final List<Computer> collectionsComputer = Collections.nCopies(42, computer);
        final List<Company> collectionsCompanies = Collections.nCopies(42, company);
        Mockito.when(computerDao.findAll()).thenReturn(collectionsComputer);
        Mockito.when(companyDao.findAll()).thenReturn(collectionsCompanies);
        assertEquals(collectionsComputer, servicecdb.getListComputers());
        assertEquals(collectionsCompanies, servicecdb.getListCompanies());
        Mockito.verify(computerDao).findAll();
        Mockito.verify(companyDao).findAll();
    }

    /**
     * Demande a la DAO de recuperer par page les computer.
     */
    @Test
    @DisplayName("Test la recuperation par page de computer")
    public void getComputerByPageTest() {
        List<Computer> computers = Collections.nCopies(39, computer);
        Pages<Computer> pages = new Pages<>(0);
        pages.getEntities().addAll(computers.subList(0, 19));
        Pages<Computer> pagesDeux = new Pages<>(2);
        pages.getEntities().addAll(computers.subList(20, 39));
        Mockito.when(computerDao.findPerPage(0)).thenReturn(pages);
        assertEquals(pages, servicecdb.findByPagesComputer(0));
        Mockito.verify(computerDao).findPerPage(0);

        Mockito.when(computerDao.findPerPage(1)).thenReturn(pages);
        assertEquals(pages, servicecdb.findByPagesComputer(1));
        Mockito.verify(computerDao).findPerPage(1);

        Mockito.when(computerDao.findPerPage(2)).thenReturn(pagesDeux);
        assertEquals(pagesDeux, servicecdb.findByPagesComputer(2));
        Mockito.verify(computerDao).findPerPage(2);

        Mockito.when(computerDao.findPerPage(3)).thenReturn(pagesDeux);
        assertEquals(pagesDeux, servicecdb.findByPagesComputer(3));
        Mockito.verify(computerDao).findPerPage(3);

        pages = new Pages<>(0);
        pages.getEntities().addAll(computers.subList(0, 2));
        Mockito.when(computerDao.findPerPage(3, 2)).thenReturn(pages);
        assertEquals(pages, servicecdb.findByPagesComputer(3, 2));
        Mockito.verify(computerDao).findPerPage(0);
    }

    /**
     * Demande a la DAO de recuperer par page les companys.
     */
    @Test
    @DisplayName("Test la recuperation par page de company")
    public void getCompanyByPageTest() {
        List<Company> companies = Collections.nCopies(39, company);
        Pages<Company> pages = new Pages<>(0);
        pages.getEntities().addAll(companies.subList(0, 19));
        Pages<Company> pagesDeux = new Pages<>(2);
        pages.getEntities().addAll(companies.subList(20, 39));
        Mockito.when(companyDao.findPerPage(0)).thenReturn(pages);
        assertEquals(pages, servicecdb.findByPagesCompany(0));
        Mockito.verify(companyDao).findPerPage(0);

        Mockito.when(companyDao.findPerPage(1)).thenReturn(pages);
        assertEquals(pages, servicecdb.findByPagesCompany(1));
        Mockito.verify(companyDao).findPerPage(1);

        Mockito.when(companyDao.findPerPage(2)).thenReturn(pagesDeux);
        assertEquals(pagesDeux, servicecdb.findByPagesCompany(2));
        Mockito.verify(companyDao).findPerPage(2);

        Mockito.when(companyDao.findPerPage(3)).thenReturn(pagesDeux);
        assertEquals(pagesDeux, servicecdb.findByPagesCompany(3));
        Mockito.verify(companyDao).findPerPage(3);
    }
    /*
     * Test create computers
     */

    /**
     * Demande a la DAO de crée un computer apres verification de sa validité.
     * @throws ComputerNameNotPresentException
     *             Si le nom du computer n'est pas présent
     * @throws CompanyNotFoundException La companie n'existe pas
     */
    @Test
    @DisplayName("Test to create a valid computer")
    public void createValidComputerTest() throws ComputerNameNotPresentException, CompanyNotFoundException {
        Mockito.when(computerDao.create(computer)).thenReturn(1L);
        Mockito.when(companyDao.find(1L)).thenReturn(Optional.of(company));
        assertSame(1L, servicecdb.createComputer(computer));
        final Computer computer = new Computer.Builder("e").introduced(LocalDate.parse("2014-12-30"))
                .discontinued(LocalDate.parse("2015-01-01")).build();
        Mockito.when(computerDao.create(computer)).thenReturn(2L);
        assertSame(2L, servicecdb.createComputer(computer));
        Mockito.verify(computerDao, Mockito.times(2)).create(computer);
    }

    /**
     * Essaye de crée un computer sans nom ce qui genere une exception.
     */
    @Test
    @DisplayName("Test throw a ComputerNameNotPresentException because name is required")
    public void createComputerWithoutNameTest() {
        final Computer computer = new Computer.Builder(null).build();
        assertThrows(ComputerNameNotPresentException.class, () -> servicecdb.createComputer(computer));
        computer.setName("");
        assertThrows(ComputerNameNotPresentException.class, () -> servicecdb.createComputer(computer));
    }

    /**
     * Essaye de creer un computer avec une date discontinued < a la date
     * introduced.
     * @throws ComputerNameNotPresentException
     *             Si le nom du computer n'est pas renseignée
     * @throws CompanyNotFoundException La company n'existe pas
     */
    @Test
    @DisplayName("Test dateIntroduced <= dateDiscontinued is false => Don't create computer ")
    public void createComputerWithInvalideDateTest() throws ComputerNameNotPresentException, CompanyNotFoundException {
        final Computer computer = new Computer.Builder("a").introduced(LocalDate.parse("2016-01-01"))
                .discontinued(LocalDate.parse("2015-12-30")).build();
        assertSame(-1L, servicecdb.createComputer(computer));
    }

    /*
     * Update computers
     */

    /**
     * Essaye de mettre a jour un computer qui n'a pas de nom.
     */
    @Test
    @DisplayName("Test updating a computer without name because name is required")
    public void updateComputerWithoutNameTest() {
        final Computer computer = new Computer.Builder(null).build();
        assertThrows(ComputerNameNotPresentException.class, () -> servicecdb.updateComputer(computer));
        computer.setName("");
        assertThrows(ComputerNameNotPresentException.class, () -> servicecdb.updateComputer(computer));
    }

    /**
     * Essaye de mettre a jour un computer avec une date discontinued < a la date
     * introduced.
     * @throws ComputerNameNotPresentException
     *             Si le nom du computer n'est pas renseignée
     * @throws ComputerNeedIdToBeUpdateException
     *             Si l'ID n'est pas renseignée.
     */
    @Test
    @DisplayName("Test dateIntroduced <= dateDiscontinued is false => Don't update computer ")
    public void updateComputerWithInvalideDateTest()
            throws ComputerNameNotPresentException, ComputerNeedIdToBeUpdateException {
        final Computer computer = new Computer.Builder("a").introduced(LocalDate.parse("2016-01-01"))
                .discontinued(LocalDate.parse("2015-12-30")).build();
        assertNull(servicecdb.updateComputer(computer));
    }

    /**
     * Essaye de mettre a jour un computer avec un ID non renseignée.
     * @throws ComputerNameNotPresentException
     *             Si le nom du computer n'est pas renseignée
     * @throws ComputerNeedIdToBeUpdateException
     *             Si l'ID du computer n'est pas renseignée
     */
    @Test
    @DisplayName("Test update computer with no existing id ")
    public void updateComputerWithNoIdTest() throws ComputerNameNotPresentException, ComputerNeedIdToBeUpdateException {
        final Computer computer = new Computer.Builder("a").introduced(LocalDate.parse("2016-01-01"))
                .discontinued(LocalDate.parse("2016-12-30")).build();
        Mockito.when(computerDao.update(computer)).thenCallRealMethod();
        assertThrows(ComputerNeedIdToBeUpdateException.class, () -> servicecdb.updateComputer(computer));
        Mockito.verify(computerDao).update(computer);
    }

    /**
     * Essaye de mettre a jour un computer avec un ID non valide.
     * @throws ComputerNameNotPresentException
     *             Si le nom du computer n'est pas renseignée
     * @throws ComputerNeedIdToBeUpdateException
     *             Si l'ID du computer n'est pas renseignée
     */
    @Test
    @DisplayName("Test update computer with invalid id")
    public void updateComputerWithInvalidIdTest()
            throws ComputerNeedIdToBeUpdateException, ComputerNameNotPresentException {
        final Computer computer = new Computer.Builder("a").introduced(LocalDate.parse("2016-01-01"))
                .discontinued(LocalDate.parse("2016-12-30")).id(-1L).build();
        Mockito.when(computerDao.update(computer)).thenReturn(null);
        assertNull(servicecdb.updateComputer(computer));
        Mockito.verify(computerDao).update(computer);
    }

    /**
     * Demande a la DAO de mettre a jour un ordianteur.
     * @throws ComputerNeedIdToBeUpdateException
     *             Si l'ID du computer n'est pas renseignée
     * @throws ComputerNameNotPresentException
     *             Si le nom du computer n'est pas renseignée
     */
    @Test
    @DisplayName("Test updating a valid computer")
    public void updateComputerTest() throws ComputerNeedIdToBeUpdateException, ComputerNameNotPresentException {
        final Computer computer = new Computer.Builder("test").id(8L).build();
        Mockito.when(computerDao.update(computer)).thenReturn(computer);
        assertEquals(computer, servicecdb.updateComputer(computer));
    }

    /*
     * Delete Computers
     */

    /**
     * Demande a la DAO de supprimer un ordinateur qui n'existe pas.
     */
    @Test
    @DisplayName("Test delete a computer that not exist")
    public void deleteComputerNotExistingTest() {
        Mockito.when(computerDao.delete(50L)).thenReturn(false);
        assertFalse(servicecdb.deleteComputer(50L));
        Mockito.verify(computerDao).delete(50L);
    }

    /**
     * Demaned a la DAO de supprimer un ordinateur qui n'a pas d'ID ou un ID
     * incorrecte.
     */
    @Test
    @DisplayName("Test delete a computer with no ID or invalid ID")
    public void deleteComputerWithInvalidIdTest() {
        Mockito.when(computerDao.delete(null)).thenReturn(false);
        assertFalse(servicecdb.deleteComputer(null));
        Mockito.verify(computerDao).delete(null);
        Mockito.when(computerDao.delete(-1L)).thenReturn(false);
        assertFalse(servicecdb.deleteComputer(-1L));
        Mockito.verify(computerDao).delete(-1L);
    }

    /**
     * Demande a la DAO de supprimer un ordinateur present en base.
     */
    @Test
    @DisplayName("Test delete an existing computer")
    public void deleteComputerTest() {
        Mockito.when(computerDao.delete(7L)).thenReturn(true);
        assertTrue(computerDao.delete(7L));
        Mockito.verify(computerDao).delete(7L);
    }
}
