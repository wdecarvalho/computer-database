package com.excilys.controlleurs;

import java.util.List;
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
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.date.DateTruncationException;
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

    private ServiceCdbComputer serviceComputer;

    public ControleurComputer(ServiceCdbComputer serviceCdbComputer) {
        this.serviceComputer = serviceCdbComputer;
    }

    // @PreAuthorize("hasAuthority('USER')")
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

    @RequestMapping(path = "/computer/{id}", method = { RequestMethod.DELETE })
    public ResponseEntity<Object> deleteComputerById(@PathVariable("id") final Long id)
            throws ComputerException, CompanyNotFoundException {
        serviceComputer.getComputerDaoDetails(id);
        serviceComputer.deleteOne(id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(path = "/computer/{id}", method = { RequestMethod.GET })
    public ResponseEntity<Object> findComputerById(@PathVariable("id") final Long id) throws ComputerNotFoundException {
        final ComputerDTO computerDTO = MapUtil.computerToComputerDTO(serviceComputer.getComputerDaoDetails(id));
        return ResponseEntity.ok(computerDTO);
    }

    @RequestMapping(path = "/computer", method = {
            RequestMethod.POST }, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> createComputerById(@Valid @RequestBody final ComputerDTO computerDTO,
            final UriComponentsBuilder ucb)
            throws CompanyNotFoundException, DateTruncationException, ComputerException {
        final Long id = serviceComputer.save(MapUtil.computerDTOToComputer(computerDTO), false);
        return ResponseEntity.created(ucb.path("/{id}").buildAndExpand(id).toUri()).build();
    }

}
