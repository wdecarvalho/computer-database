package main.java.com.excilys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.logging.Logger;

import com.sun.istack.internal.FinalArrayList;

import main.java.com.excilys.model.Company;
import main.java.com.excilys.model.Computer;

public class ComputerDao extends Dao<Computer> {
	
	//map de company

	private static final Logger LOGGER = Logger.getLogger(ComputerDao.class.getName());
	
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
					"INSERT INTO computer (name,introduced,discontinued,company_id) values (?,?,?,?);",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ps.setString(1, obj.getName());
			ps.setTimestamp(2, obj.getIntroduced() == null ? null : Timestamp.valueOf(obj.getIntroduced().atStartOfDay()));
			ps.setTimestamp(3, obj.getDiscontinued() == null ? null : Timestamp.valueOf(obj.getDiscontinued().atStartOfDay()));
			ps.setString(4, obj.getCompany() == null ? null : obj.getCompany().getId()+"");
			ps.executeUpdate();
			res = true;
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
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
					"DELETE FROM computer where id = ?;",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ps.setLong(1, obj.getId());
			ps.executeUpdate();
			res = true;
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
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
					"UPDATE computer SET "
					+ "name = ?, introduced = ?, discontinued = ?, company_id = ? where id = ?;",ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
			ps.setString(1, obj.getName());
			ps.setTimestamp(2, obj.getIntroduced() == null ? null : Timestamp.valueOf(obj.getIntroduced().atStartOfDay()));
			ps.setTimestamp(3, obj.getDiscontinued() == null ? null : Timestamp.valueOf(obj.getDiscontinued().atStartOfDay()));
			ps.setLong(4, obj.getCompany() == null ? null : obj.getCompany().getId());
			ps.setLong(5, obj.getId());
			ps.executeUpdate();
			res = true;
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
		return res;
	}

	@Override
	public Computer find(final Long id) {  // FIXME Si company = null;
		Computer computer = null;
		try {
			final PreparedStatement preparedStatement = this.getConnection().prepareStatement(
					"SELECT com.id, com.name, com.introduced, com.discontinued, comp.id as comp_id, comp.name as comp_name "
					+ "fROM computer AS com LEFT OUTER JOIN company AS comp on com.id = comp.id where com.id = ?;",
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			preparedStatement.setInt(1,id.intValue());
			final ResultSet resultSet = preparedStatement.executeQuery();
			if(resultSet.next()){
				Company company = null;
				if(resultSet.getString("comp_id") != null){
					company = new Company(resultSet.getLong("comp_id"),resultSet.getString("comp_name"));
				}
				computer = new Computer(resultSet.getLong("id"),resultSet.getString("name"),resultSet.getTimestamp("introduced"),
						resultSet.getTimestamp("discontinued"),company);
			}
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
		return computer;
	}

	@Override
	public Collection<Computer> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
