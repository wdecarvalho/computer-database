package com.excilys.selenium;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class SeleniumTest {

    private WebDriver driver;

    /**
     * @throws FileNotFoundException
     *             Si le fichier n'a pas été trouvés
     * @throws URISyntaxException
     *             URISyntaxException
     */
    @BeforeClass
    public void beforeClass() throws FileNotFoundException, URISyntaxException {
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
     * Verifie qu'on arrive sur la page 1 par defaut.
     */
    @Test
    @DisplayName("Should show the first page when page not setted")
    public void verifyPaginationDefaultTest() {
        driver.get("http://localhost:8081/william.cdb/dashboard");
        WebElement pageActive = driver.findElement(By.className("pagination")).findElement(By.className("active"));
        assertEquals(pageActive.getText(), "1");
    }

    /**
     * Verifie que si la page 2 est disponible un clic sur celle-ci nous deplace.
     */
    @SuppressWarnings("unchecked")
    @Test
    @DisplayName("Should go to page 2 when click on it is possible")
    public void verifyPaginationGoToExistTest() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        driver.get("http://localhost:8081/william.cdb/dashboard");
        List<WebElement> pages = (List<WebElement>) js.executeScript("return $('.pagination').children().get();");
        pages.get(2).findElement(By.linkText("2")).click();
        WebElement pageActive = driver.findElement(By.className("pagination")).findElement(By.className("active"));
        assertEquals(pageActive.getText(), "2");
    }

    /**
     * Verifie que si on essaye d'acceder a une page superieur a celle possible
     * retourne toujours la derniere possible. Verifie que si on essaye d'acceder a
     * une page inferieur a celle possible ou a une page invalide retourne toujours
     * la page 1
     */
    @Test
    @DisplayName("Should go to page possible when page invalid are requested")
    public void verifyPaginationTest() {
        driver.navigate().to("http://localhost:8081/william.cdb/dashboard?page=4");
        WebElement pageActive = driver.findElement(By.className("pagination")).findElement(By.className("active"));
        assertEquals(pageActive.getText(), "3");

        driver.navigate().to("http://localhost:8081/william.cdb/dashboard?page=-4");
        pageActive = driver.findElement(By.className("pagination")).findElement(By.className("active"));
        assertEquals(pageActive.getText(), "1");

        driver.navigate().to("http://localhost:8081/william.cdb/dashboard?page=a");
        assertTrue(driver.findElement(By.className("alert")).getText().contains("400"));
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

    /**
     * Verifie que la suppression du premier ordinateur fonctionne.
     * @throws InterruptedException
     */
    @Test
    @DisplayName("Verify that the delete on dashboard of the first computer work ")
    public void verifyDeleteComputerTest() {
        driver.get("http://localhost:8081/william.cdb/dashboard");
        driver.findElement(By.id("editComputer")).click();
        driver.findElements(By.className("cb")).get(0).click();
        driver.findElement(By.className(("fa-trash-o"))).click();
        Alert alert = driver.switchTo().alert();
        alert.accept();
        WebDriverWait webDriverWait = new WebDriverWait(driver, 3000);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.className("alert")));
        assertThrows(org.openqa.selenium.NoSuchElementException.class,
                () -> driver.findElement(By.linkText("MacBook Pro 15.4 inch")).getText());
    }

    /**
     * Verifie que l'edition du deuxieme ordinateur fonctionne et redirige vers la
     * page d'accueil.
     */
    @Test
    @DisplayName("Veirfy dashboard go to editComputer and edit work on the second computer and redirect to dashboard")
    public void verifyEditComputerTest() {
        driver.get("http://localhost:8081/william.cdb/dashboard");
        driver.findElement(By.linkText("CM-2a")).click();
        driver.findElement(By.id("computerName")).clear();
        driver.findElement(By.id("EditComputer")).submit();
        assertTrue(driver.findElement(By.id("nomObligatoire")).isDisplayed());
        driver.findElement(By.name("action")).click();
        driver.findElement(By.id("computerName")).sendKeys("CM-modif");
        driver.findElement(By.name("action")).click();
        driver.findElement(By.name("action")).click();
        assertTrue(driver.findElement(By.linkText("CM-modif")).isDisplayed());
    }

    /**
     * Permet d'attendre pour observer le fonctionnement [DEBUG MODE].
     * @throws InterruptedException
     *             InterruptedException
     */
    private void sleep() throws InterruptedException {
        WebDriverWait webDriverWait = new WebDriverWait(driver, 5000);
        synchronized (webDriverWait) {
            webDriverWait.wait();
        }
    }

}
