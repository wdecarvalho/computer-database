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

import main.java.com.excilys.model.Company;
import main.java.com.excilys.util.Pages;

public class CompanyDao extends Dao<Company> {

	private static final String FIND_ONE_COMPANY = "SELECT company.id, company.name FROM company where company.id = ?;";
	private static final String FIND_ALL_COMPANY = "SELECT company.id, company.name FROM company";
	private static final String FIND_COMPUTER_PAGE = "SELECT company.id, company.name "
			+ "FROM company ORDER BY company.id ASC LIMIT ? OFFSET ? ";
	private static final String NUMBER_PAGE_MAX = "SELECT COUNT(company.id) FROM company ORDER BY company.id";
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDao.class);


	public CompanyDao(Connection conn) {
		super(conn);
	}


	@Override
	public Optional<Company> find(final Long id) {
		Optional<Company> company = Optional.empty();
		PreparedStatement preparedStatement;
		try {
			preparedStatement = this.getConnection().prepareStatement(
					FIND_ONE_COMPANY,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setInt(1,id.intValue());
			final ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()) {
				company = Optional.ofNullable(new Company(resultSet.getLong("id"),resultSet.getString("name")));
			}
		} catch (SQLException e) {
			LOGGER.debug(e.getMessage());
		}
		return company;
	}

	@Override
	public Collection<Company> findAll() {
		final Collection<Company> companys = new ArrayList<>();
		try {
			final PreparedStatement preparedStatement = this.getConnection().prepareStatement(
					FIND_ALL_COMPANY,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			final ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()){
				companys.add(new Company(resultSet.getLong("id"),resultSet.getString("name")));
			}
		} catch (SQLException e) {
			LOGGER.debug(e.getMessage());
		}
		return companys;
	}

	/**
	 * Recupere le nombre de company en BD
	 * @return Nombre de company
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
	public Pages<Company> findPerPage(int page){
		if(page <= 1) {
			page = 1;
		}
		Pages<Company> pages = new Pages<Company>(page);
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
				pages.getEntities().add(new Company(resultSet.getLong("id"),resultSet.getString("name")));
					
			}
		} catch (SQLException e) {
			LOGGER.debug(e.getMessage());
		}
		return pages;
	}
}
