package com.excilys.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.dto.ComputerDTO;
import com.excilys.exception.CompanyNotFoundException;
import com.excilys.exception.ComputerException;
import com.excilys.exception.ComputerNeedIdToBeUpdateException;
import com.excilys.exception.ComputerNotFoundException;
import com.excilys.exception.DaoNotInitializeException;
import com.excilys.exception.DateTruncationException;
import com.excilys.exception.LocalDateExpectedException;
import com.excilys.mapper.MapUtil;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.service.ServiceCdb;

import static com.excilys.tags.TypeAlerte.ERROR;
import static com.excilys.tags.TypeAlerte.SUCCESS;

@WebServlet(urlPatterns = { "/computer" })
public class ServletComputer extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Logger.class);

    private ServiceCdb serviceCdb;

    /**
     * Init le service de CDB.
     */
    public ServletComputer() {
        try {
            serviceCdb = ServiceCdb.getInstance();
        } catch (DaoNotInitializeException e) {
            LOGGER.error(e.getMessage());
        }
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
        if (req.getParameter("action") != null) {
            if (req.getParameter("action").equals("addForm")) {
                req.setAttribute("companys", serviceCdb.getListCompanies());
                req.getRequestDispatcher("jsp/addComputer.jsp").forward(req, res);
            } else if (req.getParameter("action").equals("add")) {
                addComputerToDatabase(req, res);
            } else if (req.getParameter("action").equals("editForm")) {
                goToFormEditComputer(req, res);
            } else if (req.getParameter("action").equals("edit")) {
                final List<LocalDate> localDates = sendLocalDateErrors(req, res, "editComputer");
                if (localDates.isEmpty()) {
                    return;
                }
                final Computer computer = createComputerToAddToDatabase(req, localDates);
                try {
                    computer.setId(Long.valueOf(req.getParameter("computerID")));
                    serviceCdb.updateComputer(computer);
                    req.setAttribute("messageUser", "L'ordinateur a été correctement modifié");
                    req.setAttribute("typeMessage", SUCCESS);
                    req.getRequestDispatcher("/dashboard").forward(req, res);
                } catch (ComputerException e) {
                    req.setAttribute("id", computer.getId());
                    sendErrorMessagetoUser(req, res, e.getMessage(), "editComputer");
                } catch (NumberFormatException e) {
                    sendErrorMessagetoUser(req, res, new ComputerNeedIdToBeUpdateException().getMessage(), "dashboard");
                } catch (DateTruncationException e) {
                    req.setAttribute("id", computer.getId());
                    sendErrorMessagetoUser(req, res, e.getMessage(), "editComputer");
                } catch (CompanyNotFoundException e) {
                    req.setAttribute("id", computer.getId());
                    sendErrorMessagetoUser(req, res, e.getMessage(), "editComputer");
                }
            } else {
                req.getRequestDispatcher("static/views/404.html").forward(req, res);
            }
        } else {
            req.getRequestDispatcher("static/views/404.html").forward(req, res);
        }
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
            final Long idComputer = Long.valueOf(req.getParameter("id"));
            ComputerDTO computerDTO = MapUtil.computerToComputerDTO(serviceCdb.getComputerDaoDetails(idComputer));
            req.setAttribute("computer", computerDTO);
            req.setAttribute("companys", serviceCdb.getListCompanies());
            req.getRequestDispatcher("jsp/editComputer.jsp").forward(req, res);
        } catch (NumberFormatException e) {
            req.getRequestDispatcher("static/views/404.html").forward(req, res);
        } catch (ComputerNotFoundException e) {
            req.setAttribute("messageUser", e.getMessage());
            req.setAttribute("typeMessage", ERROR);
            req.getRequestDispatcher("/dashboard").forward(req, res);
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
        final List<LocalDate> localDates = sendLocalDateErrors(req, res, "addComputer");
        if (localDates.isEmpty()) {
            return;
        }
        final Computer computer = createComputerToAddToDatabase(req, localDates);
        try {
            if (serviceCdb.createComputer(computer) == -1L) {
                sendErrorMessagetoUser(req, res, "Une erreur a empeché la création de l'ordinateur", "addComputer");
            } else {
                req.setAttribute("messageUser", "L'ordinateur a été correctement sauvegardé");
                req.setAttribute("typeMessage", SUCCESS);
                req.getRequestDispatcher("/dashboard").forward(req, res);
            }
        } catch (ComputerException e) {
            sendErrorMessagetoUser(req, res, e.getMessage(), "addComputer");
        } catch (CompanyNotFoundException e) {
            sendErrorMessagetoUser(req, res, e.getMessage(), "addComputer");
        } catch (DateTruncationException e) {
            sendErrorMessagetoUser(req, res, e.getMessage(), "addComputer");
        }
    }

    /**
     * Create un computer a l'aide des parametres recu par l'utilisateur.
     * @param req
     *            HttpServletRequest
     * @param localDates
     *            LocalDate introduced et discontinued
     * @return Computer computer a ajouté en base
     */
    private Computer createComputerToAddToDatabase(HttpServletRequest req, final List<LocalDate> localDates) {
        final LocalDate introduced = localDates.get(0);
        final LocalDate discontinued = localDates.get(1);
        final String computerName = req.getParameter("computerName");
        final Long companyId = Long.valueOf(req.getParameter("companyId"));
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
        final StringBuilder errors = new StringBuilder("<ul>");
        final String dateIntro = req.getParameter("introduced");
        final String dateDiscon = req.getParameter("discontinued");
        try {
            introduced = dateIntro.isEmpty() ? null : MapUtil.parseStringToLocalDate(dateIntro);
        } catch (LocalDateExpectedException e) {
            errors.append("<li>");
            errors.append(e.getMessage());
        }
        try {
            discontinued = dateDiscon.isEmpty() ? null : MapUtil.parseStringToLocalDate(dateDiscon);
        } catch (LocalDateExpectedException e) {
            errors.append("<li>");
            errors.append(e.getMessage());
            errors.append("</li>");
        }
        if (!errors.toString().equals("<ul>")) {
            errors.append("</ul>");
            req.setAttribute("id", req.getParameter("computerID"));
            sendErrorMessagetoUser(req, res, errors.toString(), page);
        } else {
            localDates.add(introduced);
            localDates.add(discontinued);
        }
        return localDates;
    }

    /**
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
        req.setAttribute("messageUser", errors);
        req.setAttribute("typeMessage", ERROR);
        if (page.equals("addComputer")) {
            req.setAttribute("action", "addForm");
        } else {
            final Long idComputer = Long.valueOf(req.getAttribute("id").toString());
            try {
                final ComputerDTO computerDTO = MapUtil
                        .computerToComputerDTO(serviceCdb.getComputerDaoDetails(idComputer));
                req.setAttribute("computer", computerDTO);
            } catch (ComputerNotFoundException e) {
                LOGGER.info(e.getMessage());
            }
            req.setAttribute("action", "editForm");
        }
        req.setAttribute("companys", serviceCdb.getListCompanies());
        req.getRequestDispatcher(String.format("jsp/%s.jsp", page)).forward(req, res);
    }

}
