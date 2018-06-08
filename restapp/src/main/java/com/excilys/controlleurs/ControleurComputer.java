package com.excilys.controlleurs;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.excilys.dto.ComputerDTO;
import com.excilys.exception.ComputerException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.exceptions.ConflictException;
import com.excilys.exceptions.NoContentFoundException;
import com.excilys.mapper.MapUtil;
import com.excilys.model.Computer;
import com.excilys.service.computer.ServiceCdbComputer;

@RestController
public class ControleurComputer {

    /*
     * Request params
     */
    private static final String SEARCH = "search";
    private static final String NUMBER_RESULT = "numberResult";
    private static final String PAGE = "page";
    private static final String DELETE = "delete";

    private ServiceCdbComputer serviceComputer;

    public ControleurComputer(ServiceCdbComputer serviceCdbComputer) {
        this.serviceComputer = serviceCdbComputer;
    }

    // @PreAuthorize("hasAuthority('USER')")
    /**
     * Recupere les computers par pagination, si pas de page définit retourne la
     * premiere.
     * @param page
     *            1 par défaut
     * @param nbResult
     *            10 par défaut
     * @return Page<ComputerDTO> 200
     */
    @RequestMapping(path = "/computer", method = {
            RequestMethod.GET }, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Page<ComputerDTO>> getComputerByPage(
            @RequestParam(name = PAGE, required = false, defaultValue = "1") final Integer page,
            @RequestParam(name = NUMBER_RESULT, required = false, defaultValue = "10") Integer nbResult) {
        final Page<Computer> pagesComputer = serviceComputer.findByPage(page, nbResult);
        List<ComputerDTO> computerDTOs = pagesComputer.getContent().stream().map(MapUtil::computerToComputerDTO)
                .collect(Collectors.toList());
        Page<ComputerDTO> pagesComputerDtos = new PageImpl<>(computerDTOs, pagesComputer.getPageable(),
                pagesComputer.getTotalElements());
        return ResponseEntity.ok(pagesComputerDtos);
    }

    /**
     * Recupere les computers recherchés par pagination.
     * @param page
     *            page 1 par défaut
     * @param nbResult
     *            10 par défaut
     * @param search
     *            Mot clé de recherche
     * @return Page<ComputerDTO> 200 / 204
     * @throws NoContentFoundException
     *             Si aucun ordinateur ou aucune company n'est trouvé
     */
    @RequestMapping(path = "/computer", method = { RequestMethod.GET }, params = { SEARCH })
    public ResponseEntity<Page<ComputerDTO>> getComputerBySearchByPage(
            @RequestParam(name = PAGE, required = false, defaultValue = "1") final Integer page,
            @RequestParam(name = NUMBER_RESULT, required = false, defaultValue = "10") Integer nbResult,
            @RequestParam(name = SEARCH) final String search) throws NoContentFoundException {
        final Page<Computer> pagesComputer = serviceComputer.findByPagesSearch(search, page, nbResult);
        if (pagesComputer.getNumberOfElements() == 0) {
            throw new NoContentFoundException();
        }
        List<ComputerDTO> computerDTOs = pagesComputer.getContent().stream().map(MapUtil::computerToComputerDTO)
                .collect(Collectors.toList());
        Page<ComputerDTO> pagesComputerDtos = new PageImpl<>(computerDTOs, pagesComputer.getPageable(),
                pagesComputer.getTotalElements());
        return ResponseEntity.ok(pagesComputerDtos);
    }

    /**
     * Recupere le computer d'id précisé.
     * @param id
     *            ID du computer souhaité
     * @return ResponseEntity<ComputerDTO> 200 / 204
     * @throws ComputerNotFoundException
     *             Si l'ordinateur n'est pas trouvé.
     */
    @RequestMapping(path = "/computer/{id}", method = { RequestMethod.GET })
    public ResponseEntity<ComputerDTO> findComputerById(@PathVariable("id") final Long id)
            throws ComputerNotFoundException {
        final ComputerDTO computerDTO = MapUtil.computerToComputerDTO(serviceComputer.getComputerDaoDetails(id));
        return ResponseEntity.ok(computerDTO);
    }

    /**
     * Ajoute un computer en base de donnée.
     * @param computerDTO
     *            Computer à ajouter
     * @param ucb
     *            UriComponentsBuilder
     * @return Path location 201 or ???
     * @throws CompanyNotFoundException
     *             Si la company n'existe pas
     * @throws DateTruncationException
     *             Si un probleme avec la date
     * @throws ComputerException
     *             Si une exception intervient au niveau du computer
     */
    @RequestMapping(path = "/computer", method = {
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> createComputerById(@Valid @RequestBody final ComputerDTO computerDTO,
            final UriComponentsBuilder ucb)
            throws CompanyNotFoundException, DateTruncationException, ComputerException {
        computerDTO.setId(null);
        final Long id = serviceComputer.save(MapUtil.computerDTOToComputer(computerDTO), false);
        return ResponseEntity.created(ucb.path("/{id}").buildAndExpand(id).toUri()).build();
    }

    @RequestMapping(path = "/computer/{id}", method = { RequestMethod.PUT })
    public ResponseEntity<ComputerDTO> updateComputerById(@PathVariable("id") final Long id,
            @Valid @RequestBody final ComputerDTO computerDTO)
            throws CompanyNotFoundException, DateTruncationException, ComputerException {
        final Long computerID = computerDTO.getId();
        if (!id.equals(computerID)) {
            throw new ConflictException();
        }
        serviceComputer.getComputerDaoDetails(id); // check is the computer exists
        final Computer c = serviceComputer.update(MapUtil.computerDTOToComputer(computerDTO), false);
        return ResponseEntity.ok(MapUtil.computerToComputerDTO(c));
    }

    /**
     * Supprime un computer en base de donnée.
     * @param id
     *            ID du computer à supprimer
     * @return 200 si supprimé ou ???
     * @throws ComputerException
     *             Si une exception intervient au niveau du computer
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    @RequestMapping(path = "/computer/{id}", method = { RequestMethod.DELETE })
    public ResponseEntity<Object> deleteComputerById(@PathVariable("id") final Long id)
            throws ComputerException, CompanyNotFoundException {
        serviceComputer.getComputerDaoDetails(id);
        serviceComputer.deleteOne(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Supprime plusieurs computer en base de donnée.
     * @param id
     *            ID du computer à supprimer
     * @return 200 si supprimé ou ???
     * @throws ComputerNotDeletedException 
     * @throws ComputerException
     *             Si une exception intervient au niveau du computer
     * @throws CompanyNotFoundException
     *             Si la companie n'existe pas
     */
    @RequestMapping(path = "/computer", method = { RequestMethod.DELETE }, params = DELETE)
    public ResponseEntity<Object> deleteComputerById(@RequestParam(name = DELETE, required = true) final Set<Long> id) throws ComputerNotDeletedException {
        if(!serviceComputer.deleteMulitple(id)) {
            throw new ComputerNotDeletedException(DELETE);
        }
        return ResponseEntity.ok().build();
    }

}
