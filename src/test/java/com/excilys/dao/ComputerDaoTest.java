package com.excilys.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
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

import com.excilys.exception.ComputerNeedIdToBeUpdateException;
import com.excilys.exception.DaoNotInitializeException;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.mysql.cj.protocol.Resultset;

@ExtendWith(MockitoExtension.class) // RunWith
@TestInstance(Lifecycle.PER_CLASS)
public class ComputerDaoTest {

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement ps;

    @Mock
    private Resultset rs;

    @InjectMocks
    private ComputerDao mockComputerDao;

    private ComputerDao computerDao;

    private Computer computer;

    /**
     * SetUp la classe de test en initialisant la computerDao et en preparant la
     * base de données.
     * @throws SQLException
     *             Si une erreur SQL apparait
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas initialisé
     * @throws FileNotFoundException
     *             Si le fichier de script est manquant
     */
    @BeforeAll
    public void setUp() throws SQLException, DaoNotInitializeException, FileNotFoundException {
        MockitoAnnotations.initMocks(this);
        computerDao = (ComputerDao) DaoFactory.getInstance().getDao(DaoType.COMPUTER_DAO);
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
     * Lorsque une SQLException intervient un optional vide doit etre retournée.
     * @throws SQLException
     *             SQLException
     */
    @Test
    @DisplayName("Should return an empty optional for find method when SQLException occures")
    public void findComputerWhenSQLExceptionIsCatched() throws SQLException {
        scenariseConnectionAndPreparedStatement(false);
        assertEquals(Optional.empty(), mockComputerDao.find(1L));
        verifyConnectionAndPreparedStatement(false);
    }

    /**
     * Lorsque une SQLException intervient une liste vide doit être retournée.
     * @throws SQLException
     *             SQLException
     */
    @Test
    @DisplayName("Should return an empty collection for findAll method when SQLException occures")
    public void findAllComputersWhenSQLExceptionIsCatched() throws SQLException {
        scenariseConnectionAndPreparedStatement(false);
        assertEquals(0, mockComputerDao.findAll().size());
        verifyConnectionAndPreparedStatement(false);
    }

    /**
     * Lorsque une SQLException intervient une page sans elements doit être
     * retournée.
     * @throws SQLException
     *             SQLException
     */
    @Test
    @DisplayName("Should return an empty page for findPerPage method when SQLException occures")
    public void findPerPageComputerWhenSQLExceptionIsCatched() throws SQLException {
        scenariseConnectionAndPreparedStatement(false);
        assertSame(0, mockComputerDao.findPerPage(1).getEntities().size());
        verifyConnectionAndPreparedStatement(false);
    }

    /*
     * Test create en base de donnée
     */

    /**
     * Verifie que la creation d'un computer sans companie fonctionne.
     */
    @Test
    @DisplayName("Should create a computer when is correctly filled without company")
    public void createValidComputerWithoutCompanyTest() {
        final Long id = computerDao.create(
                new Computer.Builder("PC_NAME").introduced(LocalDate.now()).discontinued(LocalDate.now()).build());
        assertNotEquals(-1L, id);
    }

    /**
     * Verifie que la création d'un ordinateur avec un ID de companie qui n'existe
     * pas, met la companie a null.
     */
    @Test
    @DisplayName("Should not create a computer when ID 111L for a company is used (111L not exist)")
    public void createValidComputerWithCompanyNotExistingTest() {
        final Company company = new Company.Builder(111L).build();
        final Long id = computerDao.create(new Computer.Builder("PC_COMPANY_NOT").introduced(LocalDate.now())
                .discontinued(LocalDate.now()).company(company).build());
        assertEquals(Optional.empty(), computerDao.find(id));
    }

    /**
     * Verifie que la création d'un computer avec une companie qui existe
     * fonctionne.
     */
    @Test
    @DisplayName("Should create a computer when is correctly filled with a valid company")
    public void createValidComputerWithCompanyExistingTest() {
        final Company company = new Company.Builder(1L).build();
        final Long id = computerDao.create(new Computer.Builder("PC_COMPANY").introduced(LocalDate.now())
                .discontinued(LocalDate.now()).company(company).build());
        assertNotNull(computerDao.find(id).get().getCompany());
    }

    /**
     * Verifie que la création d'un computer "leger" fonctionne.
     */
    @Test
    @DisplayName("Should create a light computer (with minimum required)")
    public void createValidLightComputerTest() {
        final Long id = computerDao.create(new Computer.Builder(null).build());
        assertNotEquals(-1L, id);
    }

    /*
     * Test update
     */

    /**
     * Verifie que la mise a jour d'un ordinateur qui n'est pas en BD deleche une
     * ComputerNeedIdToBeUpdateException.
     * @throws ComputerNeedIdToBeUpdateException
     *             Si l'ID n'est pas présent
     */
    @Test
    @DisplayName("Should not update computer with no ID and throw ComputerNeedIdToBeUpdateException and should not update computer with ID 1111L (no present in database)")
    public void updateComputerTransientTest() throws ComputerNeedIdToBeUpdateException {
        final Computer computer = new Computer.Builder(null).build();
        assertThrows(ComputerNeedIdToBeUpdateException.class, () -> computerDao.update(computer));
        final Computer computer3 = new Computer.Builder("name").id(1111L).build();
        assertNull(computerDao.update(computer3));
    }

    /**
     * Verifie que la mise a jour d'un ordinateur qui existe en BD fonctionne.
     * @throws ComputerNeedIdToBeUpdateException
     *             Si le computer n'a pas d'ID
     */
    @Test
    @DisplayName("Should update the computer 1L (existing in dtabase)")
    public void updateComputerExistingTest() throws ComputerNeedIdToBeUpdateException {
        final Computer computer = new Computer.Builder("rename").id(9L).introduced(LocalDate.now())
                .discontinued(LocalDate.parse("2015-02-02", DateTimeFormatter.ISO_LOCAL_DATE))
                .company(new Company.Builder(1L).build()).build();
        computerDao.update(computer);
        final Computer updated = computerDao.update(computer);
        assertEquals("rename", updated.getName());
        assertSame(9L, updated.getId());
        assertEquals(LocalDate.parse("2015-02-02", DateTimeFormatter.ISO_LOCAL_DATE), updated.getDiscontinued());
    }

    /**
     * Test la mise a jour d'un computer quand une SQLException intervient.
     * @throws SQLException
     *             SQLException
     * @throws ComputerNeedIdToBeUpdateException
     *             Quand l'ID du computer n'est pas donnée
     */
    @Test
    @DisplayName("Update should return null when a SQLException is thrown")
    public void updateComputerWhenSQLExceptionIsCatched() throws SQLException, ComputerNeedIdToBeUpdateException {
        scenariseConnectionAndPreparedStatement(true);
        assertNull(mockComputerDao.update(computer));
        verifyConnectionAndPreparedStatement(true);
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
     * Test la supression d'un computer quand une SQLException intervient.
     * @throws SQLException
     *             SQLException
     */
    @Test
    @DisplayName("Should return false when a SQLException is thrown")
    public void deleteComputerWhenSQLExceptionIsCatched() throws SQLException {
        scenariseConnectionAndPreparedStatement(true);
        assertFalse(mockComputerDao.delete(1L));
        verifyConnectionAndPreparedStatement(true);
    }

    /**
     * @param update
     *            false si c'est un find, true sinon
     * @throws SQLException
     *             When SQLException occures.
     */
    private void verifyConnectionAndPreparedStatement(final boolean update) throws SQLException {
        Mockito.verify(connection).prepareStatement(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
        if (update) {
            Mockito.verify(ps).executeUpdate();
        } else {
            Mockito.verify(ps).executeQuery();
        }

    }

    /**
     * @param update
     *            false si c'est un find, true sinon
     * @throws SQLException
     *             When SQLException occures.
     */
    private void scenariseConnectionAndPreparedStatement(final boolean update) throws SQLException {
        Mockito.when(connection.prepareStatement(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ps);
        if (update) {
            Mockito.when(ps.executeUpdate()).thenThrow(SQLException.class);
        } else {
            Mockito.when(ps.executeQuery()).thenThrow(SQLException.class);
        }

    }
}