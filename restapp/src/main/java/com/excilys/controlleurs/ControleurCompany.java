package com.excilys.controlleurs;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.model.Company;
import com.excilys.service.company.ServiceCdbCompany;

@RestController
public class ControleurCompany {

    /*
     * Request params
     */
    private static final String SEARCH = "search";
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
     * ID de la company souhaité
     * @return ResponseEntity<Company> 200 / 204
     * Si l'ordinateur n'est pas trouvé.
     * @throws CompanyNotFoundException 
     */
    @RequestMapping(path = "/company/{id}", method = { RequestMethod.GET })
    public ResponseEntity<Company> findComputerById(@PathVariable("id") final Long id) throws CompanyNotFoundException {
        final Company company = serviceCompany.findOneById(id);
        return ResponseEntity.ok(company);
    }
    //
    // /**
    // * Ajoute un computer en base de donnée.
    // * @param computerDTO
    // * Computer à ajouter
    // * @param ucb
    // * UriComponentsBuilder
    // * @return Path location 201 or ???
    // * @throws CompanyNotFoundException
    // * Si la company n'existe pas
    // * @throws DateTruncationException
    // * Si un probleme avec la date
    // * @throws ComputerException
    // * Si une exception intervient au niveau du computer
    // */
    // @RequestMapping(path = "/computer", method = {
    // RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    // public ResponseEntity<Object> createComputerById(@Valid @RequestBody final
    // ComputerDTO computerDTO,
    // final UriComponentsBuilder ucb)
    // throws CompanyNotFoundException, DateTruncationException, ComputerException {
    // computerDTO.setId(null);
    // final Long id =
    // serviceComputer.save(MapUtil.computerDTOToComputer(computerDTO), false);
    // return
    // ResponseEntity.created(ucb.path("/{id}").buildAndExpand(id).toUri()).build();
    // }
    //
    // @RequestMapping(path = "/computer/{id}", method = { RequestMethod.PUT })
    // public ResponseEntity<ComputerDTO> updateComputerById(@PathVariable("id")
    // final Long id,
    // @Valid @RequestBody final ComputerDTO computerDTO)
    // throws CompanyNotFoundException, DateTruncationException, ComputerException {
    // final Long computerID = computerDTO.getId();
    // if (!id.equals(computerID)) {
    // throw new ConflictException();
    // }
    // serviceComputer.getComputerDaoDetails(id); // check is the computer exists
    // final Computer c =
    // serviceComputer.update(MapUtil.computerDTOToComputer(computerDTO), false);
    // return ResponseEntity.ok(MapUtil.computerToComputerDTO(c));
    // }
    //
    // /**
    // * Supprime un computer en base de donnée.
    // * @param id
    // * ID du computer à supprimer
    // * @return 200 si supprimé ou ???
    // * @throws ComputerException
    // * Si une exception intervient au niveau du computer
    // * @throws CompanyNotFoundException
    // * Si la companie n'existe pas
    // */
    // @RequestMapping(path = "/computer/{id}", method = { RequestMethod.DELETE })
    // public ResponseEntity<Object> deleteComputerById(@PathVariable("id") final
    // Long id)
    // throws ComputerException, CompanyNotFoundException {
    // serviceComputer.getComputerDaoDetails(id);
    // serviceComputer.deleteOne(id);
    // return ResponseEntity.ok().build();
    // }

}
