package main.java.com.excilys.dao;

import java.sql.Connection;

import main.java.com.excilys.model.Company;

public class CompanyDao extends Dao<Company> {

	public CompanyDao(Connection conn) {
		super(conn);
	}

	@Override
	public boolean create(Company obj) {
		return false;
	}

	@Override
	public boolean delete(Company obj) {
		return false;
	}

	@Override
	public boolean update(Company obj) {
		return false;
	}

	@Override
	public Company find(Long id) {
		return null;
	}

}
