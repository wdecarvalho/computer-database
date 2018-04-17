package main.java.com.excilys.service;

import java.sql.SQLException;
import java.util.Collection;
import main.java.com.excilys.dao.CompanyDao;
import main.java.com.excilys.dao.ComputerDao;
import main.java.com.excilys.dao.DaoFactory;
import main.java.com.excilys.dao.DaoType;
import main.java.com.excilys.exception.ComputerNotFoundException;
import main.java.com.excilys.exception.DaoNotInitializeException;
import main.java.com.excilys.model.Company;
import main.java.com.excilys.model.Computer;

public class ServiceCdb {
	
	private CompanyDao companyDao;
	private ComputerDao computerDao;
	
	public ServiceCdb() {
		DaoFactory.getInstance();
		try {
			companyDao = (CompanyDao) DaoFactory.getDao(DaoType.COMPANY_DAO);
			computerDao = (ComputerDao) DaoFactory.getDao(DaoType.COMPUTER_DAO);
		} catch (SQLException | DaoNotInitializeException e) {
			e.getMessage();
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
}
