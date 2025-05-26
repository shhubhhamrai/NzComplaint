package com.clarivate.NzComplaints.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class CaseStatusDialogPage {

    private final WebDriver driver;
    private final JavascriptExecutor js;

    public CaseStatusDialogPage(WebDriver driver, JavascriptExecutor js) {
        this.driver = driver;
        this.js = js;
    }

    public void selectCaseStatusesAndConfirm() throws InterruptedException {
        // Checkbox 7
        WebElement checkbox7 = driver.findElement(By.id(
                "MainContent_ctrlTMSearch_ctrlCaseStatusSearchDialog_ctrlCaseStatusSearch_ctrlCaseStatusList_gvCaseStatuss_chckbxSelected_7"
        ));
        if (!checkbox7.isSelected()) {
            checkbox7.click();
        }

        // Checkbox 8
        WebElement checkbox8 = driver.findElement(By.id(
                "MainContent_ctrlTMSearch_ctrlCaseStatusSearchDialog_ctrlCaseStatusSearch_ctrlCaseStatusList_gvCaseStatuss_chckbxSelected_8"
        ));
        if (!checkbox8.isSelected()) {
            checkbox8.click();
        }

        // Optional: small wait
        Thread.sleep(500);

        // Click the "Select" button
        WebElement selectButton = driver.findElement(By.id(
                "MainContent_ctrlTMSearch_ctrlCaseStatusSearchDialog_lnkBtnSelect"
        ));
        selectButton.click();

        Thread.sleep(1000); // Optional: allow time for dialog to close
    }
}