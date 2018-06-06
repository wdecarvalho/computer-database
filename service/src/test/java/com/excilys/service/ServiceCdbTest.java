package com.excilys.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.excilys.config.ServiceConfig;
import com.excilys.dao.CompanyDAO;
import com.excilys.dao.ComputerDAO;
import com.excilys.exception.ComputerException;
import com.excilys.exception.ExceptionHelper;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNameNotPresentException;
import com.excilys.exception.computer.ComputerNeedIdToBeUpdateException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.computer.DateIntroShouldBeMinorthanDisconException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.service.company.ServiceCdbCompany;
import com.excilys.service.company.ServiceCompany;
import com.excilys.service.computer.ServiceCdbComputer;
import com.excilys.service.computer.ServiceComputer;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

@TestInstance(Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(classes = ServiceConfig.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ServiceCdbTest  {
    
    @Autowired
    public ApplicationContext applicationContext;

    @Mock
    CompanyDAO companyDao;

    @Mock
    ComputerDAO computerDao;

    @Mock
    private ServiceCompany mockServiceCompany;

    @Mock
    MysqlDataTruncation mysqlDataTruncation;

    @InjectMocks
    private ServiceComputer injectedServiceComputer;

    @InjectMocks
    private ServiceCompany injectedServiceCompany;

    @Autowired
    private ServiceCdbCompany serviceCompany;

    @Autowired
    private ServiceCdbComputer serviceComputer;

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
        new ExceptionHelper(applicationContext);
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
        Mockito.when(computerDao.findById(1L)).thenReturn(computerOptional);
        assertEquals(computer2, injectedServiceComputer.getComputerDaoDetails(1L));
        Mockito.verify(computerDao).findById(1L);
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
        Mockito.when(computerDao.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ComputerNotFoundException.class, () -> injectedServiceComputer.getComputerDaoDetails(1L));
        Mockito.verify(computerDao).findById(1L);
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
        assertEquals(collectionsComputer, injectedServiceComputer.getAll());
        assertEquals(collectionsCompanies, injectedServiceCompany.getAll());
        Mockito.verify(computerDao).findAll();
        Mockito.verify(companyDao).findAll();
    }

    /**
     * Demande a la DAO de recuperer par page les computer.
     */
    @Test
    @DisplayName("Test la recuperation par page de computer")
    public void getComputerByPageTest() {
        assertTrue(serviceComputer.findByPage(0, 60).getNumberOfElements() > 16);
        assertTrue(serviceComputer.findByPage(0, 30).getNumberOfElements() > 16);
        assertTrue(serviceComputer.findByPage(0).getNumberOfElements() == 10);

        List<Computer> computers = serviceComputer.findByPage(1, 11).getContent();
        assertEquals(serviceComputer.findByPage(2, 10).getContent().stream().findFirst().get().getId(),
                computers.get(computers.size() - 1).getId());
    }

    /**
     * Demande a la DAO de recuperer par page les companys.
     */
    @Test
    @DisplayName("Test la recuperation par page de company")
    public void getCompanyByPageTest() {
        assertEquals(new Long(1), serviceCompany.findByPage(-1, 1).getContent().get(0).getId());
        assertEquals(new Long(1), serviceCompany.findByPage(0, 1).getContent().get(0).getId());
        assertEquals(new Long(1), serviceCompany.findByPage(1, 1).getContent().get(0).getId());
        assertEquals(new Long(11), serviceCompany.findByPage(2, 1).getContent().get(0).getId());

        assertTrue(serviceCompany.findByPage(0, 60).getNumberOfElements() >= 9);
        assertTrue(serviceCompany.findByPage(0, 30).getNumberOfElements() >= 9);
    }

    /**
     * Verifie que un ordinateur qui existe est trouvé.
     */
    @Test
    @DisplayName("Should return true for 1 and false for -1 when find if exist")
    public void testIfExistCompanys() {
        assertTrue(serviceCompany.isExists(1L));
        assertFalse(serviceCompany.isExists(-1L));
    }

    /**
     * Verifie que la recherche d'une company qui n'existe pas retourne bien une
     * exception.
     */
    @Test
    @DisplayName("Should throw CompanyNotFoundException for company -1L")
    public void testThrowExceptionNotFoundWhenFindName() {
        assertThrows(CompanyNotFoundException.class, () -> serviceCompany.getCompanyNameById(-1L));
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
    public void createValidComputerTest() throws CompanyNotFoundException, DateTruncationException, ComputerException {
        Computer computer = new Computer.Builder("test").build();
        Computer computerSaved = new Computer.Builder("test").id(8L).build();
        Mockito.when(computerDao.save(computer)).thenReturn(computerSaved);
        assertSame(8L, injectedServiceComputer.save(computer, true));
        computer = new Computer.Builder("e").introduced(LocalDate.parse("2014-12-30"))
                .discontinued(LocalDate.parse("2015-01-01")).build();
        computerSaved = new Computer.Builder("e").introduced(LocalDate.parse("2014-12-30"))
                .discontinued(LocalDate.parse("2015-01-01")).id(2L).build();
        Mockito.when(computerDao.save(computer)).thenReturn(computerSaved);
        assertSame(2L, injectedServiceComputer.save(computer, true));
        Mockito.verify(computerDao, Mockito.times(2)).save(computer);
    }

    /**
     * Essaye de crée un computer sans nom ce qui genere une exception.
     */

    @Test
    @DisplayName("Test throw a ComputerNameNotPresentException because name is required")
    public void createComputerWithoutNameTest() {
        final Computer computer = new Computer.Builder(null).build();
        assertThrows(ComputerException.class, () -> injectedServiceComputer.save(computer, true));
        computer.setName("");
        assertThrows(ComputerException.class, () -> injectedServiceComputer.save(computer, true));
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
                () -> injectedServiceComputer.save(computer, true));
    }

    /**
     * Retourne une CompanyNotFoundException pour des entrées invalides.
     * @throws ComputerException
     *             Si une regle de validation echoue
     * @throws DateTruncationException
     *             Si la date est < 1970
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */

    @Test
    @DisplayName("Should throw a CompanyNotFoundException if save of wrong computer occures")
    public void createComputerWithInvalidCompany() throws DateTruncationException, ComputerException {
        final Company company = new Company.Builder(-1L).build();
        final Computer computer = new Computer.Builder("a").company(company).build();
        Mockito.when(computerDao.save(computer)).thenThrow(
                new DataIntegrityViolationException("", new SQLIntegrityConstraintViolationException("company")));
        assertThrows(CompanyNotFoundException.class, () -> injectedServiceComputer.save(computer, true));
    }

    /**
     * Retourne une DateTruncationException pour une date invalide.
     * @throws DateTruncationException
     *             Si la date est < 1970
     * @throws ComputerException
     *             SI une regle de validation echoue
     */
    @Test
    @DisplayName("Should throw a DateTruncationException if save of wrong date occures")
    public void createComputerWithInvalidDate() throws ComputerException {
        final Computer computer = new Computer.Builder("a").introduced(LocalDate.of(1969, 12, 30)).build();
        Mockito.when(mysqlDataTruncation.getSQLState()).thenReturn("22001");
        Mockito.when(computerDao.save(computer))
                .thenThrow(new DataIntegrityViolationException("", mysqlDataTruncation));
        assertThrows(DateTruncationException.class, () -> injectedServiceComputer.save(computer, false));
    }
    /*
     *
     * =============================================================================
     * == Update computers
     *
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
        assertThrows(ComputerNeedIdToBeUpdateException.class, () -> serviceComputer.update(computer, true));
        computer.setName("");
        computer.setId(1L);
        assertThrows(ComputerException.class, () -> serviceComputer.update(computer, true));
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
        assertThrows(DateIntroShouldBeMinorthanDisconException.class, () -> serviceComputer.update(computer, true));
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
        assertThrows(ComputerNeedIdToBeUpdateException.class, () -> serviceComputer.update(computer, true));
    }

    /**
     * Retourne une CompanyNotFoundException pour des entrées invalides.
     * @throws ComputerException
     *             Si une regle de validation echoue
     * @throws DateTruncationException
     *             Si la date est < 1970
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */

    @Test
    @DisplayName("Should throw a CompanyNotFoundException if save of wrong computer occures")
    public void updateComputerWithInvalidCompany() throws DateTruncationException, ComputerException {
        final Company company = new Company.Builder(-1L).build();
        final Computer computer = new Computer.Builder("a").company(company).id(1L).build();
        Mockito.when(computerDao.save(computer)).thenThrow(
                new DataIntegrityViolationException("", new SQLIntegrityConstraintViolationException("company")));
        assertThrows(CompanyNotFoundException.class, () -> injectedServiceComputer.update(computer, true));
    }

    /**
     * Retourne une DateTruncationException pour une date invalide.
     * @throws DateTruncationException
     *             Si la date est < 1970
     * @throws ComputerException
     *             SI une regle de validation echoue
     */
    @Test
    @DisplayName("Should throw a DateTruncationException if save of wrong date occures")
    public void updateComputerWithInvalidDate() throws ComputerException {
        final Computer computer = new Computer.Builder("a").introduced(LocalDate.of(1969, 12, 30)).id(1L).build();
        Mockito.when(mysqlDataTruncation.getSQLState()).thenReturn("22001");
        Mockito.when(computerDao.save(computer))
                .thenThrow(new DataIntegrityViolationException("", mysqlDataTruncation));
        assertThrows(DateTruncationException.class, () -> injectedServiceComputer.update(computer, false));
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
        Mockito.when(computerDao.save(computer.get())).thenReturn(computer.get());
        assertEquals(computer.get(), injectedServiceComputer.update(computer.get(), true));
        computer = Optional.ofNullable(new Computer.Builder("test").id(8L).discontinued(null)
                .introduced(LocalDate.parse("2015-12-30")).build());
        Mockito.when(computerDao.save(computer.get())).thenReturn(computer.get());
        assertEquals(computer.get(), injectedServiceComputer.update(computer.get(), true));
    }

    /*
     *
     * =============================================================================
     * ============== Delete Computers
     *
     * =============================================================================
     * ==============
     */

    /**
     * Demande a la DAO de supprimer un ordinateur qui n'existe pas.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs computer n'ont pas été supprimé
     */
    @Test
    @DisplayName("Test delete a computer that not exist")
    public void deleteComputerNotExistingTest() throws ComputerNotDeletedException {
        assertThrows(EmptyResultDataAccessException.class, () -> serviceComputer.deleteOne(50L));
    }

    /**
     * Demaned a la DAO de supprimer un ordinateur qui n'a pas d'ID ou un ID
     * incorrecte.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs ordinateurs n'ont pas pu être supprimé
     */
    @Test
    @DisplayName("Test delete a computer with no ID or invalid ID")
    public void deleteComputerWithInvalidIdTest() throws ComputerNotDeletedException {
        final Computer computer = new Computer.Builder("").build();
        assertThrows(InvalidDataAccessApiUsageException.class, () -> serviceComputer.deleteOne(computer.getId()));
        assertThrows(EmptyResultDataAccessException.class, () -> serviceComputer.deleteOne(-1L));
    }

    /**
     * Demande a la DAO de supprimer un ordinateur present en base.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs computers n'ont pas été supprimés
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    @Test
    @DisplayName("Test delete an existing computer")
    public void deleteComputerTest() throws ComputerNotDeletedException, CompanyNotFoundException {
        serviceComputer.deleteOne(7L);
    }

    /**
     * Demande a la DAO de supprimer un ordinateur présent en base de données.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs computers n'ont pas été supprimés
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    @Test
    @DisplayName("Test delete an existing company")
    public void deleteCompanytest() throws ComputerNotDeletedException, CompanyNotFoundException {
        serviceCompany.deleteOne(1L);
    }

    /**
     * Verifie que la suppresion d'une companie supprime bien tous ses computers.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs computers n'ont pas été supprimés
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     * @throws SQLException
     *             SQLException
     */
    @Test
    @DisplayName("Test delete an existing company with his computers")
    public void deleteCompanyAndHisComputers() throws ComputerNotDeletedException, CompanyNotFoundException {
        assertTrue(serviceComputer.findByPagesSearch("name", 0, 10).getNumberOfElements() >= 2);
        serviceComputer.deleteOne(32L);
        assertEquals(2, serviceComputer.findByPagesSearch("name", 0, 10).getNumberOfElements());
    }

    /**
     * Verifie que la suppresion d'une liste de computer coté DAO fonctionne.
     * @throws ComputerNotDeletedException
     *             Si un ou plusieurs computer n'ont pas pu être supprimé
     */
    @Test
    @DisplayName("Test delete a list of computers ")
    public void deleteListComputerTest() throws ComputerNotDeletedException {
        final List<Long> liste = Arrays.asList(4L, 2L);
        Mockito.when(computerDao.deleteByIdIn(liste)).thenReturn(2L);
        assertTrue(injectedServiceComputer.deleteMulitple(liste));
        Mockito.verify(computerDao).deleteByIdIn(liste);
    }

    /**
     * Demande a la DAO de supprimer une liste de computer non valide.
     * @throws ComputerNotDeletedException
     *             Computer Not deleted exception
     */
    @Test
    @DisplayName("Test delete a list of computers ")
    public void deleteListComputerNotValidTest() throws ComputerNotDeletedException {
        List<Long> liste = Arrays.asList(-4L, 1222L);
        Mockito.when(computerDao.deleteByIdIn(liste)).thenReturn(0L);
        assertTrue(!injectedServiceComputer.deleteMulitple(liste));
        Mockito.verify(computerDao).deleteByIdIn(liste);
    }

    /**
     * Demande a la DAO de supprimer un computer valide et un non valide.
     * @throws ComputerNotDeletedException
     *             Computer Not deleted exception
     */
    @Test
    @DisplayName("Test delete a list of computers with one valid and another non valid ")
    public void deleteListComputerSemivalidTest() {
        List<Long> liste = Arrays.asList(-4L, 15L);
        Mockito.when(computerDao.deleteByIdIn(liste)).thenReturn(1L);
        assertThrows(ComputerNotDeletedException.class, () -> injectedServiceComputer.deleteMulitple(liste));
        Mockito.verify(computerDao).deleteByIdIn(liste);
    }

    /**
     * Demande a la DAO de supprimer une company.
     * @throws ComputerNotDeletedException
     *             Si au moin un computer a été supprimé mais pas tous
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    @Test
    @DisplayName("Should delete company id ? and computers (?,?,?")
    public void deleteOneCompany() throws ComputerNotDeletedException, CompanyNotFoundException {
        assertEquals(3, serviceComputer.findByPagesSearch("cascade", 1).getNumberOfElements());
        serviceCompany.deleteOne(38L);
        assertEquals(0, serviceComputer.findByPagesSearch("cascade", 1).getNumberOfElements());
    }

    /**
     * Demande au service de supprimer une company qui ne possede pas de computer.
     * @throws ComputerNotDeletedException
     *             Un ou plusieurs n'ont pas été supprimés
     * @throws CompanyNotFoundException
     *             La companie n'existe pas
     */
    @Test
    @DisplayName("Delete a company without computer should not throw Exception")
    public void deleteOneCompanyWithoutComputer() throws ComputerNotDeletedException, CompanyNotFoundException {
        serviceCompany.deleteOne(43L);
    }

    /**
     * Demande au service de supprimer une company qui ne possede pas de computer.
     * @throws ComputerNotDeletedException
     *             Un ou plusieurs n'ont pas été supprimés
     * @throws CompanyNotFoundException
     *             La companie n'existe pas
     */
    @Test
    @DisplayName("Delete a company and his computers not deleting")
    public void deleteOneCompanyWithoutDeleteHisComputer()
            throws ComputerNotDeletedException, CompanyNotFoundException {
        Mockito.when(mockServiceCompany.getCompanyNameById(1L)).thenReturn("name");
        Mockito.when(computerDao.countByCompanyName("name")).thenReturn(1L);
        Mockito.when(computerDao.deleteByCompanyId(1L)).thenReturn(0L);
        assertThrows(ComputerNotDeletedException.class, () -> injectedServiceComputer.deleteByCompany(1L));
        Mockito.verify(mockServiceCompany).getCompanyNameById(1L);
        Mockito.verify(computerDao).deleteByCompanyId(1L);
    }
}
