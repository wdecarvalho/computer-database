package com.excilys.controleurs;

import static com.excilys.tags.TypeAlerte.ERROR;
import static com.excilys.tags.TypeAlerte.INFO;
import static com.excilys.tags.TypeAlerte.SUCCESS;
import static com.excilys.tags.TypeAlerte.WARNING;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.excilys.dto.ComputerDTO;
import com.excilys.exception.computer.ComputerNotDeletedException;
import com.excilys.mapper.MapUtil;
import com.excilys.model.Computer;
import com.excilys.service.ServiceComputer;
import com.excilys.util.Pages;

import static com.excilys.controleurs.AttributeToSend.COMPUTERS;
import static com.excilys.controleurs.AttributeToSend.NB_COMPUTERS;
import static com.excilys.controleurs.AttributeToSend.LIMIT;
import static com.excilys.controleurs.AttributeToSend.PAGE_COURANTE;
import static com.excilys.controleurs.MessagetypeUser.DELETE_NO_COMPUTER_SELECTED;
import static com.excilys.controleurs.MessagetypeUser.DELETE_SUCCESSFULL_COMPUTER;
import static com.excilys.controleurs.MessagetypeUser.MESSAGE_USER;
import static com.excilys.controleurs.MessagetypeUser.TYPE_MESSAGE;
import static com.excilys.controleurs.RouteUrl.DASHBOARD;
import static com.excilys.controleurs.RouteUrl.DASHBOARD_JSP;

@Controller
@SessionAttributes(names = { "numberResult" }, types = { Integer.class })
public class ControleurMain {

    /*
     * Request params
     */
    private static final String SEARCH = "search";
    private static final String NUMBER_RESULT = "numberResult";
    private static final String PAGE = "page";

    @Autowired
    private ServiceComputer serviceComputer;

    @RequestMapping(path = "/dashboard", method = { RequestMethod.GET })
    public String accueil(final Model model,
            @RequestParam(name = PAGE, required = false, defaultValue = "1") final Integer page,
            @RequestParam(name = NUMBER_RESULT, required = false) Integer nbResult) {
        nbResult = createNbResultWithAllowedValue(model, nbResult);
        final Pages<Computer> pagesComputer = serviceComputer.findByPage(page, nbResult);
        return getComputerDTOAndPrintAccueil(model, pagesComputer);
    }

    @RequestMapping(path = "/dashboard", method = { RequestMethod.GET }, params = { SEARCH })
    public String accueil(final Model model,
            @RequestParam(name = PAGE, required = false, defaultValue = "1") final Integer page,
            @RequestParam(name = NUMBER_RESULT, required = false) Integer nbResult,
            @RequestParam(name = SEARCH) final String search) {
        nbResult = createNbResultWithAllowedValue(model, nbResult);
        final Pages<Computer> pagesComputer = serviceComputer.findByPagesComputer(search, page, nbResult);
        return getComputerDTOAndPrintAccueil(model, pagesComputer);
    }

    /**
     * Recupere les computer de la page et les transforme en computerDTO puis envoie
     * toutes les infos necessaire a la JSP.
     * @param model
     *            ModelAttribute
     * @param pagesComputer
     *            Pages<Computer>
     * @return JSP
     */
    private String getComputerDTOAndPrintAccueil(final Model model, final Pages<Computer> pagesComputer) {
        final List<ComputerDTO> computerDTOs = pagesComputer.getEntities().stream()
                .map(c -> MapUtil.computerToComputerDTO(c)).collect(Collectors.toList());
        model.addAttribute(COMPUTERS.toString(), computerDTOs);
        model.addAttribute(NB_COMPUTERS.toString(), pagesComputer.getMaxComputers());
        model.addAttribute(LIMIT.toString(), pagesComputer.getPageMax());
        model.addAttribute(PAGE_COURANTE.toString(), pagesComputer.getPageCourante());
        return DASHBOARD_JSP.toString();
    }

    /**
     * Demande a la couche de service de supprimer une liste de computers.
     * @param requestAttributes
     *            RequestAttribute
     * @param toDelete
     *            Set<Long> a supprimer
     * @return JSP
     */
    @PostMapping(path = "/dashboard/delete")
    public String deleteComputers(final RedirectAttributes requestAttributes,
            @RequestParam(name = "selection", required = true) Set<Long> toDelete) {
        if (toDelete.isEmpty()) {
            requestAttributes.addFlashAttribute(MESSAGE_USER.toString(), DELETE_NO_COMPUTER_SELECTED.toString());
            requestAttributes.addFlashAttribute(TYPE_MESSAGE.toString(), INFO);
        } else {
            try {
                if (serviceComputer.deleteComputer(MapUtil.setIdToStringListDatabase(toDelete))) {
                    requestAttributes.addFlashAttribute(MESSAGE_USER.toString(), DELETE_SUCCESSFULL_COMPUTER.toString());
                    requestAttributes.addFlashAttribute(TYPE_MESSAGE.toString(), SUCCESS);
                } else {
                    requestAttributes.addFlashAttribute(MESSAGE_USER.toString(),
                            new ComputerNotDeletedException(MESSAGE_USER.toString()).getMessage());
                    requestAttributes.addFlashAttribute(TYPE_MESSAGE.toString(), ERROR);
                }
            } catch (ComputerNotDeletedException e) {
                requestAttributes.addFlashAttribute(MESSAGE_USER.toString(), e.getMessage());
                requestAttributes.addFlashAttribute(TYPE_MESSAGE.toString(), WARNING);
            }
        }
        return DASHBOARD.toString();
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
            model.addAttribute(NUMBER_RESULT, nbResult);
        } else if (model.asMap().containsKey(NUMBER_RESULT)) {
            nbResult = (Integer) model.asMap().get(NUMBER_RESULT);
        } else {
            nbResult = 10;
        }
        return nbResult;
    }

}
