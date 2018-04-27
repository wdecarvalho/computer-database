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

import com.excilys.exception.ComputerNameNotPresentException;
import com.excilys.exception.DaoNotInitializeException;
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
                String computerName = req.getParameter("computerName");
                String dateIntro = req.getParameter("introduced");
                String dateDiscon = req.getParameter("discontinued");
                LocalDate introduced = dateIntro.isEmpty() ? null : LocalDate.parse(dateIntro);
                LocalDate discontinued = dateDiscon.isEmpty() ? null : LocalDate.parse(dateDiscon);
                Long company = Long.valueOf(req.getParameter("companyId"));
                Computer computer = new Computer.Builder(computerName).introduced(introduced).discontinued(discontinued)
                        .company(new Company.Builder(company).build()).build();
                try {
                    if (serviceCdb.createComputer(computer) == -1L) {
                        req.setAttribute("messageUser", "Une erreur a empeché la création de l'ordinateur");
                        req.setAttribute("typeMessage", ERROR);
                        req.setAttribute("action", "editForm");
                        req.getRequestDispatcher("jsp/computer.jsp").forward(req, res);
                    } else {
                        req.setAttribute("messageUser", "L'ordinateur a été correctement sauvegardé");
                        req.setAttribute("typeMessage", SUCCESS);
                    }

                } catch (ComputerNameNotPresentException e) {
                    req.setAttribute("messageUser", e.getMessage());
                    req.setAttribute("typeMessage", ERROR);
                    req.setAttribute("action", "editForm");
                    req.setAttribute("companys", serviceCdb.getListCompanies());
                    req.getRequestDispatcher("jsp/addComputer.jsp").forward(req, res);
                }
            } else {
                req.getRequestDispatcher("static/views/404.html").forward(req, res);
            }
        } else {
            req.getRequestDispatcher("static/views/404.html").forward(req, res);
        }
    }

}
