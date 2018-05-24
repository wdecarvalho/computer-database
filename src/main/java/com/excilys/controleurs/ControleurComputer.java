package com.excilys.controleurs;

import static com.excilys.tags.TypeAlerte.ERROR;
import static com.excilys.tags.TypeAlerte.SUCCESS;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.excilys.dto.ComputerDTO;
import com.excilys.exception.ComputerException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.mapper.MapUtil;
import com.excilys.service.ServiceCompany;
import com.excilys.service.ServiceComputer;
import com.excilys.tags.TypeAlerte;

import static com.excilys.servlet.MessagetypeUser.ADD_ERROR_COMPUTER;
import static com.excilys.servlet.MessagetypeUser.ADD_SUCCESSFULL_COMPUTER;
import static com.excilys.servlet.RouteUrl.ADDCOMPUTER_JSP;
import static com.excilys.servlet.RouteUrl.DASHBOARD;

@Controller
public class ControleurComputer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ControleurComputer.class);

    private static final String COMPANYS = "companys";
    private static final String TYPE_MESSAGE = "typeMessage";
    private static final String MESSAGE_USER = "messageUser";

    @Autowired
    private ServiceCompany serviceCompany;

    @Autowired
    private ServiceComputer serviceComputer;

    /**
     * Redirige vers le formulaire d'ajout de computer.
     * @param model
     *            ModelAttribute
     * @return JSP
     */
    @GetMapping(path = "/computer")
    public String addComputerForm(final Model model) {
        return redirectToFormAdd(model);
    }

    /**
     * Valide le computer recu et demande a la couche de service de l'inserer en
     * base de données.
     * @param computerDTO
     *            ComputerDTO
     * @param result
     *            Resultat de la validation
     * @param model
     *            AttributeModel
     * @param redirectAttributes
     *            Permet d'envoyer des attributs a un autre controlleur
     * @return Redirection vers dashboard
     */
    @PostMapping(path = "/computer", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public String postComputer(@Valid @ModelAttribute ComputerDTO computerDTO, final BindingResult result,
            final Model model, final RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            final String errors = result.getAllErrors().stream().map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining("\n"));
            prepareErrorVisibleByUser(model, errors, ERROR);
        } else {
            try {
                if (serviceComputer.createComputer(MapUtil.computerDTOToComputer(computerDTO)).equals(-1L)) {
                    prepareErrorVisibleByUser(model, ADD_ERROR_COMPUTER.toString(), ERROR);
                } else {
                    return sendMessageToMainControlleur(redirectAttributes);
                }
            } catch (ComputerException | CompanyNotFoundException | DateTruncationException e) {
                prepareErrorVisibleByUser(model, e.getMessage(), ERROR);
            }
        }
        return redirectToFormAdd(model);
    }

    /**
     * SetUp le modele pour pouvoir creer un formulaire d'ajout et s'y rendre.
     * @param model
     *            ModelAttribute
     * @return jsp computer
     */
    private String redirectToFormAdd(final Model model) {
        model.addAttribute(COMPANYS, serviceCompany.getAll());
        return ADDCOMPUTER_JSP.toString();
    }

    /**
     * Remplie le model avec le type d'erreur et le message a afficher.
     * @param model
     *            ModelAttribute
     * @param errors
     *            Erreur a afficher
     * @param typeAlerte
     *            Type alerte du message
     */
    private void prepareErrorVisibleByUser(final Model model, final String errors, final TypeAlerte typeAlerte) {
        model.addAttribute(MESSAGE_USER, errors);
        model.addAttribute(TYPE_MESSAGE, typeAlerte);
    }

    /**
     * Redirigie vers le mainControlleur et lui notifie un success.
     * @param redirectAttributes
     *            Attribut a envoyé
     * @return Destination
     */
    private String sendMessageToMainControlleur(final RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute(MESSAGE_USER, ADD_SUCCESSFULL_COMPUTER.toString());
        redirectAttributes.addFlashAttribute(TYPE_MESSAGE, SUCCESS);
        return DASHBOARD.toString();
    }

}
