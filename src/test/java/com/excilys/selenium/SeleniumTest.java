package com.excilys.selenium;

import org.testng.annotations.Test;

import com.excilys.dao.CompanyDao;
import com.excilys.exception.DaoNotInitializeException;

import org.testng.annotations.BeforeClass;

import static org.testng.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;

public class SeleniumTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumTest.class);

    private WebDriver driver;

    private CompanyDao companyDao;

    /**
     * @throws DaoNotInitializeException
     *             Si la DAO n'a pas pu etre initialisé
     * @throws SQLException
     *             SQLException
     * @throws FileNotFoundException
     *             Si le fichier n'a pas été trouvés
     */
    @BeforeClass
    public void beforeClass() throws SQLException, DaoNotInitializeException, FileNotFoundException {
        System.setProperty("webdriver.gecko.driver", "/home/decarvalho/Documents/geckodriver/geckodriver");
        driver = new FirefoxDriver();
    }

    /**
     */
    @AfterClass
    public void afterClass() {
        driver.quit();
    }

    /**
     */
    @Test
    public void verifySearchButton() {
        LOGGER.error("Je passe ICICICICICI");
        driver.get("http://localhost:8080/william.cdb/dashboard");
        WebElement text = driver.findElement(By.id("addComputer"));
        assertTrue(text.getText().equals("Add Computer"));
    }

}
