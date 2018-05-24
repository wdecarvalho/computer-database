package com.excilys.controleurs;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.excilys.dto.ComputerDTO;
import com.excilys.mapper.MapUtil;
import com.excilys.model.Computer;
import com.excilys.service.ServiceComputer;
import com.excilys.util.Pages;

import static com.excilys.servlet.RouteUrl.DASHBOARD_JSP;

@Controller
@SessionAttributes(names = { "numberResult" }, types = { Integer.class })
public class ControleurMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControleurMain.class);
    private static final String COMPUTERS = "computers";
    private static final String NB_COMPUTERS = "nbComputers";
    private static final String LIMIT = "limit";
    private static final String PAGE_COURANTE = "pageCourante";

    @Autowired
    private ServiceComputer serviceComputer;

    @RequestMapping(path = "/dashboard", method = { RequestMethod.GET })
    public String accueil(final Model model,
            @RequestParam(name = "page", required = false, defaultValue = "1") final Integer page,
            @RequestParam(name = "numberResult", required = false) Integer nbResult) {
        nbResult = createNbResultWithAllowedValue(model, nbResult);
        final Pages<Computer> pagesComputer = serviceComputer.findByPage(page, nbResult);
        final List<ComputerDTO> computerDTOs = pagesComputer.getEntities().stream()
                .map(c -> MapUtil.computerToComputerDTO(c)).collect(Collectors.toList());
        model.addAttribute(COMPUTERS, computerDTOs);
        model.addAttribute(NB_COMPUTERS, pagesComputer.getMaxComputers());
        model.addAttribute(LIMIT, pagesComputer.getPageMax());
        model.addAttribute(PAGE_COURANTE, pagesComputer.getPageCourante());
        return DASHBOARD_JSP.toString();
    }

    @RequestMapping(path = "/dashboard", method = { RequestMethod.GET }, params = { "search" })
    public String accueil(final Model model,
            @RequestParam(name = "page", required = false, defaultValue = "1") final Integer page,
            @RequestParam(name = "numberResult", required = false) Integer nbResult,
            @RequestParam(name = "search") final String search) {
        nbResult = createNbResultWithAllowedValue(model, nbResult);
        final Pages<Computer> pagesComputer = serviceComputer.findByPagesComputer(search, page, nbResult);
        final List<ComputerDTO> computerDTOs = pagesComputer.getEntities().stream()
                .map(c -> MapUtil.computerToComputerDTO(c)).collect(Collectors.toList());
        model.addAttribute(COMPUTERS, computerDTOs);
        model.addAttribute(NB_COMPUTERS, pagesComputer.getMaxComputers());
        model.addAttribute(LIMIT, pagesComputer.getPageMax());
        model.addAttribute(PAGE_COURANTE, pagesComputer.getPageCourante());
        return DASHBOARD_JSP.toString();
    }

    /**
     * @param model
     *            ModelAttribute
     * @param nbResult
     *            NbResult souhaité par l'utilisateur
     * @return nbResult 10 50 100
     */
    private Integer createNbResultWithAllowedValue(final Model model, Integer nbResult) {
        final boolean valeurPossible = nbResult != null && (nbResult == 10 || nbResult == 50 || nbResult == 100);
        if (valeurPossible) {
            model.addAttribute("numberResult", nbResult);
        } else if (model.asMap().containsKey("numberResult")) {
            nbResult = (Integer) model.asMap().get("numberResult");
        } else {
            nbResult = 10;
        }
        return nbResult;
    }

}
