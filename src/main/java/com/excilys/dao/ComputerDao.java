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

import com.excilys.exception.DateTruncationException;
import com.excilys.mapper.MapResulSet;
import com.excilys.mapper.MapUtil;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.util.Pages;

public class ComputerDao extends Dao<Computer> {
    private static final String SEARCH_COMPUTER = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company on computer.company_id = company.id WHERE computer.name LIKE ? or company.name LIKE ? ORDER BY computer.name ASC LIMIT ? OFFSET ? ";
    private static final String DELETE_ONE_COMPUTER = "DELETE FROM computer where id = ?;";
    private static final String DELETE_LIST_COMPUTER = "DELETE FROM computer where id in %s ;";
    private static final String CREATE_ONE_COMPUTER = "INSERT INTO computer (name,introduced,discontinued,company_id) values (?,?,?,?);";
    private static final String UPDATE_ONE_COMPUTER = "UPDATE computer SET "
            + "name = ?, introduced = ?, discontinued = ?, company_id = ? where id = ?;";
    private static final String FIND_ONE_COMPUTER = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company on computer.company_id = company.id where computer.id = ?;";
    private static final String FIND_ALL_COMPUTER = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company on computer.company_id = company.id";
    private static final String FIND_COMPUTER_PAGE = "SELECT computer.id, computer.name, computer.introduced, computer.discontinued, company.id, company.name "
            + "FROM computer LEFT OUTER JOIN company on computer.company_id = company.id ORDER BY computer.id ASC LIMIT ? OFFSET ? ";
    private static final String NUMBER_PAGE_MAX = "SELECT COUNT(computer.id) FROM computer";
    private static final String NUMBER_PAGE_MAX_SEARCH = "SELECT count(computer.id) FROM computer LEFT OUTER JOIN company on computer.company_id = company.id WHERE computer.name LIKE ? or company.name LIKE ?; ";

    private static final Logger LOGGER = LoggerFactory.getLogger(ComputerDao.class);

    private DaoFactory daoFactory;

    private static ComputerDao computerDao;

    /**
     * Constructeur de ComputerDao.
     */
    private ComputerDao() {
    }

    /**
     * Permet de recuperer l'instance d'un ComputerDao.
     * @param factory
     *            DaoFactory
     * @return ComputerDao
     */
    public static ComputerDao getInstance(final DaoFactory factory) {
        if (computerDao == null) {
            computerDao = new ComputerDao();
            computerDao.daoFactory = factory;
        }
        return computerDao;
    }

    /**
     * Ajouter un computer dans la base de donnée.
     * @param obj
     *            Computer
     * @return true si l'objet est ajouté sinon false
     * @throws DateTruncationException
     *             Lorsque une date invalide essaye de se stocker en BD
     */
    public Long create(final Computer obj) throws DateTruncationException {
        Long id = -1L;
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement pStatement = connection.prepareStatement(CREATE_ONE_COMPUTER,
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
        } catch (SQLException e) {
            if (e.getSQLState().equals("22001")) {
                throw new DateTruncationException();
            } else {
                LOGGER.debug(e.getMessage());
            }
        }
        return id;
    }

    @Override
    public boolean delete(final Long iD) {
        boolean res = false;
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement ps = connection.prepareStatement(DELETE_ONE_COMPUTER, ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE)) {
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
     * Supprime une liste de computer.
     * @param iDs
     *            ID des computers a supprimer
     * @return Vrai si fonctionne, faux sinon
     */
    public boolean delete(final String iDs) {
        boolean res = false;
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement ps = connection.prepareStatement(String.format(DELETE_LIST_COMPUTER, iDs),
                        ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
            if (ps.executeUpdate() > 0) {
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
     * @throws DateTruncationException
     *             Lorsque une date invalide essaye de se stocker en BD
     */

    public Optional<Computer> update(final Computer obj) throws DateTruncationException {
        Optional<Computer> computer = Optional.empty();
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement ps = connection.prepareStatement(UPDATE_ONE_COMPUTER, ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_UPDATABLE)) {
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
            if (e.getSQLState().equals("22001")) {
                throw new DateTruncationException();
            } else {
                LOGGER.debug(e.getMessage());
            }
        }
        return computer;
    }

    @Override
    public Optional<Computer> find(final Long id) {
        Optional<Computer> computer = Optional.empty();
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ONE_COMPUTER,
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
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_COMPUTER,
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

            company = MapResulSet.resulSetToCompanyOfComputer(resultSet);
        }
        computer = MapResulSet.resulsetToOptionalComputer(resultSet, company);
        return computer;
    }

    /**
     * Recupere le nombre d'ordinateur en BD.
     * @return Nombre d'ordinateur
     * @throws SQLException
     *             Si une erreur SQL intervient
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

    /**
     * Recupere le nombre d'ordinateur max de la recherche.
     * @param search
     *            Parametre de recherche (nom)
     * @return Nombre d'ordinateur trouvée.
     * @throws SQLException
     *             SQLException
     */
    public int numberOfElementToSearch(final String search) throws SQLException {
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement preparedStatement = connection.prepareStatement(NUMBER_PAGE_MAX_SEARCH,
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            preparedStatement.setString(1, search);
            preparedStatement.setString(2, search);
            try (ResultSet rSet = preparedStatement.executeQuery()) {
                rSet.next();
                int numberElement = rSet.getInt(1);
                return numberElement;
            }
        }

    }

    @Override
    public Pages<Computer> findPerPage(int... pageAndResultAndTypeSearch) {
        int page = pageAndResultAndTypeSearch[0];
        if (page <= 1) {
            page = 1;
        }
        Pages<Computer> pages = new Pages<Computer>(page);
        try {
            if (pageAndResultAndTypeSearch.length > 1) {
                pages.setNumberPerPageResult(pageAndResultAndTypeSearch[1]);
            }
            createAndExecuteSearchPerPageSql(pages, FIND_COMPUTER_PAGE, "");
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }
        return pages;
    }

    /**
     * Recupere le mot a recherché et effectue une recherche par page.
     * @param search
     *            Mot a recherhcé
     * @param pageAndResultAndTypeSearch
     *            Contient la page destination et le nombre de resutlat souhaitée
     * @return Pages de computer
     */
    public Pages<Computer> findPerPage(final String search, int... pageAndResultAndTypeSearch) {
        int page = pageAndResultAndTypeSearch[0];
        if (page <= 1) {
            page = 1;
        }
        Pages<Computer> pages = new Pages<Computer>(page);
        try {
            if (pageAndResultAndTypeSearch.length > 1) {
                pages.setNumberPerPageResult(pageAndResultAndTypeSearch[1]);
            }
            createAndExecuteSearchPerPageSql(pages, SEARCH_COMPUTER, search);
        } catch (SQLException e) {
            LOGGER.debug(e.getMessage());
        }
        return pages;
    }

    /**
     * Creer la requete SQL en fonction du besoin utilisateur et l'execute.
     * @param pages
     *            Page a afficher
     * @param request
     *            Requete SQL a executer si on fait une recherche ou non
     * @param search
     *            La requete SQL va recherché par rapport a cette attribut
     * @throws SQLException
     *             SQLException
     */
    private void createAndExecuteSearchPerPageSql(final Pages<Computer> pages, final String request,
            final String search) throws SQLException {
        String searchAll = "";
        if (search.isEmpty()) {
            pages.setPageMax(numberOfElement());
        } else {
            searchAll = new StringBuilder("%").append(search).append("%").toString();
            pages.setPageMax(numberOfElementToSearch(searchAll));
        }
        try (Connection connection = daoFactory.getConnexion();
                PreparedStatement preparedStatement = connection.prepareStatement(request,
                        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)) {
            int cpt = 0;
            if (!search.isEmpty()) {
                preparedStatement.setString(++cpt, searchAll);
                preparedStatement.setString(++cpt, searchAll);
            }
            preparedStatement.setInt(++cpt, pages.getNumberPerPageResult());
            preparedStatement.setInt(++cpt, pages.startResult());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    pages.getEntities().add(createComputerWithcompany(null, resultSet).get());
                }
            }
        }
    }

}
