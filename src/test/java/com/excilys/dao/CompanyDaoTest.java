package com.excilys.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import org.mockito.junit.jupiter.MockitoExtension;

import com.excilys.exception.DaoNotInitializeException;
import com.excilys.model.Company;
import com.mysql.cj.protocol.Resultset;

@ExtendWith(MockitoExtension.class) // RunWith
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyDaoTest {

    @Mock
    private DaoFactory daoFactory;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement ps;

    @Mock
    private Resultset rs;

    @InjectMocks
    private CompanyDao mockCompanyDao;

    private CompanyDao companyDao;

    /**
     * SetUp la classe de test en initialisant la companyDao et en preparant la base
     * de données.
     * @throws SQLException
     *             Si une erreur SQL apparait
     * @throws DaoNotInitializeException
     *             Si la DAO n'est pas initialisée
     * @throws FileNotFoundException
     *             Si le fichier de script est manquant
     */
    @BeforeAll
    public void setUp() throws SQLException, DaoNotInitializeException, FileNotFoundException {
        companyDao = (CompanyDao) DaoFactory.getInstance().getDao(DaoType.COMPANY_DAO);
    }

    /*
     * Test find en base de donnée
     */

    /**
     * Verifie que la recherche d'une companie existante fonctionne.
     */
    @Test
    @DisplayName("Test find an existing company")
    public void findExistingCompanyTest() {
        assertEquals(new Company.Builder(1L).name("Apple Inc.").build(), companyDao.find(1L).get());
    }

    /**
     * Verifie que la recherche d'une companie qui n'existe pas déclenche une
     * NoSuchElementException.
     */
    @Test
    @DisplayName("Test find a company that not exist")
    public void findNotExistingComputerTest() {
        assertThrows(NoSuchElementException.class, () -> companyDao.find(111L).get());
    }

    /**
     * Verifie que la recherche d'une company avec un ID impossible déclenche une
     * NoSuchElementException.
     */
    @Test
    @DisplayName("Test find a company with invalid Long")
    public void findComputerWithInvalidIDTest() {
        assertThrows(NoSuchElementException.class, () -> companyDao.find(-1L).get());
    }

    /**
     * Verifie que findAll recupere toutes les companies.
     */
    @Test
    @DisplayName("Test find to get all companys")
    public void findAllCompanyTest() {
        assertTrue(companyDao.findAll().size() >= 38);
    }

    /**
     * Verifie que la recuperation par page fonctionne meme si on va trop loin
     * (retour a la derniere).
     */
    @Test
    @DisplayName("Test find companys by page")
    public void findcomputerByPageTest() {
        assertEquals(1, companyDao.findPerPage(1).getPageCourante());
        assertEquals(2, companyDao.findPerPage(2).getPageCourante());
    }

    /**
     * Verifie que la recuperation de page incorrecte affiche bien la premeire page.
     */
    @Test
    @DisplayName("Test find company by impossible pages")
    public void findComputerByPageNotPossibleTest() {
        assertEquals(1, companyDao.findPerPage(0).getPageCourante());
        assertEquals(1, companyDao.findPerPage(-1).getPageCourante());
    }

    /**
     * Verifie que la supression en cascade d'une companie fonctionne.
     */
    @Test
    @DisplayName("Should delete company 34 ")
    public void deleteCompanyAndHisComputers() {
        assertTrue(companyDao.delete(34L));
    }

    /**
     * Verifie que la supression en cascade d'une companie ne fonctionne pas si l'ID
     * n'est pas valide.
     */
    @Test
    @DisplayName("Should not delete company 66 ")
    public void deleteCompanyAndHisComputersWhenIDNotValid() {
        assertFalse(companyDao.delete(66L));
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
        assertEquals(Optional.empty(), mockCompanyDao.find(1L));
        verifyConnectionAndPreparedStatement(false, false);
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
        assertEquals(0, mockCompanyDao.findAll().size());
        verifyConnectionAndPreparedStatement(false, false);
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
        assertSame(0, mockCompanyDao.findPerPage(1).getEntities().size());
        verifyConnectionAndPreparedStatement(false, false);
    }

    /**
     * Verifie que rien n'est supprimé sur une SQLException intervient.
     * @throws SQLException
     *             SQLException
     */
    @Test
    @DisplayName("Should not delete a company if a SQLException occures")
    public void deleteACompanyWhenSQLExceptionOccures() throws SQLException {
        scenariseConnectionAndPreparedStatement(true);
        assertEquals(false, mockCompanyDao.delete(27L));
        verifyConnectionAndPreparedStatement(false, true);
    }

    /**
     * @param update
     *            false si c'est un find, true sinon
     * @param delete
     *            true si c'est un delete, false sinon
     * @throws SQLException
     *             When SQLException occures.
     */
    private void verifyConnectionAndPreparedStatement(final boolean update, final boolean delete) throws SQLException {
        Mockito.verify(daoFactory).getConnexion();
        if (delete) {
            Mockito.verify(connection, Mockito.times(2)).prepareStatement(Mockito.anyString(), Mockito.anyInt(),
                    Mockito.anyInt());
        } else {
            Mockito.verify(connection).prepareStatement(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt());
        }
        if (update || delete) {
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
        Mockito.when(daoFactory.getConnexion()).thenReturn(connection);
        Mockito.when(connection.prepareStatement(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(ps);
        if (update) {
            Mockito.when(ps.executeUpdate()).thenThrow(SQLException.class);
        } else {
            Mockito.when(ps.executeQuery()).thenThrow(SQLException.class);
        }

    }

}
