package com.excilys.service.computer;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import com.excilys.dao.ComputerDAO;
import com.excilys.exception.ComputerException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.model.Computer;
import com.excilys.service.ServiceUtil;
import com.excilys.service.company.ServiceCdbCompany;
import com.excilys.validation.ComputerValidation;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

@Service
public class ServiceComputer implements ServiceCdbComputer {
    private static final String COMPANY = "company";

    private static final String ERROR_CODE_DATE_SQL = "22001";

    private ComputerDAO computerDao;

    private ServiceCdbCompany serviceCompany;

    /**
     * Constructeur de ServiceComputer [Spring].
     */

    private ServiceComputer() {
    }

    /*
     * Retrieve
     */

    @Override
    public Collection<Computer> getAll() {
        return computerDao.findAll();
    }

    @Override
    public Computer getComputerDaoDetails(final Long id) throws ComputerNotFoundException {
        return computerDao.findById(id).orElseThrow(() -> new ComputerNotFoundException("" + id));
    }

    @Override
    public Page<Computer> findByPage(int... pageAndNumberResult) {
        int pageRequested = ServiceUtil.verifyPageRequestedIsValidOrPutOne(pageAndNumberResult[0]);
        if (pageAndNumberResult.length > 1) {
            return ServiceUtil.findObjectInDatabaseByPage(computerDao,pageRequested, pageAndNumberResult[1]);
        } else {
            return ServiceUtil.findObjectInDatabaseByPage(computerDao,pageRequested, NB_PAGE);
        }
    }

    private Page<Computer> findComputerSearchedInDatabaseByPage(final String search, final int pageRequested,
            final int numberResult) {
        Page<Computer> pagecomputer = computerDao.findByNameContainingOrCompanyNameContainingOrderByName(search, search,
                new QPageRequest(pageRequested, numberResult));
        if (pagecomputer.getTotalPages() > pageRequested) {
            return pagecomputer;
        } else {
            return computerDao.findByNameContainingOrCompanyNameContainingOrderByName(search, search, new QPageRequest(
                    pagecomputer.getTotalPages() == 0 ? 0 : pagecomputer.getTotalPages() - 1, numberResult));
        }
    }

    @Override
    public Page<Computer> findByPagesSearch(final String search, int... pageAndNumberResult) {
        final int pageRequested = ServiceUtil.verifyPageRequestedIsValidOrPutOne(pageAndNumberResult[0]);
        if (pageAndNumberResult.length > 1) {
            return findComputerSearchedInDatabaseByPage(search, pageRequested, pageAndNumberResult[1]);
        } else {
            return findComputerSearchedInDatabaseByPage(search, pageRequested, NB_PAGE);
        }
    }

    @Override
    public Long getCountInDatabase() {
        return computerDao.count();
    }

    @Override
    public Long getCountSearched(final String search) {
        return computerDao.countByNameContainingOrCompanyNameContaining(search, search);
    }

    /*
     * Create
     */
    @Override
    public Long save(Computer c, boolean validation)
            throws CompanyNotFoundException, DateTruncationException, ComputerException {
        Long idCreatedLong = -1L; // Never used
        try {
            if (validation) {
                ComputerValidation.validateComputerIntegrityAndDate(c);
            } else {
                ComputerValidation.dateIntroMinorThanDateDiscon(c.getIntroduced(), c.getDiscontinued());
            }
            idCreatedLong = computerDao.save(c).getId();
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause() instanceof SQLIntegrityConstraintViolationException) {
                if (e.getMostSpecificCause().getMessage().contains(COMPANY)) {
                    throw new CompanyNotFoundException(c.getCompany().getId().toString());
                }
            }
            if (e.getMostSpecificCause() instanceof MysqlDataTruncation) {
                if (((MysqlDataTruncation) e.getMostSpecificCause()).getSQLState().equals(ERROR_CODE_DATE_SQL)) {
                    throw new DateTruncationException();
                }
            }
        }
        return idCreatedLong;

    }

    /*
     * Update
     */

    @Override
    public Computer update(final Computer c, boolean validation)
            throws CompanyNotFoundException, DateTruncationException, ComputerException {
        Computer computer = null; // never used
        try {
            if (validation) {
                ComputerValidation.validateComputerIntegrityAndVerifyPresenceId(c);
            } else {
                ComputerValidation.validateComputerAndVerifyPresenceId(c);
            }
            computer = computerDao.save(c);
        } catch (DataIntegrityViolationException e) {
            if (e.getMostSpecificCause() instanceof SQLIntegrityConstraintViolationException) {
                if (e.getMostSpecificCause().getMessage().contains(COMPANY)) {
                    throw new CompanyNotFoundException(c.getCompany().getId().toString());
                }
            }
            if (e.getMostSpecificCause() instanceof MysqlDataTruncation) {
                if (((MysqlDataTruncation) e.getMostSpecificCause()).getSQLState().equals(ERROR_CODE_DATE_SQL)) {
                    throw new DateTruncationException();
                }
            }
        }
        return computer;

    }

    /*
     * Delete
     */

    @Override
    public void deleteOne(final Long id) throws ComputerNotDeletedException {
        computerDao.deleteById(id);
    }

    @Override
    public boolean deleteMulitple(Iterable<Long> computersToDelete) throws ComputerNotDeletedException {
        boolean res = false;
        final Long nbComputerDelete = computerDao.deleteByIdIn(computersToDelete);
        if (nbComputerDelete.equals(computersToDelete.spliterator().getExactSizeIfKnown())) {
            res = true;
        } else if (nbComputerDelete > 0) {
            throw new ComputerNotDeletedException();
        }
        return res;
    }

    @Override
    public void deleteByCompany(final Long companyId) throws ComputerNotDeletedException, CompanyNotFoundException {
        final String name = serviceCompany.getCompanyNameById(companyId);
        final Long nbComputer = computerDao.countByCompanyName(name);
        if (nbComputer != 0) {
            final Long nbComputerDelete = computerDao.deleteByCompanyId(companyId);
            if (nbComputerDelete == 0) {
                throw new ComputerNotDeletedException("");
            }
        }
    }

    /**
     * @param computerDao
     *            the computerDao to set
     */
    @Autowired
    public void setComputerDao(ComputerDAO computerDao) {
        this.computerDao = computerDao;
    }

    /**
     * @param serviceCompany
     *            the serviceCompany to set
     */
    @Autowired
    public void setServiceCompany(ServiceCdbCompany serviceCompany) {
        this.serviceCompany = serviceCompany;
    }

}
