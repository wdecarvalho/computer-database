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

@WebServlet("/dashboard")
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
        int page = 1;
        try {
            page = Integer.parseInt(req.getParameter("page"));
        } catch (NumberFormatException e) {
            // Nothing to do especially
        }
        Pages<Computer> pagesComputer = serviceCdb.findByPagesComputer(page);
        List<ComputerDTO> computerDTOs = pagesComputer.getEntities().stream().map(c -> MapUtil.computerToComputerDTO(c))
                .collect(Collectors.toList());
        req.setAttribute("computers", computerDTOs);
        req.setAttribute("nbComputers", pagesComputer.getMaxComputers());
        req.setAttribute("limit", pagesComputer.getPageMax());
        req.setAttribute("pageCourante", pagesComputer.getPageCourante());
        req.getRequestDispatcher("jsp/dashboard.jsp").forward(req, res);
    }
}
