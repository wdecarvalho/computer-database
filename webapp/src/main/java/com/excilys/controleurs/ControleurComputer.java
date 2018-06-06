package com.excilys.controleurs;

import static com.excilys.controleurs.AttributeToSend.COMPANYS;
import static com.excilys.controleurs.MessagetypeUser.ADD_SUCCESSFULL_COMPUTER;
import static com.excilys.controleurs.MessagetypeUser.MESSAGE_USER;
import static com.excilys.controleurs.MessagetypeUser.TYPE_MESSAGE;
import static com.excilys.controleurs.MessagetypeUser.UPDATE_SUCCESSFULL_COMPUTER;
import static com.excilys.controleurs.RouteUrl.ADDCOMPUTER_JSP;
import static com.excilys.controleurs.RouteUrl.DASHBOARD;
import static com.excilys.controleurs.RouteUrl.EDITCOMPUTER_JSP;
import static com.excilys.controleurs.RouteUrl.ERROR_PAGE_409;
import static com.excilys.tags.TypeAlerte.ERROR;
import static com.excilys.tags.TypeAlerte.SUCCESS;

import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.excilys.dto.ComputerDTO;
import com.excilys.exception.ComputerException;
import com.excilys.exception.company.CompanyNotFoundException;
import com.excilys.exception.computer.ComputerNotFoundException;
import com.excilys.exception.date.DateTruncationException;
import com.excilys.mapper.MapUtil;
import com.excilys.service.company.ServiceCdbCompany;
import com.excilys.service.computer.ServiceCdbComputer;
import com.excilys.tags.TypeAlerte;

@PreAuthorize("hasAuthority('ADMIN')")
@Controller
public class ControleurComputer {

    private static final String COMPUTER = "computer";

    @Autowired
    private ServiceCdbCompany serviceCompany;

    @Autowired
    private ServiceCdbComputer serviceComputer;

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
     * @param redirectAttributes
     *            Permet d'envoyer des attributs a un autre controlleur
     * @return Redirection vers dashboard
     */
    @PostMapping(path = "/computer", consumes = { MediaType.APPLICATION_FORM_URLENCODED_VALUE })
    public String postComputer(@Valid @ModelAttribute ComputerDTO computerDTO, final BindingResult result,
            final RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            resultErrorToStringAndSendItWithTypeError(result, redirectAttributes);
        } else {
            try {
                serviceComputer.save(MapUtil.computerDTOToComputer(computerDTO), false);
                return sendMessageToMainControlleur(redirectAttributes, ADD_SUCCESSFULL_COMPUTER.toString(), SUCCESS);
            } catch (CompanyNotFoundException | DateTruncationException | ComputerException e) {
                setMessageErrorAndIsTypeToModel(redirectAttributes, e.getMessage(), ERROR);
            }
        }
        return RouteUrl.ADDCOMPUTER.toString();
    }

    /**
     * Redirige vers le formulaire d'edition d'un computer.
     * @param model
     *            ModelAttribute
     * @param id
     *            ID du computer a editer
     * @param redirectAttributes
     *            Attribut a envoyer a un autre controlleur
     * @return JSP
     */
    @GetMapping(path = "/computer/{id}")
    public String updateComputerForm(final Model model, @PathVariable(name = "id", required = true) final Long id,
            final RedirectAttributes redirectAttributes) {
        try {
            final ComputerDTO computerDTO = MapUtil.computerToComputerDTO(serviceComputer.getComputerDaoDetails(id));
            return redirectToFormEdit(model, computerDTO);
        } catch (ComputerNotFoundException e) {
            return sendMessageToMainControlleur(redirectAttributes, e.getMessage(), ERROR);
        }
    }

    /**
     * Valide le computeDTO et demande a la couche de service de mettre a jour le
     * computer.
     * @param computerDTO
     *            ComputerDTO
     * @param result
     *            Resultat de la validation
     * @param model
     *            ModelAttribute
     * @param redirectAttributes
     *            Attribut a envoyer a un autre controlleur
     * @param id
     *            ID dans l'URL du computer qui doit être modifié.
     * @return JSP
     */
    @PostMapping(path = "/computer/{idComputer}")
    public String updateComputer(@Valid @ModelAttribute ComputerDTO computerDTO, final BindingResult result,
            final Model model, final RedirectAttributes redirectAttributes,
            @PathVariable(name = "idComputer") final Long id) {
        if (!id.equals(computerDTO.getId())) {
            return ERROR_PAGE_409.toString();
        }
        if (result.hasErrors()) {
            resultErrorToStringAndSendItWithTypeError(result, redirectAttributes);
        } else {
            try {
                serviceComputer.update(MapUtil.computerDTOToComputer(computerDTO), false);
                return sendMessageToMainControlleur(redirectAttributes, UPDATE_SUCCESSFULL_COMPUTER.toString(),
                        SUCCESS);
            } catch (ComputerException | DateTruncationException | CompanyNotFoundException e) {
                setMessageErrorAndIsTypeToModel(redirectAttributes, e.getMessage(), ERROR);
            }
        }
        return String.format(RouteUrl.EDITCOMPUTER.toString(), id);
    }

    /**
     * Creer une string a partir de tout les message d'erreur de validation et
     * prepare leur affichage.
     * @param result
     *            Resultat de la validation
     * @param redirectAttributes
     *            redirectAttributes
     */
    private void resultErrorToStringAndSendItWithTypeError(final BindingResult result,
            final RedirectAttributes redirectAttributes) {
        final String errors = result.getAllErrors().stream().map(e -> e.getDefaultMessage())
                .collect(Collectors.joining("\n"));
        setMessageErrorAndIsTypeToModel(redirectAttributes, errors, ERROR);
    }

    /**
     * SetUp le modele pour pouvoir creer un formulaire d'ajout et s'y rendre.
     * @param model
     *            ModelAttribute
     * @return jsp computer
     */
    private String redirectToFormAdd(final Model model) {
        model.addAttribute(COMPANYS.toString(), serviceCompany.getAll());
        return ADDCOMPUTER_JSP.toString();
    }

    /**
     * SetUp le modele pour pouvoir creer un formulaire d'ajout et s'y rendre.
     * @param model
     *            ModelAttribute
     * @param computerDTO
     *            a afficher
     * @return jsp computer
     */
    private String redirectToFormEdit(final Model model, final ComputerDTO computerDTO) {
        model.addAttribute(COMPUTER, computerDTO);
        model.addAttribute(COMPANYS.toString(), serviceCompany.getAll());
        return EDITCOMPUTER_JSP.toString();
    }

    /**
     * Remplie le model avec le type d'erreur et le message a afficher.
     * @param redirectAttributes
     *            redirectAttributes
     * @param errors
     *            Erreur a afficher
     * @param typeAlerte
     *            Type alerte du message
     */
    private void setMessageErrorAndIsTypeToModel(final RedirectAttributes redirectAttributes, final String errors,
            final TypeAlerte typeAlerte) {
        redirectAttributes.addFlashAttribute(MESSAGE_USER.toString(), errors);
        redirectAttributes.addFlashAttribute(TYPE_MESSAGE.toString(), typeAlerte);
    }

    /**
     * Redirigie vers le mainControlleur et lui notifie un success.
     * @param redirectAttributes
     *            Attribut a envoyé
     * @param message
     *            Message a envoyé
     * @param typeAlerte
     *            type du message
     * @return Destination
     */
    private String sendMessageToMainControlleur(final RedirectAttributes redirectAttributes, final String message,
            final TypeAlerte typeAlerte) {
        redirectAttributes.addFlashAttribute(MESSAGE_USER.toString(), message);
        redirectAttributes.addFlashAttribute(TYPE_MESSAGE.toString(), typeAlerte);
        return DASHBOARD.toString();
    }

}