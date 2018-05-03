package com.excilys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.mapper.MapUtil;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.util.Pages;

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
    private static final String NUMBER_PAGE_MAX = "SELECT COUNT(*) FROM computer";

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDao.class);

    private static ComputerDao computerDao;

    /**
     * Constructeur de ComputerDao.
     * @param conn
     *            Connection
     */
    private ComputerDao(Connection conn) {
        super(conn);
    }

    /**
     * Permet de recuperer l'instance d'un ComputerDao.
     * @param conn
     *            Connection
     * @return ComputerDao
     */
    public static ComputerDao getInstance(final Connection conn) {
        if (computerDao == null) {
            computerDao = new ComputerDao(conn);
        }
        return computerDao;
    }

    /**
     * Ajouter un computer dans la base de donnée.
     * @param obj
     *            Computer
     * @return true si l'objet est ajouté sinon false
     */
    public Long create(final Computer obj) {
        Long id = -1L;
        try (PreparedStatement pStatement = this.getConnection().prepareStatement(CREATE_ONE_COMPUTER,
                Statement.RETURN_GENERATED_KEYS)) {
            pStatement.setString(1, obj.getName());
            pStatement.setTimestamp(2, MapUtil.convertLocalDateToTimeStamp(obj.getIntroduced()));
            pStatement.setTimestamp(3, MapUtil.convertLocalDateToTimeStamp(obj.getDiscontinued()));
            pStatement.setString(4, obj.getCompany() == null ? null : obj.getCompany().getId() + "");
            pStatement.executeUpdate();
            try (ResultSet rSet = pStatement.getGeneratedKeys()) {
                rSet.next();
                id = rSet.getLong(1);
            }
        } catch (SQLException e1) {
            LOGGER.error(e1.getMessage());
        }
        return id;
    }

    /**
     * Supprime un computer de la base de donnée.
     * @param iD
     *            ID du computer
     * @return true si l'objet est supprimé sinon false
     */

    public boolean delete(final Long iD) {
        boolean res = false;
        try (PreparedStatement ps = this.getConnection().prepareStatement(DELETE_ONE_COMPUTER,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ps.setLong(1, iD);
            if (ps.executeUpdate() == 1) {
                res = true;
            }
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }
        return res;
    }

    /**
     * Met a jour le computer en base de donnée.
     * @param obj
     *            Computer
     * @return true si l'objet est mit a jour sinon false
     */

    public Optional<Computer> update(final Computer obj) {
        Optional<Computer> computer = Optional.empty();
        try (PreparedStatement ps = this.getConnection().prepareStatement(UPDATE_ONE_COMPUTER,
                ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            ps.setString(1, obj.getName());
            ps.setTimestamp(2,
                    obj.getIntroduced() == null ? null : MapUtil.convertLocalDateToTimeStamp(obj.getIntroduced()));
            ps.setTimestamp(3,
                    obj.getDiscontinued() == null ? null : MapUtil.convertLocalDateToTimeStamp(obj.getDiscontinued()));
            if (obj.getCompany() != null) {
                ps.setLong(4, obj.getCompany().getId());
            } else {
                ps.setNull(4, java.sql.Types.BIGINT);
            }
            ps.setLong(5, obj.getId());
            if (ps.executeUpdate() != 0) {
                computer = Optional.ofNullable(obj);
            }
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }
        return computer;
    }

    @Override
    public Optional<Computer> find(final Long id) {
        Optional<Computer> computer = Optional.empty();
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(FIND_ONE_COMPUTER,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            preparedStatement.setInt(1, id.intValue());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    computer = createComputerWithcompany(computer, resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }

        return computer;
    }

    @Override
    public Collection<Computer> findAll() {
        final Collection<Computer> computers = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(FIND_ALL_COMPUTER,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                computers.add(createComputerWithcompany(null, resultSet).get());
            }
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }
        return computers;
    }

    /**
     * Permet de creer un computer si il existe. (La company est spécifiée si elle
     * existe)
     * @param computer
     *            : Computer optionel
     * @param resultSet
     *            : Resultat de la requete SQL
     * @return Un computer si il existe
     * @throws SQLException
     *             Si une erreur SQL intervient
     */
    private Optional<Computer> createComputerWithcompany(Optional<Computer> computer, final ResultSet resultSet)
            throws SQLException {
        Company company = null;
        if (resultSet.getString("company.id") != null) {

            company = new Company.Builder(resultSet.getLong("company.id")).name(resultSet.getString("company.name"))
                    .build();
        }
        final String computerName = resultSet.getString("computer.name");
        final Long computerId = resultSet.getLong("computer.id");

        computer = Optional.ofNullable(new Computer.Builder(computerName).id(computerId)
                .introduced(MapUtil.convertTimeStampToLocal(resultSet.getTimestamp("computer.introduced")))
                .discontinued(MapUtil.convertTimeStampToLocal(resultSet.getTimestamp("computer.discontinued")))
                .company(company).build());
        return computer;
    }

    /**
     * Recupere le nombre d'ordinateur en BD.
     * @return Nombre d'ordinateur
     * @throws SQLException
     *             Si une erreur SQL intervient
     */
    public int numberOfElement() throws SQLException {
        final PreparedStatement preparedStatement = this.getConnection().prepareStatement(NUMBER_PAGE_MAX,
                ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rSet = preparedStatement.executeQuery();
        rSet.next();
        int numberElement = rSet.getInt(1);
        preparedStatement.close();
        rSet.close();
        return numberElement;
    }

    @Override
    public Pages<Computer> findPerPage(int... pageAndNumberResult) {
        int page = pageAndNumberResult[0];
        if (page <= 1) {
            page = 1;
        }
        Pages<Computer> pages = new Pages<Computer>(page);
        if (pageAndNumberResult.length > 1) {
            pages.setNumberPerPageResult(pageAndNumberResult[1]);
        }
        try {
            pages.setPageMax(numberOfElement());
            try (PreparedStatement preparedStatement = this.getConnection().prepareStatement(FIND_COMPUTER_PAGE,
                    ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                preparedStatement.setInt(1, pages.getNumberPerPageResult());
                preparedStatement.setInt(2, pages.startResult());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        pages.getEntities().add(createComputerWithcompany(null, resultSet).get());
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }
        return pages;
    }

}
