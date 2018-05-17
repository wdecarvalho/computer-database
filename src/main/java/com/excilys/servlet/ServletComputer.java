package com.excilys.servlet;

import static com.excilys.tags.TypeAlerte.ERROR;
import static com.excilys.tags.TypeAlerte.SUCCESS;
import static com.excilys.tags.TypeAlerte.WARNING;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.excilys.dto.ComputerDTO;
import com.excilys.exception.CompanyNotFoundException;
import com.excilys.exception.ComputerException;
import com.excilys.exception.ComputerNameNotPresentException;
import com.excilys.exception.ComputerNotFoundException;
import com.excilys.exception.DateTruncationException;
import com.excilys.exception.LocalDateExpectedException;
import com.excilys.mapper.MapUtil;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.service.ServiceCompany;
import com.excilys.service.ServiceComputer;

import static com.excilys.servlet.MessagetypeUser.ADD_ERROR_COMPUTER;
import static com.excilys.servlet.MessagetypeUser.ADD_SUCCESSFULL_COMPUTER;
import static com.excilys.servlet.MessagetypeUser.DELETE_NO_COMPUTER_SELECTED;
import static com.excilys.servlet.MessagetypeUser.DELETE_NO_VALID_ID;
import static com.excilys.servlet.MessagetypeUser.DELETE_SUCCESSFULL_COMPUTER;
import static com.excilys.servlet.MessagetypeUser.UPDATE_SUCCESSFULL_COMPUTER;

import static com.excilys.servlet.RouteUrl.DASHBOARD_SERVLET;
import static com.excilys.servlet.RouteUrl.ADDCOMPUTER_JSP;
import static com.excilys.servlet.RouteUrl.ERROR_PAGE_404;
import static com.excilys.servlet.RouteUrl.EDITCOMPUTER_JSP;

import static com.excilys.servlet.ActionUtilisateur.ADDFORM;
import static com.excilys.servlet.ActionUtilisateur.EDITFORM;

@WebServlet(urlPatterns = { "/computer" })
public class ServletComputer extends HttpServlet {
    private static final String UL_CLOSE = "</ul>";
    private static final String LI_CLOSE = "</li>";
    private static final String LI = "<li>";
    private static final String DISCONTINUED = "discontinued";
    private static final String INTRODUCED = "introduced";
    private static final String UL = "<ul>";
    private static final String COMPANY_ID = "companyId";
    private static final String COMPUTER_NAME = "computerName";
    private static final String ADD_COMPUTER = "addComputer";
    private static final String COMPUTER = "computer";
    private static final String ID = "id";
    private static final String COMPUTER_ID = "computerID";
    private static final String SELECTION = "selection";
    private static final String EDIT_COMPUTER = "editComputer";
    private static final String TYPE_MESSAGE = "typeMessage";
    private static final String COMPANYS = "companys";
    private static final String ACTION = "action";
    private static final String MESSAGE_USER = "messageUser";
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Logger.class);

    @Autowired
    private ServiceComputer serviceComputer;

    @Autowired
    private ServiceCompany serviceCompany;

    /**
     * Init les service de CDB.
     * @throws ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, config.getServletContext());
    }

    /**
     * Point d'entrée des requetes POST.
     * @param req
     *            Requete
     * @param res
     *            Response
     * @throws IOException
     *             Si un problem en entrée ou en sortie apparait
     * @throws ServletException
     *             Exception généré par la servlet
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        doGet(req, res);
    }

    /**
     * Point d'entrée des requetes GET.
     * @param req
     *            Requete
     * @param res
     *            Response
     * @throws IOException
     *             Si un problem en entrée ou en sortie apparait
     * @throws ServletException
     *             Exception généré par la servlet
     */
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        final String action = req.getParameter(ACTION);
        if (action != null) {
            switch (ActionUtilisateur.getEnum(action)) {
            case ADDFORM:
                req.setAttribute(COMPANYS, serviceCompany.getAll());
                req.getRequestDispatcher(ADDCOMPUTER_JSP.toString()).forward(req, res);
                break;
            case ADD:
                addComputerToDatabase(req, res);
                break;
            case EDITFORM:
                goToFormEditComputer(req, res);
                break;
            case EDIT:
                editComputerInDatabase(req, res);
                break;
            case DELETE:
                deleteComputerInDatabase(req, res);
                break;
            default:
                req.getRequestDispatcher(ERROR_PAGE_404.toString()).forward(req, res);
            }
        } else {
            req.getRequestDispatcher(ERROR_PAGE_404.toString()).forward(req, res);
        }
    }

    /**
     * Supprime une liste de computer en base de données si les ID sont valides et
     * sont présents.
     * @param req
     *            HttpServletRequest
     * @param res
     *            HttpServletResponse
     * @throws ServletException
     *             ServletException
     * @throws IOException
     *             IOException
     */
    private void deleteComputerInDatabase(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        String[] idsComputer = req.getParameter(SELECTION).split(",");
        if ("".equals(idsComputer[0])) {
            req.setAttribute(MESSAGE_USER, DELETE_NO_COMPUTER_SELECTED.toString());
            req.setAttribute(TYPE_MESSAGE, WARNING);
        } else {
            try {
                if (serviceComputer.deleteComputer(MapUtil.stringListIdToStringInListDatabase(idsComputer))) {
                    req.setAttribute(MESSAGE_USER, DELETE_SUCCESSFULL_COMPUTER.toString());
                    req.setAttribute(TYPE_MESSAGE, SUCCESS);
                } else {
                    req.setAttribute(MESSAGE_USER, new ComputerNotFoundException("").getMessage());
                    req.setAttribute(TYPE_MESSAGE, ERROR);
                }
            } catch (NumberFormatException e) {
                req.setAttribute(MESSAGE_USER, DELETE_NO_VALID_ID.toString());
                req.setAttribute(TYPE_MESSAGE, ERROR);
                req.getRequestDispatcher(DASHBOARD_SERVLET.toString()).forward(req, res);
            }
        }
        req.getRequestDispatcher(DASHBOARD_SERVLET.toString()).forward(req, res);
    }

    /**
     * Verifie que les localdates ne produisent pas d'erreur, demande a la couche de
     * service de mettre a jour le computer et gère toutes les eventuelles erreurs
     * qui irait à l'encontre des validations necessaires pour sa modification.
     * @param req
     *            HttpServletRequest
     * @param res
     *            HttpServletResponse
     * @throws ServletException
     *             Si une Exception apparait dans la servlet
     * @throws IOException
     *             IOException
     */
    private void editComputerInDatabase(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        final List<LocalDate> localDates = sendLocalDateErrors(req, res, EDIT_COMPUTER);
        if (localDates.isEmpty()) {
            return;
        }
        final Long computerId = returnIdIfIsPresent(req, res);
        if (!computerId.equals(-1L)) {
            try {
                final Computer computer = createComputerToAddToDatabase(req, localDates);
                computer.setId(computerId);
                serviceComputer.updateComputer(computer);
                req.setAttribute(MESSAGE_USER, UPDATE_SUCCESSFULL_COMPUTER.toString());
                req.setAttribute(TYPE_MESSAGE, SUCCESS);
                req.getRequestDispatcher(DASHBOARD_SERVLET.toString()).forward(req, res);
            } catch (ComputerException e) {
                req.setAttribute(ID, computerId);
                sendErrorMessagetoUser(req, res, e.getMessage(), EDIT_COMPUTER);
            } catch (DateTruncationException e) {
                req.setAttribute(ID, computerId);
                sendErrorMessagetoUser(req, res, e.getMessage(), EDIT_COMPUTER);
            } catch (CompanyNotFoundException e) {
                req.setAttribute(ID, computerId);
                sendErrorMessagetoUser(req, res, e.getMessage(), EDIT_COMPUTER);
            }
        }

    }

    /**
     * Si l'ID n'est pas present envoie un message d'erreur et renvoie -1 sinon
     * renvoie l'ID du computer.
     * @param req
     *            HttpServletRequest
     * @param res
     *            HttpServletResponse
     * @return -1 ou ID du computer
     * @throws IOException
     *             IOException
     * @throws ServletException
     *             ServletException
     */
    private Long returnIdIfIsPresent(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        Long idReturn = -1L;
        try {
            idReturn = Long.valueOf(req.getParameter(COMPUTER_ID));
        } catch (NumberFormatException e) {
            req.setAttribute(MESSAGE_USER, DELETE_NO_VALID_ID.toString());
            req.setAttribute(TYPE_MESSAGE, ERROR);
            req.getRequestDispatcher(DASHBOARD_SERVLET.toString()).forward(req, res);
        }
        return idReturn;
    }

    /**
     * Redirige l'utilisateur vers la page d'edition de computer.
     * @param req
     *            HttpServletRequest
     * @param res
     *            HttpServletResponse
     * @throws ServletException
     *             Exception dans la servlet
     * @throws IOException
     *             IOException
     */
    private void goToFormEditComputer(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        try {
            final Long idComputer = Long.valueOf(req.getParameter(ID));
            ComputerDTO computerDTO = MapUtil.computerToComputerDTO(serviceComputer.getComputerDaoDetails(idComputer));
            req.setAttribute(COMPUTER, computerDTO);
            req.setAttribute(COMPANYS, serviceCompany.getAll());
            req.getRequestDispatcher(EDITCOMPUTER_JSP.toString()).forward(req, res);
        } catch (NumberFormatException e) {
            req.getRequestDispatcher(ERROR_PAGE_404.toString()).forward(req, res);
        } catch (ComputerNotFoundException e) {
            req.setAttribute(MESSAGE_USER, e.getMessage());
            req.setAttribute(TYPE_MESSAGE, ERROR);
            req.getRequestDispatcher(DASHBOARD_SERVLET.toString()).forward(req, res);
        }
    }

    /**
     * Demande au service d'ajouter un computer dans la base de données.
     * @param req
     *            HttpServletRequest
     * @param res
     *            HttpServletResponse
     * @throws ServletException
     *             Exception dans la Servlet
     * @throws IOException
     *             IOException
     */
    private void addComputerToDatabase(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        final List<LocalDate> localDates = sendLocalDateErrors(req, res, ADD_COMPUTER);
        if (localDates.isEmpty()) {
            return;
        }
        try {
            final Computer computer = createComputerToAddToDatabase(req, localDates);
            if (serviceComputer.createComputer(computer) == -1L) {
                sendErrorMessagetoUser(req, res, ADD_ERROR_COMPUTER.toString(), ADD_COMPUTER);
            } else {
                req.setAttribute(MESSAGE_USER, ADD_SUCCESSFULL_COMPUTER.toString());
                req.setAttribute(TYPE_MESSAGE, SUCCESS);
                req.getRequestDispatcher(DASHBOARD_SERVLET.toString()).forward(req, res);
            }
        } catch (ComputerException e) {
            sendErrorMessagetoUser(req, res, e.getMessage(), ADD_COMPUTER);
        } catch (CompanyNotFoundException e) {
            sendErrorMessagetoUser(req, res, e.getMessage(), ADD_COMPUTER);
        } catch (DateTruncationException e) {
            sendErrorMessagetoUser(req, res, e.getMessage(), ADD_COMPUTER);
        }
    }

    /**
     * Create un computer a l'aide des parametres recu par l'utilisateur.
     * @param req
     *            HttpServletRequest
     * @param localDates
     *            LocalDate introduced et discontinued
     * @return Computer computer a ajouté en base
     * @throws ComputerNameNotPresentException
     *             Le nom du computer est obligatoire
     */
    private Computer createComputerToAddToDatabase(HttpServletRequest req, final List<LocalDate> localDates)
            throws ComputerNameNotPresentException {
        final LocalDate introduced = localDates.get(0);
        final LocalDate discontinued = localDates.get(1);
        final String computerName = req.getParameter(COMPUTER_NAME);
        if (computerName.trim().isEmpty()) {
            throw new ComputerNameNotPresentException();
        }
        final Long companyId = Long.valueOf(req.getParameter(COMPANY_ID));
        Company company = null;
        if (companyId != 0L) { // Attention service catch company hors liste et -1
            company = new Company.Builder(companyId).build();
        }
        final Computer computer = new Computer.Builder(computerName).introduced(introduced).discontinued(discontinued)
                .company(company).build();
        return computer;
    }

    /**
     * Si retourne true c'est que des erreurs sont a afficher coté user et on doit
     * interrupt l'ajout en BD.
     * @param req
     *            HttpServletRequest
     * @param res
     *            HttpServletResponse
     * @param page
     *            Page de destination
     * @throws IOException
     *             IOException
     * @throws ServletException
     *             Exception dans la Servlet
     * @return List<LocalDate> Retourne les date pour l'ajout du computer, ou vide
     *         si une erreur est presente
     */
    private List<LocalDate> sendLocalDateErrors(HttpServletRequest req, HttpServletResponse res, final String page)
            throws ServletException, IOException {
        List<LocalDate> localDates = new ArrayList<>();
        LocalDate introduced = null;
        LocalDate discontinued = null;
        final StringBuilder errors = new StringBuilder(UL);
        final String dateIntro = req.getParameter(INTRODUCED);
        final String dateDiscon = req.getParameter(DISCONTINUED);
        try {
            introduced = dateIntro.isEmpty() ? null : MapUtil.parseStringToLocalDate(dateIntro);
        } catch (LocalDateExpectedException e) {
            errors.append(LI);
            errors.append(e.getMessage());
        }
        try {
            discontinued = dateDiscon.isEmpty() ? null : MapUtil.parseStringToLocalDate(dateDiscon);
        } catch (LocalDateExpectedException e) {
            errors.append(LI);
            errors.append(e.getMessage());
            errors.append(LI_CLOSE);
        }
        if (!errors.toString().equals(UL)) {
            errors.append(UL_CLOSE);
            req.setAttribute(ID, req.getParameter(COMPUTER_ID));
            sendErrorMessagetoUser(req, res, errors.toString(), page);
        } else {
            localDates.add(introduced);
            localDates.add(discontinued);
        }
        return localDates;
    }

    /**
     * Ajoute le message d'erreur et son type, et redirige en fonction de l'action.
     * @param req
     *            Requete
     * @param res
     *            Reponse
     * @param errors
     *            Erreurs a afficher
     * @param page
     *            Page de destination
     * @throws ServletException
     *             Exception liée a la servlet
     * @throws IOException
     *             IOException
     */
    private void sendErrorMessagetoUser(HttpServletRequest req, HttpServletResponse res, final String errors,
            final String page) throws ServletException, IOException {
        req.setAttribute(MESSAGE_USER, errors);
        req.setAttribute(TYPE_MESSAGE, ERROR);
        if (page.equals(ADD_COMPUTER)) {
            req.setAttribute(ACTION, ADDFORM.toString());
        } else {
            final Long idComputer = Long.valueOf(req.getAttribute(ID).toString()); // ID peut etre null - to string
            try {
                final ComputerDTO computerDTO = MapUtil
                        .computerToComputerDTO(serviceComputer.getComputerDaoDetails(idComputer));
                req.setAttribute(COMPUTER, computerDTO);
            } catch (ComputerNotFoundException e) {
                LOGGER.info(e.getMessage());
            }
            req.setAttribute(ACTION, EDITFORM.toString());
        }
        req.setAttribute(COMPANYS, serviceCompany.getAll());
        req.getRequestDispatcher(String.format("jsp/%s.jsp", page)).forward(req, res);
    }

}
