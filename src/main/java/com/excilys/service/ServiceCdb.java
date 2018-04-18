package main.java.com.excilys.service;

import java.sql.SQLException;
import java.util.Collection;

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
		return computerDao.create(c);
	}
	
	/**
	 * Demande a la DAO de mettre a jour un computer
	 * @param c Computer à mettre a jour
	 * @return True si réussi
	 * @throws CompanyNotFoundException 
	 */
	public boolean updateComputer(final Computer c) {
		return computerDao.update(c);
	}
	
	/**
	 * Demande a la DAO de supprimer un computer
	 * @param c Computer à supprimer
	 * @return True si réussi
	 */
	public boolean deleteComputer(final Computer c) {
		return computerDao.delete(c);
	}
	
	public boolean isExistCompany(final Long id) {
		return companyDao.find(id).isPresent();
	}
}
