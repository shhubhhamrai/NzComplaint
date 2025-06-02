package com.clarivate.NzComplaints.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NzHomepagePage {

    public void navigateToWebsite(WebDriver driver, WebDriverWait wait, String url) {
        driver.get(url);
        clickClassificationStatus(driver, wait);
    }

    public void clickClassificationStatus(WebDriver driver, WebDriverWait wait) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("MainContent_ctrlTMSearch_hdrClassifStatusCriteria_lblheader"))).click();
        clickSelectStatus(driver, wait);
    }

    public void clickSelectStatus(WebDriver driver, WebDriverWait wait) {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("MainContent_ctrlTMSearch_ctrlCaseStatusSearchDialog_lnkBtnSearch"))).click();
    }

    public void clickSearchButton(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.id("MainContent_ctrlTMSearch_lnkbtnSearch"))).click();
        sortByCaseNumber(driver, wait);
    }

    public void sortByCaseNumber(WebDriver driver, WebDriverWait wait) throws InterruptedException {
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, 'Sort$NRPROC')]"))).click();
        Thread.sleep(10000); // Consider using wait instead of Thread.sleep if possible
        openFirstCase(driver, wait);
    }

    public void openFirstCase(WebDriver driver, WebDriverWait wait) {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("loader")));
        wait.withTimeout(Duration.ofSeconds(12)); // Extending timeout for the next operation
        WebElement firstCase = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a[id*='gvwIPCases_lnkBtnCaseBrowser_3']")));
        firstCase.click();
    }
}
