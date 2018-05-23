package com.excilys.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.excilys.config.ServerConfiguration;
import com.excilys.dao.CompanyDao;
import com.excilys.dao.ComputerDao;
import com.excilys.exception.ComputerException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNameNotPresentException;
import com.excilys.exception.computer.ComputerNeedIdToBeUpdateException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.computer.ComputerNotUpdatedException;
import com.excilys.exception.computer.DateIntroShouldBeMinorthanDisconException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.util.Pages;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(classes = ServerConfiguration.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ServiceCdbTest {

    @Autowired
    DataSource dataSource;

    @Mock
    CompanyDao companyDao;

    @Mock
    ComputerDao computerDao;

    @InjectMocks
    private ServiceComputer mockServiceComputer;

    @InjectMocks
    private ServiceCompany mockServiceCompany;

    @Mock
    private ServiceCompany serviceCompany;

    @Autowired
    private ServiceCompany serviceCompanyReal;

    @Autowired
    private ServiceComputer serviceComputer;

    private Computer computer;

    private Computer computer2;

    private Company company;

    private Optional<Computer> computerOptional;

    /**
     * Set up la classe de test en initialisant la couche de service.
     */
    @BeforeAll
    public void setUp() {
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
        assertEquals(computer2, mockServiceComputer.getComputerDaoDetails(1L));
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
        assertThrows(ComputerNotFoundException.class, () -> mockServiceComputer.getComputerDaoDetails(1L));
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
        assertEquals(collectionsComputer, mockServiceComputer.getAll());
        assertEquals(collectionsCompanies, mockServiceCompany.getAll());
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
        assertEquals(pages, mockServiceComputer.findByPage(0));
        Mockito.verify(computerDao).findPerPage(0);

        Mockito.when(computerDao.findPerPage(1)).thenReturn(pages);
        assertEquals(pages, mockServiceComputer.findByPage(1));
        Mockito.verify(computerDao).findPerPage(1);

        Mockito.when(computerDao.findPerPage(2)).thenReturn(pagesDeux);
        assertEquals(pagesDeux, mockServiceComputer.findByPage(2));
        Mockito.verify(computerDao).findPerPage(2);

        Mockito.when(computerDao.findPerPage(3)).thenReturn(pagesDeux);
        assertEquals(pagesDeux, mockServiceComputer.findByPage(3));
        Mockito.verify(computerDao).findPerPage(3);

        pages = new Pages<>(0);
        pages.getEntities().addAll(computers.subList(0, 2));
        Mockito.when(computerDao.findPerPage(3, 2)).thenReturn(pages);
        assertEquals(pages, mockServiceComputer.findByPage(3, 2));
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
        assertEquals(pages, mockServiceCompany.findByPage(0));
        Mockito.verify(companyDao).findPerPage(0);

        Mockito.when(companyDao.findPerPage(1)).thenReturn(pages);
        assertEquals(pages, mockServiceCompany.findByPage(1));
        Mockito.verify(companyDao).findPerPage(1);

        Mockito.when(companyDao.findPerPage(2)).thenReturn(pagesDeux);
        assertEquals(pagesDeux, mockServiceCompany.findByPage(2));
        Mockito.verify(companyDao).findPerPage(2);

        Mockito.when(companyDao.findPerPage(3)).thenReturn(pagesDeux);
        assertEquals(pagesDeux, mockServiceCompany.findByPage(3));
        Mockito.verify(companyDao).findPerPage(3);
    }

    /*
     * Test create computers
     */

    /**
     * Demande a la DAO de crée un computer apres verification de sa validité.
     * @throws CompanyNotFoundException
     *             La companie n'existe pas
     * @throws ComputerException
     *             Si une regle de validation sur computer echoue
     * @throws DateTruncationException
     *             Si une erreur de date apparait
     */
    @Test
    @DisplayName("Test to create a valid computer")
    public void createValidComputerTest() throws CompanyNotFoundException, ComputerException, DateTruncationException {
        Computer computer = new Computer.Builder("test").id(8L).build();
        Mockito.when(computerDao.create(computer)).thenReturn(1L);
        assertSame(1L, mockServiceComputer.createComputer(computer));
        computer = new Computer.Builder("e").introduced(LocalDate.parse("2014-12-30"))
                .discontinued(LocalDate.parse("2015-01-01")).build();
        Mockito.when(computerDao.create(computer)).thenReturn(2L);
        assertSame(2L, mockServiceComputer.createComputer(computer));
        Mockito.verify(computerDao).create(computer);
    }

    /**
     * Essaye de crée un computer sans nom ce qui genere une exception.
     */
    @Test
    @DisplayName("Test throw a ComputerNameNotPresentException because name is required")
    public void createComputerWithoutNameTest() {
        final Computer computer = new Computer.Builder(null).build();
        assertThrows(ComputerNameNotPresentException.class, () -> mockServiceComputer.createComputer(computer));
        computer.setName("");
        assertThrows(ComputerNameNotPresentException.class, () -> mockServiceComputer.createComputer(computer));
    }

    /**
     * Essaye de creer un computer avec une date discontinued < a la date
     * introduced.
     * @throws CompanyNotFoundException
     *             La company n'existe pas
     * @throws ComputerException
     *             Si une regle de validation sur computer échoue
     */
    @Test
    @DisplayName("Test dateIntroduced <= dateDiscontinued is false => Don't create computer ")
    public void createComputerWithInvalideDateTest() throws CompanyNotFoundException, ComputerException {
        final Computer computer = new Computer.Builder("a").introduced(LocalDate.parse("2016-01-01"))
                .discontinued(LocalDate.parse("2015-12-30")).build();
        assertThrows(DateIntroShouldBeMinorthanDisconException.class,
                () -> mockServiceComputer.createComputer(computer));
    }

    /**
     * Retourne une CompanyNotFoundException pour des entrées invalides (-1) ou des
     * entrées non existente (50L).
     */
    @Test
    @DisplayName("Should throw a CompanyNotFoundException for -1 (invalid) and 50 (not exist)")
    public void createComputerWithInvalidCompany() {
        final Company company = new Company.Builder(-1L).build();
        final Computer computer = new Computer.Builder("a").company(company).build();
        assertThrows(CompanyNotFoundException.class, () -> mockServiceComputer.createComputer(computer));
        company.setId(50L);
        assertThrows(CompanyNotFoundException.class, () -> mockServiceComputer.createComputer(computer));
    }

    /*
     * =============================================================================
     * == Update computers
     * =============================================================================
     * ==
     */

    /**
     * Essaye de mettre a jour un computer qui n'a pas de nom.
     */
    @Test
    @DisplayName("Test updating a computer without name because name is required")
    public void updateComputerWithoutNameTest() {
        final Computer computer = new Computer.Builder(null).build();
        assertThrows(ComputerNeedIdToBeUpdateException.class, () -> mockServiceComputer.updateComputer(computer));
        computer.setName("");
        computer.setId(1L);
        assertThrows(ComputerNameNotPresentException.class, () -> mockServiceComputer.updateComputer(computer));
    }

    /**
     * Essaye de mettre a jour un computer avec une date discontinued < a la date
     * introduced.
     * @throws ComputerException
     *             Si une regle de validation sur computer existe
     */
    @Test
    @DisplayName("Test dateIntroduced <= dateDiscontinued is false => Don't update computer ")
    public void updateComputerWithInvalideDateTest() throws ComputerException {
        final Computer computer = new Computer.Builder("a").id(9L).introduced(LocalDate.parse("2016-01-01"))
                .discontinued(LocalDate.parse("2015-12-30")).build();
        assertThrows(DateIntroShouldBeMinorthanDisconException.class,
                () -> mockServiceComputer.updateComputer(computer));
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
        assertThrows(ComputerNeedIdToBeUpdateException.class, () -> mockServiceComputer.updateComputer(computer));
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
        assertThrows(ComputerNotUpdatedException.class, () -> mockServiceComputer.updateComputer(computer));
    }

    /**
     * Demande a la DAO de mettre a jour un ordinateur valide.
     * @throws ComputerException
     *             Si une regle de validation echoue sur un computer
     * @throws DateTruncationException
     *             Si une erreur de date apparait
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    @Test
    @DisplayName("Test updating a valid computer")
    public void updateComputerTest() throws ComputerException, DateTruncationException, CompanyNotFoundException {
        Optional<Computer> computer = Optional.ofNullable(new Computer.Builder("test").id(8L).build());
        Mockito.when(computerDao.update(computer.get())).thenReturn(computer);
        assertEquals(computer.get(), mockServiceComputer.updateComputer(computer.get()));
        computer = Optional.ofNullable(new Computer.Builder("test").id(8L).discontinued(null)
                .introduced(LocalDate.parse("2015-12-30")).build());
        Mockito.when(computerDao.update(computer.get())).thenReturn(computer);
        assertEquals(computer.get(), mockServiceComputer.updateComputer(computer.get()));
    }

    /**
     * La mise a jour d'un computer possedant un companie invalide ou n'existant
     * doit produire une exception.
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     * @throws DateTruncationException
     *             Si la date est avant 1970
     * @throws ComputerException
     *             Si une regle de validation sur computer n'est pas verifiée
     */
    @Test
    @DisplayName("Should throw CompanyNotFoundException for company -1 and 50")
    public void updateComputerWhenCompanyNotExist()
            throws ComputerException, DateTruncationException, CompanyNotFoundException {
        final Computer computer = new Computer.Builder("ee").company(new Company.Builder(-1L).build()).build();
        assertThrows(CompanyNotFoundException.class, () -> serviceComputer.updateComputer(computer));
        computer.getCompany().setId(50L);
        assertThrows(CompanyNotFoundException.class, () -> serviceComputer.updateComputer(computer));
        computer.getCompany().setId(-1L);
        assertThrows(CompanyNotFoundException.class, () -> mockServiceComputer.updateComputer(computer));
    }

    /*
     * =============================================================================
     * ============== Delete Computers
     * =============================================================================
     * ==============
     */

    /**
     * Demande a la DAO de supprimer un ordinateur qui n'existe pas.
     */
    @Test
    @DisplayName("Test delete a computer that not exist")
    public void deleteComputerNotExistingTest() {
        Mockito.when(computerDao.delete(50L)).thenReturn(false);
        assertFalse(mockServiceComputer.deleteOne(50L));
        Mockito.verify(computerDao).delete(50L);
    }

    /**
     * Demaned a la DAO de supprimer un ordinateur qui n'a pas d'ID ou un ID
     * incorrecte.
     */
    @Test
    @DisplayName("Test delete a computer with no ID or invalid ID")
    public void deleteComputerWithInvalidIdTest() {
        final Computer computer = new Computer.Builder("").build();
        Mockito.when(computerDao.delete(computer.getId())).thenReturn(false);
        assertFalse(mockServiceComputer.deleteOne(computer.getId()));
        Mockito.verify(computerDao).delete(computer.getId());
        Mockito.when(computerDao.delete(-1L)).thenReturn(false);
        assertFalse(mockServiceComputer.deleteOne(-1L));
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

    /**
     * Demande a la DAO de supprimer un ordinateur présent en base de données.
     */
    @Test
    @DisplayName("Test delete an existing company")
    public void deleteCompanytest() {
        Mockito.when(companyDao.delete(1L)).thenReturn(true);
        assertTrue(companyDao.delete(1L));
        Mockito.verify(companyDao).delete(1L);
    }

    /**
     * Demande a la DAO de supprimer un ordinateur présent en base de données.
     */
    @Test
    @DisplayName("Test delete an existing company")
    public void deleteCompanyThatNotExistTest() {
        Mockito.when(companyDao.delete(1L)).thenReturn(false);
        assertFalse(companyDao.delete(1L));
        Mockito.verify(companyDao).delete(1L);
    }

    /**
     * Verifie que la suppresion d'une companie supprime bien tous ses computers.
     * @throws SQLException
     *             SQLException
     */
    @Test
    @DisplayName("Test delete an existing company with his computers")
    public void deleteCompanyAndHisComputers() throws SQLException {
        assertTrue(serviceComputer.findByPagesComputer("name", 0, 10).getEntities().size() >= 2);
        serviceComputer.deleteOne(32L);
        assertEquals(2, serviceComputer.findByPagesComputer("name", 0, 10).getEntities().size());
    }

    /**
     * Verifie que la suppresion d'une liste de computer coté DAO fonctionne.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs computer n'ont pas pu être supprimé
     */
    @Test
    @DisplayName("Test delete a list of computers ")
    public void deleteListComputerTest() throws ComputerNotDeletedException {
        Mockito.when(computerDao.delete("(4,2)")).thenReturn(true);
        assertTrue(mockServiceComputer.deleteComputer("(4,2)"));
        Mockito.verify(computerDao).delete("(4,2)");
    }

    /**
     * Demande a la DAO de supprimer une liste de computer non valide.
     * @throws ComputerNotDeletedException
     *             Computer Not deleted exception
     */
    @Test
    @DisplayName("Test delete a list of computers ")
    public void deleteListComputerNotValidTest() throws ComputerNotDeletedException {
        Mockito.when(computerDao.delete("(-4,1222)")).thenReturn(false);
        assertFalse(mockServiceComputer.deleteComputer("(-4,1222)"));
        Mockito.verify(computerDao).delete("(-4,1222)");
    }

    /**
     * Demande a la DAO de supprimer une company.
     */
    @Test
    @DisplayName("Should delete company id ? and computers (?,?,?")
    public void deleteOneCompany() {
        assertEquals(3, serviceComputer.findByPagesComputer("cascade", 1).getEntities().size());
        serviceCompanyReal.deleteOne(38L);
        assertEquals(0, serviceComputer.findByPagesComputer("cascade", 1).getEntities().size());
    }
}
