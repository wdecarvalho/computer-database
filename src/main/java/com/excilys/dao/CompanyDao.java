package com.excilys.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import com.excilys.mapper.MapperResulSetToCompany;
import com.excilys.model.Company;
import com.excilys.util.Pages;

@Repository
public class CompanyDao extends Dao<Company> {

    private static final String FIND_ONE_COMPANY = "SELECT company.id, company.name FROM company where company.id = ?;";
    private static final String FIND_ALL_COMPANY = "SELECT company.id, company.name FROM company";
    private static final String FIND_COMPUTER_PAGE = "SELECT company.id, company.name "
            + "FROM company ORDER BY company.id ASC LIMIT ? OFFSET ? ";
    private static final String NUMBER_PAGE_MAX = "SELECT COUNT(*) FROM company";
    private static final String DELETE_ONE_COMPANY = "DELETE FROM company where id = ? ;";
    private static final String DELETE_COMPUTER_LINKED = "DELETE FROM computer where company_id = ? ;";
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyDao.class);

    /**
     * Constructeur de CompanyDao.
     * @param dataSource DataSourceHikari
     */
    private CompanyDao(final DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public Optional<Company> find(final Long id) {
        Optional<Company> company = Optional.empty();
        try {
            company = Optional
                    .ofNullable(getJdbcTemplate().queryForObject(FIND_ONE_COMPANY, new MapperResulSetToCompany(), id));
        } catch (EmptyResultDataAccessException e) {
            // Nothing to espacially
        }
        return company;
    }

    @Override
    public Collection<Company> findAll() {
        return getJdbcTemplate().query(FIND_ALL_COMPANY, new Object[0], new MapperResulSetToCompany());
    }

    /**
     * Recupere le nombre de company en BD.
     * @return Nombre de company
     * @throws SQLException
     *             Si une erreur SQL apparait
     */
    public int numberOfElement() {
        return getJdbcTemplate().queryForObject(NUMBER_PAGE_MAX, Integer.class);
    }

    @Override
    public Pages<Company> findPerPage(int... pageWithoutNumberResult) {
        int page = pageWithoutNumberResult[0];
        if (page <= 1) {
            page = 1;
        }
        Pages<Company> pages = new Pages<Company>(page);
        pages.setPageMax(numberOfElement());
        ArrayList<Object> params = new ArrayList<>();
        params.add(pages.getNumberPerPageResult());
        params.add(pages.startResult());
        pages.getEntities()
                .addAll(getJdbcTemplate().query(FIND_COMPUTER_PAGE, params.toArray(), new MapperResulSetToCompany()));
        return pages;
    }

    @Override
    public boolean delete(Long id) {
        boolean res = false;
        getJdbcTemplate().update(DELETE_COMPUTER_LINKED, id);
        if (getJdbcTemplate().update(DELETE_ONE_COMPANY, id) == 1) {
            res = true;
        }
        return res;

    }
}
