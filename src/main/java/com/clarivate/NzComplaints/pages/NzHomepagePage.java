package com.clarivate.NzComplaints.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NzHomepagePage {
    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor js;

    public NzHomepagePage(WebDriver driver, JavascriptExecutor js) {
        this.driver = driver;
        this.js = js;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void navigateToWebsite(String url) {
        driver.get(url);
    }

    public void clickClassificationStatus() {
        WebElement classificationStatus = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("MainContent_ctrlTMSearch_hdrClassifStatusCriteria_lblheader")));
        classificationStatus.click();
    }

    public void clickSelectStatus() {
        WebElement selectStatus = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("MainContent_ctrlTMSearch_ctrlCaseStatusSearchDialog_lnkBtnSearch")));
        selectStatus.click();
    }
    public void clickSearchButton() {
        WebElement searchButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("MainContent_ctrlTMSearch_lnkbtnSearch")));
        searchButton.click();
    }
    public void sortByCaseNumber() {
        WebElement sortCaseNumber = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, \"Sort$NRPROC\")]")));
        sortCaseNumber.click();
    }

    public void openFirstCase() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader"))); // optional: if a loader exists
        wait.withTimeout(Duration.ofSeconds(12)); // to account for long load time
        WebElement firstCase = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[id*='gvwIPCases_lnkBtnCaseBrowser_3']")));
        firstCase.click();
    }

}
