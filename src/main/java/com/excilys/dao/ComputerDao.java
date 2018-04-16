package main.java.com.excilys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import main.java.com.excilys.model.Computer;

public class ComputerDao extends Dao<Computer> {

	public ComputerDao(Connection conn) {
		super(conn);
	}

	@Override
	public boolean create(Computer obj) {
		return false;
	}

	@Override
	public boolean delete(Computer obj) {
		return false;
	}

	@Override
	public boolean update(Computer obj) {
		return false;
	}

	@Override
	public Computer find(Long id) {
		try {
			PreparedStatement ps = this.getConnection().prepareStatement(
					"SELECT * FROM Computer WHERE id = :id",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ps.setInt(0,id.intValue());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	
	
}
