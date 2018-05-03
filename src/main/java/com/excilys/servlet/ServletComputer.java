package com.excilys.servlet;

import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.exception.CompanyNotFoundException;
import com.excilys.exception.ComputerException;
import com.excilys.exception.DaoNotInitializeException;
import com.excilys.exception.LocalDateExpectedException;
import com.excilys.mapper.MapUtil;
import com.excilys.model.Company;
import com.excilys.model.Computer;
import com.excilys.service.ServiceCdb;

import static com.excilys.tags.TypeAlerte.ERROR;
import static com.excilys.tags.TypeAlerte.SUCCESS;

@WebServlet("/computer")
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
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            if (req.getParameter("action").equals("editForm")) {
                req.setAttribute("companys", serviceCdb.getListCompanies());
                req.getRequestDispatcher("jsp/addComputer.jsp").forward(req, res);
            } else if (req.getParameter("action").equals("add")) {
                final StringBuilder errors = new StringBuilder("<ul>");
                final String computerName = req.getParameter("computerName");
                final String dateIntro = req.getParameter("introduced");
                final String dateDiscon = req.getParameter("discontinued");
                LocalDate introduced = null;
                LocalDate discontinued = null;
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
                    sendErrorMessagetoUser(req, res, errors.toString());
                    return;
                }
                Long companyId = Long.valueOf(req.getParameter("companyId")); //final
                Company company = null;
                if (companyId != 0L) {
                    company = new Company.Builder(companyId).build();
                }
                Computer computer = new Computer.Builder(computerName).introduced(introduced).discontinued(discontinued)
                        .company(company).build();
                try {
                    if (serviceCdb.createComputer(computer) == -1L) {
                        sendErrorMessagetoUser(req, res, "Une erreur a empeché la création de l'ordinateur");
                    } else {
                        req.setAttribute("messageUser", "L'ordinateur a été correctement sauvegardé");
                        req.setAttribute("typeMessage", SUCCESS);
                        req.getRequestDispatcher("/dashboard").forward(req, res);
                    }

                } catch (ComputerException e) {
                    sendErrorMessagetoUser(req, res, e.getMessage());
                } catch (CompanyNotFoundException e) {
                    sendErrorMessagetoUser(req, res, e.getMessage());
                }
            } else {
                req.getRequestDispatcher("static/views/404.html").forward(req, res);
            }
        } else {
            req.getRequestDispatcher("static/views/404.html").forward(req, res);
        }
    }

    /**
     * @param req
     *            Requete
     * @param res
     *            Reponse
     * @param errors
     *            Erreurs a afficher
     * @throws ServletException
     *             Exception liée a la servlet
     * @throws IOException
     *             IOExceptio
     */
    private void sendErrorMessagetoUser(HttpServletRequest req, HttpServletResponse res, final String errors)
            throws ServletException, IOException {
        req.setAttribute("messageUser", errors);
        req.setAttribute("typeMessage", ERROR);
        req.setAttribute("action", "editForm");
        req.setAttribute("companys", serviceCdb.getListCompanies());
        req.getRequestDispatcher("jsp/addComputer.jsp").forward(req, res);
    }

}
