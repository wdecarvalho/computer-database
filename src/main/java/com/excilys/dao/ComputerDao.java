package com.excilys.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.mapper.MapUtil;
import com.excilys.mapper.MapperResulSetToComputer;
import com.excilys.model.Computer;
import com.excilys.util.Pages;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

@Repository
public class ComputerDao extends Dao<Computer> {
    private static final String ERROR_CODE_DATE_SQL = "22001";
    private static final String ID = "id";
    private static final String COMPUTER = "computer";
    private static final String COMPANY_ID = "company_id";
    private static final String DISCONTINUED = "discontinued";
    private static final String INTRODUCED = "introduced";
    private static final String NAME = "name";
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

    /**
     * Constructeur de ComputerDao.
     */
    private ComputerDao() {

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
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(getJdbcTemplate());
        jdbcInsert.withTableName(COMPUTER).usingGeneratedKeyColumns(ID);
        SqlParameterSource parameterSource = new MapSqlParameterSource().addValue(NAME, obj.getName())
                .addValue(INTRODUCED, MapUtil.convertLocalDateToTimeStamp(obj.getIntroduced()))
                .addValue(DISCONTINUED, MapUtil.convertLocalDateToTimeStamp(obj.getDiscontinued()))
                .addValue(COMPANY_ID, obj.getCompany() == null ? null : obj.getCompany().getId());
        Long id = -1L;
        try {
            id = jdbcInsert.executeAndReturnKey(parameterSource).longValue();
        } catch (DataIntegrityViolationException e) {
            if (e.getCause() instanceof MysqlDataTruncation) {
                if (((MysqlDataTruncation) e.getCause()).getSQLState().equals(ERROR_CODE_DATE_SQL)) {
                    throw new DateTruncationException();
                }
            } else {
                LOGGER.error(e.getMessage());
            }
        }
        return id;
    }

    @Override
    public boolean delete(final Long iD) {
        boolean res = false;
        if (getJdbcTemplate().update(DELETE_ONE_COMPUTER, iD) == 1) {
            res = true;
        }
        return res;
    }

    /**
     * Supprime une liste de computer.
     * @param iDs
     *            ID des computers a supprimer
     * @return Vrai si fonctionne, faux sinon
     * @throws ComputerNotDeletedException
     *             Un ou plusieurs computers n'ont pas pu être supprimé.
     */
    public boolean delete(final String iDs) throws ComputerNotDeletedException {
        boolean res = false;
        int nbComputerDeleted = getJdbcTemplate().update(String.format(DELETE_LIST_COMPUTER, iDs));
        if (nbComputerDeleted == iDs.split(",").length) {
            res = true;
        } else if (nbComputerDeleted > 0) {
            throw new ComputerNotDeletedException();
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
        final ArrayList<Object> params = new ArrayList<>();
        params.add(obj.getName());
        params.add(MapUtil.convertLocalDateToTimeStamp(obj.getIntroduced()));
        params.add(MapUtil.convertLocalDateToTimeStamp(obj.getDiscontinued()));
        if (obj.getCompany() != null) {
            params.add(obj.getCompany().getId());
        } else {
            params.add(null);
        }
        params.add(obj.getId());
        try {
            if (getJdbcTemplate().update(UPDATE_ONE_COMPUTER, params.toArray()) != 0) {
                computer = Optional.ofNullable(obj);
            }
        } catch (DataIntegrityViolationException e) {
            if (((MysqlDataTruncation) e.getCause()).getSQLState().equals(ERROR_CODE_DATE_SQL)) {
                throw new DateTruncationException();
            } else {
                LOGGER.error(e.getMessage());
            }
        }
        return computer;
    }

    @Override
    public Optional<Computer> find(final Long id) {
        Optional<Computer> computer = Optional.empty();
        try {
            computer = Optional.ofNullable(
                    getJdbcTemplate().queryForObject(FIND_ONE_COMPUTER, new MapperResulSetToComputer(), id));
        } catch (EmptyResultDataAccessException e) {
            // Nothing to espacially
        }
        return computer;

    }

    @Override
    public Collection<Computer> findAll() {
        return getJdbcTemplate().query(FIND_ALL_COMPUTER, new Object[0], new MapperResulSetToComputer());
    }

    /**
     * Recupere le nombre d'ordinateur en BD.
     * @return Nombre d'ordinateur
     * @throws SQLException
     *             Si une erreur SQL intervient
     */
    public int numberOfElement() {
        return getJdbcTemplate().queryForObject(NUMBER_PAGE_MAX, Integer.class);
    }

    /**
     * Recupere le nombre d'ordinateur max de la recherche.
     * @param search
     *            Parametre de recherche (nom)
     * @return Nombre d'ordinateur trouvée.
     * @throws SQLException
     *             SQLException
     */
    public int numberOfElementToSearch(final String search) {
        return getJdbcTemplate().queryForObject(NUMBER_PAGE_MAX_SEARCH, Integer.class, search, search);
    }

    @Override
    public Pages<Computer> findPerPage(int... pageAndResult) {
        int page = pageAndResult[0];
        if (page <= 1) {
            page = 1;
        }
        Pages<Computer> pages = new Pages<Computer>(page);
        trySetNumberResultPerPage(pages, pageAndResult);
        createAndExecuteSearchPerPageSql(pages, FIND_COMPUTER_PAGE, "");
        return pages;
    }

    /**
     * Recupere le mot a recherché et effectue une recherche par page.
     * @param search
     *            Mot a recherhcé
     * @param pageAndResult
     *            Contient la page destination et le nombre de resutlat souhaitée
     * @return Pages de computer
     */
    public Pages<Computer> findPerPage(final String search, int... pageAndResult) {
        int page = pageAndResult[0];
        if (page <= 1) {
            page = 1;
        }
        Pages<Computer> pages = new Pages<Computer>(page);
        trySetNumberResultPerPage(pages, pageAndResult);
        createAndExecuteSearchPerPageSql(pages, SEARCH_COMPUTER, search);
        return pages;
    }

    /**
     * Modifie le nombre de resultat par page si il est modifiée par l'utilisateur.
     * @param pages
     *            Pages d'éléments
     * @param pageAndResult
     *            Page courante et nombre de resultat par page
     */
    private void trySetNumberResultPerPage(final Pages<Computer> pages, int[] pageAndResult) {
        if (pageAndResult.length > 1) {
            pages.setNumberPerPageResult(pageAndResult[1]);
        }
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
            final String search) {
        String searchAll = "";
        if (search.isEmpty()) { // Si l'utilisateur n'a rien recherché.
            pages.setPageMax(numberOfElement());
        } else {
            searchAll = new StringBuilder("%").append(search).append("%").toString();
            pages.setPageMax(numberOfElementToSearch(searchAll));
        }
        final ArrayList<Object> arrayParam = new ArrayList<>();
        if (!search.isEmpty()) {
            arrayParam.add(searchAll);
            arrayParam.add(searchAll);
        }
        arrayParam.add(pages.getNumberPerPageResult());
        arrayParam.add(pages.startResult());
        pages.getEntities()
                .addAll(getJdbcTemplate().query(request, arrayParam.toArray(), new MapperResulSetToComputer()));
    }

}
