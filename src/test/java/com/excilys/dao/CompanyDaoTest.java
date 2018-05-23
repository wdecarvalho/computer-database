package com.excilys.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.excilys.config.ServerConfiguration;
import com.excilys.model.Company;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(classes = ServerConfiguration.class)
public class CompanyDaoTest {

    @Mock
    JdbcTemplate jdbcTemplate;

    @InjectMocks
    CompanyDao mockCompanyDao;

    @Autowired
    private CompanyDao companyDao;

    /**
     * Init les annotations.
     */
    @BeforeAll
    public void setUp() {
        MockitoAnnotations.initMocks(this);
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
     * Verifie que la supression en cascade d'une companie ne fonctionne pas si l'ID
     * n'est pas valide.
     */
    @SuppressWarnings("serial")
    @Test
    @DisplayName("Should not delete company 66 ")
    public void deleteeCompanyAndHisComputersWhenIDNotValid() {
        Mockito.when(mockCompanyDao.getJdbcTemplate().update(Mockito.anyString(), Mockito.anyLong()))
                .thenThrow(new DataAccessException("Anonymous") {
                });
        assertFalse(mockCompanyDao.delete(1L));
    }
}