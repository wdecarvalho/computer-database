package com.excilys.servlet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.dto.ComputerDTO;
import com.excilys.exception.DaoNotInitializeException;
import com.excilys.mapper.MapUtil;
import com.excilys.model.Computer;
import com.excilys.service.ServiceCdb;
import com.excilys.util.Pages;

@WebServlet(name = "dashboard", urlPatterns = { "/dashboard" })
public class ServletMain extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(Logger.class);

    private ServiceCdb serviceCdb;

    /**
     * Init le service de CDB.
     */
    public ServletMain() {
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
        int numberResult = numberResultForThisRequest(req);
        int page = 1;
        try {
            page = Integer.parseInt(req.getParameter("page"));
        } catch (NumberFormatException e) {
            // Nothing to do especially
        }
        final Pages<Computer> pagesComputer = serviceCdb.findByPagesComputer(page, numberResult);
        final List<ComputerDTO> computerDTOs = pagesComputer.getEntities().stream()
                .map(c -> MapUtil.computerToComputerDTO(c)).collect(Collectors.toList());
        req.setAttribute("computers", computerDTOs);
        req.setAttribute("nbComputers", pagesComputer.getMaxComputers());
        req.setAttribute("limit", pagesComputer.getPageMax());
        req.setAttribute("pageCourante", pagesComputer.getPageCourante());
        req.getRequestDispatcher("jsp/dashboard.jsp").forward(req, res);
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
        if (req.getSession().getAttribute("numberResult") == null) {
            req.getSession().setAttribute("numberResult", 10);
        }
        int numberResult = (int) req.getSession().getAttribute("numberResult");
        try {
            int tmpNumberResult = Integer.parseInt(req.getParameter("numberResult"));
            if (tmpNumberResult == 10 || tmpNumberResult == 50 || tmpNumberResult == 100) {
                req.getSession().setAttribute("numberResult", tmpNumberResult);
                numberResult = tmpNumberResult;
            }
        } catch (NumberFormatException e) {
            // nothing to do especially
        }
        return numberResult;
    }
}
