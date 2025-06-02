package com.clarivate.NzComplaints.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

public class CaseStatusDialogPage {


    public void selectCaseStatusesAndConfirm(WebDriver driver, WebDriverWait wait) throws InterruptedException {
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