package com.excilys.controlleurs;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.model.Company;
import com.excilys.service.company.ServiceCdbCompany;

@RestController
public class ControleurCompany {

    /*
     * Request params
     */
    private static final String NUMBER_RESULT = "numberResult";
    private static final String PAGE = "page";

    private ServiceCdbCompany serviceCompany;

    public ControleurCompany(ServiceCdbCompany serviceCdbCompany) {
        this.serviceCompany = serviceCdbCompany;
    }

    // @PreAuthorize("hasAuthority('USER')")
    /**
     * Recupere les companies par pagination, si pas de page définit retourne la
     * premiere.
     * @param page
     *            1 par défaut
     * @param nbResult
     *            10 par défaut
     * @return Page<Company> 200
     */
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(path = "/company", method = { RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<Company>> getComputerByPage(
            @RequestParam(name = PAGE, required = false, defaultValue = "1") final Integer page,
            @RequestParam(name = NUMBER_RESULT, required = false, defaultValue = "10") Integer nbResult) {
        final Page<Company> pagesCompanys = serviceCompany.findByPage(page, nbResult);
        return ResponseEntity.ok(pagesCompanys);
    }

    /**
     * Recupere la company d'id précisé.
     * @param id
     *            ID de la company souhaité
     * @return ResponseEntity<Company> 200 / 204 Si l'ordinateur n'est pas trouvé.
     * @throws CompanyNotFoundException
     */
    @PreAuthorize("hasAuthority('USER')")
    @RequestMapping(path = "/company/{id}", method = { RequestMethod.GET })
    public ResponseEntity<Company> findComputerById(@PathVariable("id") final Long id) throws CompanyNotFoundException {
        final Company company = serviceCompany.findOneById(id);
        return ResponseEntity.ok(company);
    }
    
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(path="/company",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> addCompany(@Valid @RequestBody final Company company,final UriComponentsBuilder ucb){
        final Long id = serviceCompany.save(company);
        return ResponseEntity.created(ucb.path("/{id}").buildAndExpand(id).toUri()).build();
    }
    

    /**
     * Supprime une company de la base de donnée.
     * @param id ID de la company à supprimer
     * @return 200
     * @throws ComputerNotDeletedException Si un probleme de suppresion intervient avec les computers
     * @throws CompanyNotFoundException Si la companie n'existe pas
     */
    @PreAuthorize("hasAuthority('ADMIN')")
    @RequestMapping(path = "/company/{id}", method = { RequestMethod.DELETE })
    public ResponseEntity<Object> deleteCompanyById(@PathVariable(name = "id") final Long id) throws ComputerNotDeletedException, CompanyNotFoundException {
        serviceCompany.deleteOne(id);
        return ResponseEntity.ok().build();
    }

}
