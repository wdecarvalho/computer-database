package main.java.com.excilys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.java.com.excilys.mapper.MapUtil;
import main.java.com.excilys.model.Company;
import main.java.com.excilys.model.Computer;
import main.java.com.excilys.util.Pages;

public class ComputerDao extends Dao<Computer> {

	private static final String DELETE_ONE_COMPUTER = "DELETE FROM computer where id = ?;";
	private static final String CREATE_ONE_COMPUTER = "INSERT INTO computer (name,introduced,discontinued,company_id) values (?,?,?,?);";
	private static final String UPDATE_ONE_COMPUTER = "UPDATE computer SET "
			+ "name = ?, introduced = ?, discontinued = ?, company_id = ? where id = ?;";
	private static final String FIND_ONE_COMPUTER = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
			+ "FROM computer LEFT OUTER JOIN company on computer.company_id = company.id where computer.id = ?;";
	private static final String FIND_ALL_COMPUTER = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
			+ "FROM computer LEFT OUTER JOIN company on computer.company_id = company.id";
	private static final String FIND_COMPUTER_PAGE = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
			+ "FROM computer LEFT OUTER JOIN company on computer.company_id = company.id ORDER BY computer.id ASC LIMIT ? OFFSET ? ";
	private static final String NUMBER_PAGE_MAX = "SELECT COUNT(computer.id) FROM computer ORDER BY computer.id";

	private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDao.class);

	public ComputerDao(Connection conn) {
		super(conn);
	}

	/**
	 * Ajouter un computer dans la base de donnée
	 * @param obj Computer
	 * @return true si l'objet est ajouté sinon false
	 */
	public boolean create(final Computer obj) {
		boolean res = false;
		try {
			final PreparedStatement ps = this.getConnection().prepareStatement(
					CREATE_ONE_COMPUTER,
					ResultSet.TYPE_SCROLL_SENSITIVE,
					ResultSet.CONCUR_UPDATABLE);
			ps.setString(1, obj.getName());
			ps.setTimestamp(2, MapUtil.convertLocalDateToTimeStamp(obj.getIntroduced()));
			ps.setTimestamp(3, MapUtil.convertLocalDateToTimeStamp(obj.getDiscontinued()));
			ps.setString(4, obj.getCompany() == null ? null : obj.getCompany().getId()+"");
			ps.executeUpdate();
			res = true;
		} catch (SQLException e) {
			LOGGER.error(e.getMessage());
		}
		return res;
	}


	/**
	 * Supprime un computer de la base de donnée 
	 * @param obj Computer
	 * @return true si l'objet est supprimé sinon false
	 */

	public boolean delete(final Computer obj) {
		boolean res = false;
		try {
			final PreparedStatement ps = this.getConnection().prepareStatement(
					DELETE_ONE_COMPUTER,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ps.setLong(1, obj.getId());
			ps.executeUpdate();
			res = true;
		} catch (SQLException e) {
			LOGGER.debug(e.getMessage());
		}
		return res;
	}


	/**
	 * Met a jour le computer en base de donnée
	 * @param obj Computer
	 * @return true si l'objet est mit a jour sinon false
	 */

	public boolean update(final Computer obj) {
		boolean res = false;
		try {
			final PreparedStatement ps = this.getConnection().prepareStatement(
					UPDATE_ONE_COMPUTER,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ps.setString(1, obj.getName());
			ps.setTimestamp(2, obj.getIntroduced() == null ? null : MapUtil.convertLocalDateToTimeStamp(obj.getIntroduced()));
			ps.setTimestamp(3, obj.getDiscontinued() == null ? null : MapUtil.convertLocalDateToTimeStamp(obj.getDiscontinued()));
			if(obj.getCompany() != null) {
				ps.setLong(4, obj.getCompany().getId());
			}
			else {
				ps.setNull(4, java.sql.Types.BIGINT);
			}
			ps.setLong(5, obj.getId());
			ps.executeUpdate();
			res = true;
		} catch (SQLException e) {
			LOGGER.debug(e.getMessage());
		}
		return res;
	}

	@Override
	public Optional<Computer> find(final Long id) {  
		Optional<Computer> computer = Optional.empty();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = this.getConnection().prepareStatement(
					FIND_ONE_COMPUTER,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setInt(1,id.intValue());
			final ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				computer = createComputerWithcompany(computer, resultSet);
			}
		} catch (SQLException e) {
			LOGGER.debug(e.getMessage());
		}

		return computer;
	}

	@Override
	public Collection<Computer> findAll() {
		final Collection<Computer> computers = new ArrayList<>();
		try {
			final PreparedStatement preparedStatement = this.getConnection().prepareStatement(
					FIND_ALL_COMPUTER,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			final ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
					computers.add(createComputerWithcompany(null, resultSet).get());
			}
		} catch (SQLException e) {
			LOGGER.debug(e.getMessage());
		}
		return computers;
	}
	
	/**
	 * Permet de creer un computer si il existe. (La company est spécifiée si elle existe)
	 * @param computer : Computer optionel
	 * @param resultSet : Resultat de la requete SQL
	 * @return Un computer si il existe
	 * @throws SQLException
	 */
	private Optional<Computer> createComputerWithcompany(Optional<Computer> computer, final ResultSet resultSet) throws SQLException {
		Company company = null;
		if(resultSet.getString("company.id") != null){
			company = new Company(resultSet.getLong("company.id"),resultSet.getString("company.name"));
		}
		computer = Optional.ofNullable(
				new Computer(
						resultSet.getLong("computer.id"),
						resultSet.getString("computer.name"),
						MapUtil.convertTimeStampToLocal(resultSet.getTimestamp("computer.introduced")),
						MapUtil.convertTimeStampToLocal(resultSet.getTimestamp("computer.discontinued")),
						company));
		return computer;
	}
	
	/**
	 * Recupere le nombre d'ordinateur en BD
	 * @return Nombre d'ordinateur
	 * @throws SQLException
	 */
	public int numberOfElement() throws SQLException { 
			final PreparedStatement preparedStatement = this.getConnection().prepareStatement(
					NUMBER_PAGE_MAX,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rSet = preparedStatement.executeQuery();
			rSet.next();
			return rSet.getInt(1);
	}
	
	@Override
	public Pages<Computer> findPerPage(int page){
		if(page <= 1) {
			page = 1;
		}
		Pages<Computer> pages = new Pages<Computer>(page);
		try {
			pages.setPage_max(numberOfElement());
			final PreparedStatement preparedStatement = this.getConnection().prepareStatement(
					FIND_COMPUTER_PAGE,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setInt(1, Pages.getNUMBER_PER_PAGE_RESULT());
			preparedStatement.setInt(2, pages.startResult());
			final ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				pages.getEntities().add(createComputerWithcompany(null, resultSet).get());
					
			}
		} catch (SQLException e) {
			LOGGER.debug(e.getMessage());
		}
		return pages;
	}



}
