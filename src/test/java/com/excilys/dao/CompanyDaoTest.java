package com.excilys.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;

import com.excilys.exception.DaoNotInitializeException;
import com.excilys.extensions.MockitoExtension;
import com.excilys.model.Company;

@ExtendWith(MockitoExtension.class) // RunWith
@TestInstance(Lifecycle.PER_CLASS)
public class CompanyDaoTest {

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
        RunScript.execute(companyDao.getConnection(), new FileReader("src/test/resources/test_db.sql"));
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
    public void findAllcomputerTest() {
        assertTrue(companyDao.findAll().size() == 9);
    }

    /**
     * Verifie que la recuperation par page fonctionne meme si on va trop loin
     * (retour a la derniere).
     */
    @Test
    @DisplayName("Test find companys by page")
    public void findcomputerByPage() {
        assertEquals(1, companyDao.findPerPage(1).getPageCourante());
        assertEquals(1, companyDao.findPerPage(2).getPageCourante());
    }

    /**
     * Verifie que la recuperation de page incorrecte affiche bien la premeire page.
     */
    @Test
    @DisplayName("Test find company by impossible pages")
    public void findComputerByPageNotPossible() {
        assertEquals(1, companyDao.findPerPage(0).getPageCourante());
        assertEquals(1, companyDao.findPerPage(-1).getPageCourante());
    }

}
