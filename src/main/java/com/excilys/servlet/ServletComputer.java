package com.excilys.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.exception.DaoNotInitializeException;
import com.excilys.service.ServiceCdb;

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
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res) {
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
            } else {
                req.getRequestDispatcher("static/views/404.html").forward(req, res);
            }
        } else {
            req.getRequestDispatcher("static/views/404.html").forward(req, res);
        }

        // int numberResult = numberResultForThisRequest(req);
        // int page = 1;
        // try {
        // page = Integer.parseInt(req.getParameter("page"));
        // } catch (NumberFormatException e) {
        // // Nothing to do especially
        // }
        // final Pages<Computer> pagesComputer = serviceCdb.findByPagesComputer(page,
        // numberResult);
        // final List<ComputerDTO> computerDTOs = pagesComputer.getEntities().stream()
        // .map(c -> MapUtil.computerToComputerDTO(c)).collect(Collectors.toList());
        // req.setAttribute("computers", computerDTOs);
        // req.setAttribute("nbComputers", pagesComputer.getMaxComputers());
        // req.setAttribute("limit", pagesComputer.getPageMax());
        // req.setAttribute("pageCourante", pagesComputer.getPageCourante());

    }

}
