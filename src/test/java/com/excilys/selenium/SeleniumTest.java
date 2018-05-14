package com.excilys.selenium;

import org.testng.annotations.Test;

import com.excilys.exception.DaoNotInitializeException;

import javafx.scene.control.Pagination;

import org.testng.annotations.BeforeClass;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;

public class SeleniumTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeleniumTest.class);

    private WebDriver driver;

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
     * Verifie qu'on arrive sur la page 1 par defaut, que si on demande la page 2 on
     * l'obtient et que si on demande une page trop loin on obtient la derniere page
     * possible ici 3.
     */
    @Test
    @DisplayName("Verify the pagination, should always get possible pages even if wrong entries")
    public void verifyPaginationTest() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.get("http://localhost:8081/william.cdb/dashboard");
        WebElement pageActive = driver.findElement(By.className("pagination")).findElement(By.className("active"));
        assertEquals(pageActive.getText(), "1");
        List<WebElement> pages = (List<WebElement>) js.executeScript("return $('.pagination').children().get();");
        pages.get(2).findElement(By.linkText("2")).click();
        pageActive = driver.findElement(By.className("pagination")).findElement(By.className("active"));
        assertEquals(pageActive.getText(), "2");

        driver.navigate().to("http://localhost:8081/william.cdb/dashboard?page=4");
        // js.executeScript("document.location.href='/william.cdb/dashboard?page=4'");
        pageActive = driver.findElement(By.className("pagination")).findElement(By.className("active"));
        assertEquals(pageActive.getText(), "3");

        // js.executeScript("document.location.href='/william.cdb/dashboard?page=-4'");
        driver.navigate().to("http://localhost:8081/william.cdb/dashboard?page=-4");
        pageActive = driver.findElement(By.className("pagination")).findElement(By.className("active"));
        assertEquals(pageActive.getText(), "1");
    }

    /**
     * Verifie que lorsqu'on le click sur addComputer, on arrive sur la page du
     * formulaire, que le submit ne se fasse que si le nom est present en prevenant
     * l'utilisateur et que si tout ce passe correctement on est rediriger vers
     * dashboard.
     * @throws InterruptedException
     *             InterruptedException
     */
    @Test
    @DisplayName("Verify dashboard go to addComputer and add work redirecting to dasboard")
    public void verifyAddComputerTest() throws InterruptedException {
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        driver.get("http://localhost:8081/william.cdb/dashboard");
        driver.findElement(By.id("addComputer")).click();
        driver.findElement(By.id("addAComputer")).submit();
        assertTrue(driver.findElement(By.id("nomObligatoire")).isDisplayed());
        driver.findElement(By.id("computerName")).sendKeys("Test");
        driver.findElement(By.name("action")).click();
        driver.findElement(By.name("action")).click();
        WebDriverWait webDriverWait = new WebDriverWait(driver, 5);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("pagination")));
        WebElement pageActive = driver.findElement(By.className("pagination")).findElement(By.className("active"));
        assertEquals(pageActive.getText(), "1");
    }

}
