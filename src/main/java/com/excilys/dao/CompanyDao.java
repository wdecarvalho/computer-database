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

public class CompanyDao extends Dao<Company> {
	
	private static final String FIND_ONE_COMPANY = "SELECT company.id, company.name FROM company where company.id = ?;";
	private static final String FIND_ALL_COMPANY = "SELECT company.id, company.name FROM company";

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
			// FIXME Retour client necessaire ? 
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
}
