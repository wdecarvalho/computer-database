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
        req.setAttribute("computers", serviceCdb.findByPagesComputer(1).getEntities());
        req.getRequestDispatcher("jsp/dashboard.jsp").forward(req, res);
    }
}
