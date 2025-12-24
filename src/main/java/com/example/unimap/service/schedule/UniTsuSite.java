package com.example.unimap.service.schedule;

import com.example.unimap.exception.InvalidInputException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class UniTsuSite {
    private WebDriver driver;

    public UniTsuSite(String username, String password) {
        checkInputsValidity(username, password);

        driver = constractDriver();

        String loginUrl = "https://uni.tsu.ge/login";
        driver.get(loginUrl);

        loggingIn(username, password);

        String scheduleUrl = "https://uni.tsu.ge/schedule";
        driver.get(scheduleUrl);
    }

    private void checkInputsValidity(String username, String password) {
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new InvalidInputException("invalid username or password");
        }
    }

    private void loggingIn(String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        String usernameXPath = "//input[@formcontrolname='username']";
        String passwordXPath = "//input[@formcontrolname='password']";
        WebElement usernameBar = waitAndFindElementBy(wait, usernameXPath);
        WebElement passwordBar = waitAndFindElementBy(wait, passwordXPath);

        usernameBar.sendKeys(username);       // inputing values in login bars
        passwordBar.sendKeys(password);

        // find click button and click
        String buttonXPath = "//button[normalize-space()='შესვლა']";
        WebElement loginButton = wait.until(ExpectedConditions
                .elementToBeClickable(By.xpath(buttonXPath)));
        loginButton.click();

        try {       // wait until logged in.
            wait.until(ExpectedConditions.stalenessOf(loginButton));
        } catch (Exception e) {
            throw new InvalidInputException("incorrect username or password");
        }
    }

    public List<WebElement> getScheduleHeadValues(){
        String scheduleHeadXPath = "//table/thead/tr/th";       // schedule's head
        return findElementsBy(scheduleHeadXPath);
    }

    public List<WebElement> getScheduleBodyRows(){
        String scheduleBodyXPath = "//table/tbody/tr";         // schedule's body
        return findElementsBy(scheduleBodyXPath);
    }

    // more or less ensures to fetch all the content of schedule
    private List<WebElement> findElementsBy(String xPath) {
        int elementCounter = 0;
        int reliability = 0;
        int attempts = 5;                            // guarantiees no eternal loop
        List<WebElement> elements = List.of();

        while (reliability < 3) {
            elements = driver.findElements(By.xpath(xPath));
            if (elementCounter != elements.size()) {
                if (attempts == 0) {        // fetching failed more that 5 times
                    throw new RuntimeException("schedule not fetched");
                }
                elementCounter = elements.size();
                reliability = 0;
                attempts--;
            } else reliability++;           // when fetched same amount of elements
        }

        return elements;
    }


    private WebElement waitAndFindElementBy(WebDriverWait wait, String xPath) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xPath)));
        } catch (Exception e) {
            throw new RuntimeException("could not fetch data");
        }
    }

    private WebDriver constractDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        return new ChromeDriver(options);
    }
}
