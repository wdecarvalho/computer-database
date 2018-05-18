package com.excilys.servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.excilys.dto.ComputerDTO;
import com.excilys.mapper.MapUtil;
import com.excilys.model.Computer;
import com.excilys.service.ServiceComputer;
import com.excilys.util.Pages;

import static com.excilys.servlet.RouteUrl.DASHBOARD_JSP;

@WebServlet(name = "dashboard", urlPatterns = { "/dashboard" })
public class ServletMain extends HttpServlet {
    private static final String NUMBER_RESULT = "numberResult";
    private static final String PAGE_COURANTE = "pageCourante";
    private static final String LIMIT = "limit";
    private static final String NB_COMPUTERS = "nbComputers";
    private static final String COMPUTERS = "computers";
    private static final String PAGE = "page";
    private static final long serialVersionUID = 1L;

    @Autowired
    private ServiceComputer serviceComputer;

    /**
     * Init le service de CDB.
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
        final String action = req.getParameter("action");
        if ("search".equals(action)) {
            printDashboard(req, res, req.getParameter("search"));
        } else {
            printDashboard(req, res, "");
        }
    }

    /**
     * Affiche le dashboard sans recherche.
     * @param req
     *            HttpServletRequest
     * @param res
     *            HttpServletReponse
     * @param search
     *            Si 0 false sinon true
     * @throws ServletException
     *             ServletException
     * @throws IOException
     *             IOException
     */
    private void printDashboard(HttpServletRequest req, HttpServletResponse res, final String search)
            throws ServletException, IOException {
        int numberResult = numberResultForThisRequest(req);
        int page = 1;
        try {
            page = Integer.parseInt(req.getParameter(PAGE));
        } catch (NumberFormatException e) {
            // Nothing to do especially
        }
        final Pages<Computer> pagesComputer;
        if (search.isEmpty()) {
            pagesComputer = serviceComputer.findByPage(page, numberResult);
        } else {
            pagesComputer = serviceComputer.findByPagesComputer(search, page, numberResult);
            req.setAttribute("toSearch", search);
        }
        final List<ComputerDTO> computerDTOs = pagesComputer.getEntities().stream()
                .map(c -> MapUtil.computerToComputerDTO(c)).collect(Collectors.toList());
        req.setAttribute(COMPUTERS, computerDTOs);
        req.setAttribute(NB_COMPUTERS, pagesComputer.getMaxComputers());
        req.setAttribute(LIMIT, pagesComputer.getPageMax());
        req.setAttribute(PAGE_COURANTE, pagesComputer.getPageCourante());
        req.getRequestDispatcher(DASHBOARD_JSP.toString()).forward(req, res);
    }

    /**
     * Si le nombre de resultat n'est pas défini, alors on retourne 10. Sinon si
     * l'utilisateur demande 10, 50 ou 100 resultat alors on met a jour le nombre de
     * resultat a retourner.
     * @param req
     *            Requete pagination dashboard
     * @return Nombre de resultat a afficher
     */
    private int numberResultForThisRequest(HttpServletRequest req) {
        if (req.getSession().getAttribute(NUMBER_RESULT) == null) {
            req.getSession().setAttribute(NUMBER_RESULT, 10);
        }
        int numberResult = (int) req.getSession().getAttribute(NUMBER_RESULT);
        try {
            int tmpNumberResult = Integer.parseInt(req.getParameter(NUMBER_RESULT));
            if (tmpNumberResult == 10 || tmpNumberResult == 50 || tmpNumberResult == 100) {
                req.getSession().setAttribute(NUMBER_RESULT, tmpNumberResult);
                numberResult = tmpNumberResult;
            }
        } catch (NumberFormatException e) {
            // Nothing to do especially
        }
        return numberResult;
    }
}
