package main.java.com.excilys.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.Collection;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.dao.CompanyDao;
import main.java.com.excilys.dao.ComputerDao;
import main.java.com.excilys.dao.DaoFactory;
import main.java.com.excilys.dao.DaoType;
import main.java.com.excilys.exception.CompanyNotFoundException;
import main.java.com.excilys.exception.ComputerNotFoundException;
import main.java.com.excilys.exception.DaoNotInitializeException;
import main.java.com.excilys.model.Company;
import main.java.com.excilys.model.Computer;
import main.java.com.excilys.util.Pages;

public class ServiceCdb {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCdb.class);
	
	private CompanyDao companyDao;
	private ComputerDao computerDao;

	
	public ServiceCdb() throws DaoNotInitializeException {
		DaoFactory.getInstance();
		try {
			companyDao = (CompanyDao) DaoFactory.getDao(DaoType.COMPANY_DAO);
			computerDao = (ComputerDao) DaoFactory.getDao(DaoType.COMPUTER_DAO);
		} catch (SQLException e) {
			LOGGER.debug(e.getMessage());
			throw new DaoNotInitializeException();
			
		} catch (DaoNotInitializeException e1) {
			LOGGER.error(e1.getMessage());
		}
		
	}
	
	/**
	 * Recupere la liste des computers
	 * @return Collection de computer
	 */
	public Collection<Computer> getListComputers() {
		return computerDao.findAll(); 
	}
	
	/**
	 * Recupere la liste des compagnies
	 * @return Colleciton de company
	 */
	public Collection<Company> getListCompanies(){
		return companyDao.findAll();
	}

	/**
	 * Recupere les information d'un computer
	 * @param id ID du computer recherché
	 * @return Computer 
	 */
	public Computer getComputerDaoDetails(final Long id) throws ComputerNotFoundException {
		return computerDao.find(id).orElseThrow(() -> new ComputerNotFoundException(""+id));
		
	}
	
	/**
	 * Demande a la DAO de crée un computer
	 * @param c Computer à sauvegarder
	 * @return True si réussi
	 */
	public boolean createComputer(final Computer c) {
		if(c.getIntroduced() == null || c.getIntroduced().compareTo(c.getDiscontinued()) <= 0){
			return computerDao.create(c);
		}
		return false;
	}
	
	/**
	 * Demande a la DAO de mettre a jour un computer
	 * @param c Computer à mettre a jour
	 * @return True si réussi
	 * @throws CompanyNotFoundException 
	 */
	public boolean updateComputer(final Computer c) {
		if(c.getIntroduced() == null || c.getIntroduced().compareTo(c.getDiscontinued()) <= 0){
			return computerDao.update(c);
		}
		return false;
		
	}
	
	/**
	 * Demande a la DAO de supprimer un computer
	 * @param c Computer à supprimer
	 * @return True si réussi
	 */
	public boolean deleteComputer(final Computer c) {
		return computerDao.delete(c);
	}
	
	/**
	 * Verofoe l'existence d'une company en BD
	 * @param id ID de la company a verifier
	 * @return true si elle existe
	 */
	public boolean isExistCompany(final Long id) {
		return companyDao.find(id).isPresent();
	}
	
	/**
	 * Retourne les computer par pages
	 * @param page Page de resultat
	 * @return
	 */
	public Pages<Computer> findByPagesComputer(int page){
		return computerDao.findPerPage(page);
	}
	
	/**
	 * Retourne les computers par pages
	 * @param page Page de resultat
	 * @return
	 */
	public Pages<Company> findByPagesCompany(int page){
		return companyDao.findPerPage(page);
	}
}
