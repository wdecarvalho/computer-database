package main.java.com.excilys.dao;

import java.sql.Connection;
import java.util.Collection;

import main.java.com.excilys.model.Company;

public class CompanyDao extends Dao<Company> {

	public CompanyDao(Connection conn) {
		super(conn);
	}

	public boolean create(final Company obj) {
		return false;
	}

	public boolean delete(final Company obj) {
		return false;
	}

	public boolean update(final Company obj) {
		return false;
	}

	@Override
	public Company find(final Long id) {
		return null;
	}

	@Override
	public Collection<Company> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
