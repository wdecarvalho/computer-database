package com.excilys.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.excilys.exception.DaoNotInitializeException;

public class DaoFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DaoFactory.class);

    private static DaoFactory factory;

    private static Connection connection;

    /**
     * Constructeur de DaoFactory.
     */
    private DaoFactory() {
        // default singleton
    }

    /**
     * La factory fourni la DAO demandé et si on lui demande une DAO qu'elle ne
     * possede pas throw une exception.
     * @param type
     *            Type de la DAO demandé
     * @return DAO requise
     * @throws SQLException
     *             Si une exception SQL apparait
     * @throws DaoNotInitializeException
     *             Une DAO non géré a été demandé
     */
    public Dao<?> getDao(DaoType type) throws SQLException, DaoNotInitializeException {

        switch (type) {
        case COMPUTER_DAO:
            return getComputerDao();
        case COMPANY_DAO:
            return getCompanyDao();
        default:
            throw new DaoNotInitializeException();
        }
    }

    /**
     * Recupere l'instance de computerDAO en lui passant la connection.
     * @throws SQLException
     *             La connexion n'a pas pu s'effectuer
     * @return ComputerDao
     */
    private static ComputerDao getComputerDao() throws SQLException {
        return ComputerDao.getInstance(connection);
    }

    /**
     * Recupere l'instance de companyDAO en lui passant la connection.
     * @throws SQLException
     *             La connexion n'a pas pu s'effectuer
     * @return CompanyDao
     */
    private static CompanyDao getCompanyDao() throws SQLException {
        return CompanyDao.getInstance(connection);
    }

    /**
     * Retourne la factory et la crée si elle n'existe pas.
     * @return DaoFactory
     * @throws SQLException
     *             Si une erreur SQL apparait
     */
    public static DaoFactory getInstance() throws SQLException {
        if (factory == null) {
            factory = new DaoFactory();
            initConnexion();
        }
        return factory;

    }

    /**
     * Initiliase la connexion a la base de donnée.
     * @return Connection
     * @throws SQLException
     *             La connexion n'a pas pu s'effectuer
     * @throws IOException
     */
    private static Connection initConnexion() throws SQLException {
        final Properties aProperties = new Properties();
        final InputStream path = ClassLoader.getSystemClassLoader().getResourceAsStream("app.properties");
        String driver = null;
        try {
            aProperties.load(path);
            driver = aProperties.getProperty("driver");
        } catch (FileNotFoundException e) {
            LOGGER.error(e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        try {
            Class.forName(aProperties.getProperty("driver"));

        } catch (ClassNotFoundException e) {
            LOGGER.error(e.getMessage());
        }
        LOGGER.info("Base de donnée utilisée : " + aProperties.getProperty("url"));
        connection = DriverManager.getConnection(aProperties.getProperty("url"), aProperties.getProperty("user"),
                aProperties.getProperty("password"));
        if (driver.equals("org.h2.Driver")) {
            try {
                FileReader fReader = new FileReader(
                        new File(ClassLoader.getSystemClassLoader().getResource("test_db.sql").toURI()));
                RunScript.execute(connection, new FileReader(
                        new File(ClassLoader.getSystemClassLoader().getResource("test_db.sql").toURI())));
            } catch (FileNotFoundException e1) {
                LOGGER.error(e1.getMessage());
            } catch (URISyntaxException e1) {
                LOGGER.error(e1.getMessage());
            }
        }
        return connection;
    }

    /**
     * Permet de fermer la connection.
     * @throws SQLException
     *             Si une erreur SQL apparait
     */
    public static void endConnexion() throws SQLException {
        connection.close();
    }
}
