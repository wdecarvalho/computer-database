package com.excilys.service.company;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.excilys.dao.CompanyDAO;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.model.Company;
import com.excilys.service.ServiceUtil;
import com.excilys.service.computer.ServiceCdbComputer;

@Service
@Transactional
public class ServiceCompany implements ServiceCdbCompany {

    @Autowired
    private ServiceCdbComputer computerService;

    @Autowired
    private CompanyDAO companyDAO;

    /**
     * Initialise le service.
     */

    private ServiceCompany() {

    }

    /*
     * Retrieve
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<Company> getAll() {
        return companyDAO.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Company> findByPage(final int... page) {
        final long nbComputer = getCountInDatabase();
        final int pageRequested = ServiceUtil.getTheRequestPageOrTheBestAppropriate(nbComputer, page);
        return companyDAO.findAll(new QPageRequest(pageRequested, NB_PAGE));
    }

    @Override
    @Transactional(readOnly = true)
    public Long getCountInDatabase() {
        return companyDAO.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExists(final Long id) {
        return companyDAO.existsById(id);
    }

    /*
     * Delete
     */

    @Transactional(rollbackFor = ComputerNotDeletedException.class)
    @Override
    public void deleteOne(final Long id) throws ComputerNotDeletedException {
        computerService.deleteByCompany(id);
        companyDAO.deleteById(id);

    }

}
