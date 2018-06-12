package com.excilys.service.company;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.excilys.dao.CompanyDAO;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.model.Company;
import com.excilys.service.ServiceUtil;
import com.excilys.service.computer.ServiceCdbComputer;

@Service
public class ServiceCompany implements ServiceCdbCompany {

    private ServiceCdbComputer computerService;

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
    public Collection<Company> getAll() {
        return companyDAO.findAll();
    }

    @Override
    public Page<Company> findByPage(final int... page) { //FIXME eeeee
        int pageRequested = ServiceUtil.verifyPageRequestedIsValidOrPutOne(page[0]);
        if (page.length > 1) {
            return ServiceUtil.findObjectInDatabaseByPage(companyDAO,pageRequested, page[1]);
        } else {
            return ServiceUtil.findObjectInDatabaseByPage(companyDAO,pageRequested, NB_PAGE);
        }
    }

    @Override
    public Long getCountInDatabase() {
        return companyDAO.count();
    }

    @Override
    public boolean isExists(final Long id) {
        return companyDAO.existsById(id);
    }

    @Override
    public String getCompanyNameById(final Long id) throws CompanyNotFoundException {
        return companyDAO.findById(id).orElseThrow(() -> new CompanyNotFoundException(id.toString())).getName();
    }
    
    @Override
    public Company findOneById(Long id) throws CompanyNotFoundException {
        return companyDAO.findById(id).orElseThrow(() -> new CompanyNotFoundException(id+""));
    }
    
    /*
     * Create
     */
    
    @Override
    public Long save(Company company) {
        return companyDAO.save(company).getId();
    }

    /*
     * Delete
     */

    @Override
    public void deleteOne(final Long id) throws ComputerNotDeletedException, CompanyNotFoundException {
        computerService.deleteByCompany(id);
        companyDAO.deleteById(id);
    }

    /**
     * @param computerService the computerService to set
     */
    @Autowired
    public void setComputerService(ServiceCdbComputer computerService) {
        this.computerService = computerService;
    }

    /**
     * @param companyDAO the companyDAO to set
     */
    @Autowired
    public void setCompanyDAO(CompanyDAO companyDAO) {
        this.companyDAO = companyDAO;
    }


}
