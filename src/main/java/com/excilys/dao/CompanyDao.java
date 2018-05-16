package com.excilys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.mapper.MapResulSet;
import com.excilys.model.Company;
import com.excilys.util.Pages;

public class CompanyDao extends Dao<Company> {

    private static final String FIND_ONE_COMPANY = "SELECT company.id, company.name FROM company where company.id = ?;";
    private static final String FIND_ALL_COMPANY = "SELECT company.id, company.name FROM company";
    private static final String FIND_COMPUTER_PAGE = "SELECT company.id, company.name "
            + "FROM company ORDER BY company.id ASC LIMIT ? OFFSET ? ";
    private static final String NUMBER_PAGE_MAX = "SELECT COUNT(*) FROM company";
    private static final String DELETE_ONE_COMPANY = "DELETE FROM company where id = ? ;";
    private static final String DELETE_COMPUTER_LINKED = "DELETE FROM computer where company_id = ? ;";
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDao.class);

    private DaoFactory daoFactory;

    private static CompanyDao companyDao;

    /**
     * Constructeur de CompanyDao.
     */
    private CompanyDao() {
    }

    /**
     * Recupere l'instance de CompanyDao.
     * @param factory
     *            DaoFactory
     * @return CompanyDao
     */
    public static CompanyDao getInstance(final DaoFactory factory) {
        if (companyDao == null) {
            companyDao = new CompanyDao();
            companyDao.daoFactory = factory;
        }
        return companyDao;
    }

    @Override
    public Optional<Company> find(final Long id) {
        Optional<Company> company = Optional.empty();

        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ONE_COMPANY,
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            preparedStatement.setInt(1, id.intValue());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    company = MapResulSet.resulSetToOptionalCompanyComplete(resultSet);
                }
            }
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }
        return company;
    }

    @Override
    public Collection<Company> findAll() {
        final Collection<Company> companys = new ArrayList<>();
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_COMPANY,
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                companys.add(MapResulSet.resulSetToCompanyComplete(resultSet));
            }
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }
        return companys;
    }

    /**
     * Recupere le nombre de company en BD.
     * @return Nombre de company
     * @throws SQLException
     *             Si une erreur SQL apparait
     */
    public int numberOfElement() throws SQLException {
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement preparedStatement = connection.prepareStatement(NUMBER_PAGE_MAX,
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet rSet = preparedStatement.executeQuery()) {
            rSet.next();
            int numberElement = rSet.getInt(1);
            return numberElement;
        }

    }

    @Override
    public Pages<Company> findPerPage(int... pageWithoutNumberResult) {
        int page = pageWithoutNumberResult[0];
        if (page <= 1) {
            page = 1;
        }
        Pages<Company> pages = new Pages<Company>(page);
        try {
            pages.setPageMax(numberOfElement());
            try (Connection connection = daoFactory.getConnexion();
                    PreparedStatement preparedStatement = connection.prepareStatement(FIND_COMPUTER_PAGE,
                            ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
                preparedStatement.setInt(1, pages.getNumberPerPageResult());
                preparedStatement.setInt(2, pages.startResult());
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        pages.getEntities().add(MapResulSet.resulSetToCompanyComplete(resultSet));
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }
        return pages;
    }

    @Override
    public boolean delete(Long id) {
        boolean res = false;
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement preparedStatement = connection.prepareStatement(DELETE_ONE_COMPANY,
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            try (PreparedStatement preparedStatement2 = connection.prepareStatement(DELETE_COMPUTER_LINKED,
                    ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                preparedStatement2.setLong(1, id);
                preparedStatement2.executeUpdate();
                preparedStatement.setLong(1, id);
                if (preparedStatement.executeUpdate() == 1) {
                    res = true;
                }
                connection.commit();
            }
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
            res = false;
        }
        return res;
    }
}
