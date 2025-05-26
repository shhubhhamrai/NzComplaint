package com.clarivate.NzComplaints.pages;

import com.clarivate.NzComplaints.dto.BibliographicData;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class BibliographicDataPage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public BibliographicDataPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    private String getTextOrValue(WebElement element) {
        String tag = element.getTagName();
        if ("input".equalsIgnoreCase(tag) || "textarea".equalsIgnoreCase(tag)) {
            String val = element.getAttribute("value");
            return val != null ? val.trim() : "";
        }
        return element.getText().trim();
    }

    public String getApplicationNumber() {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("MainContent_ctrlTM_txtAppNr")));
            return getTextOrValue(element);
        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("Error extracting application number: " + e.getMessage());
            return "not found";
        }
    }

    public String getApplicantName() {
        try {
            WebElement applicantCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@id='MainContent_ctrlTM_ctrlApplicant_ctrlApplicant_gvCustomers']//tr[contains(@class,'alt1')]/td[2]")));
            return applicantCell.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("Error extracting applicant name: " + e.getMessage());
            return "not found";
        }
    }

    public String getApplicantAddress() {
        try {
            WebElement addressCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@id='MainContent_ctrlTM_ctrlApplicant_ctrlApplicant_gvCustomers']//tr[contains(@class,'alt1')]/td[3]")));
            return addressCell.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("Error extracting applicant address: " + e.getMessage());
            return "not found";
        }
    }

    public String getTrademarkClass() {
        try {
            WebElement classCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@id='MainContent_ctrlTM_ctrlClassif_gvClassifications']//tr[contains(@class,'alt1')]/td[1]")));
            return classCell.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("Error extracting trademark class: " + e.getMessage());
            return "not found";
        }
    }

    public String getMarkType() {
        try {
            WebElement markTypeTd = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//tr[@id='MainContent_ctrlTM_trTMType']//td[contains(@class,'data')]")));
            return markTypeTd.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("Error extracting mark type: " + e.getMessage());
            return "not found";
        }
    }

    public String getMarkName() {
        try {
            WebElement markNameTd = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//tr[@id='MainContent_ctrlTM_trTMName']//td[contains(@class,'data')]")));
            return markNameTd.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("Error extracting mark name: " + e.getMessage());
            return "not found";
        }
    }

    public String getRedParty() {
        try {
            WebElement table = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("MainContent_ctrlProcedureList_gvwIPCases")));
            List<WebElement> rows = table.findElements(By.xpath(".//tr[contains(@class,'alt')]"));
            for (WebElement row : rows) {
                List<WebElement> tds = row.findElements(By.tagName("td"));
                if (tds.size() >= 7) {
                    String caseType = tds.get(1).getText().trim();
                    if (caseType.contains("Proceedings")) {
                        return tds.get(6).getText().trim();
                    }
                }
            }
            return "not found";
        } catch (NoSuchElementException | TimeoutException e) {
            System.err.println("Error extracting Red Party info: " + e.getMessage());
            return "not found";
        }
    }

    public String[] getFirstActionDetails() {
        try {
            WebElement historyTab = wait.until(ExpectedConditions.elementToBeClickable(By.id("ui-id-2")));
            historyTab.click();
            Thread.sleep(1000);

            WebElement historyTable = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("MainContent_ctrlHistoryList_gvHistory")));
            WebElement firstRow = historyTable.findElement(By.xpath(".//tr[contains(@class,'alt1')]"));

            String firstActionType = firstRow.findElement(By.xpath("./td[1]")).getText().trim();
            String firstActionDate = firstRow.findElement(By.xpath("./td[3]")).getText().trim();

            return new String[]{firstActionType, firstActionDate};
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while waiting for history tab: " + ie.getMessage());
            return new String[]{"not found", "not found"};
        } catch (Exception e) {
            System.err.println("Error extracting first action details: " + e.getMessage());
            return new String[]{"not found", "not found"};
        }
    }

    public void clickDocumentsTab() {
        try {
            WebElement documentsTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='#MainContent_tabDocuments' and contains(text(),'Documents')]")
            ));
            documentsTab.click();
            // Wait a short time for the tab content to load
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while clicking Documents tab: " + ie.getMessage());
        } catch (Exception e) {
            System.err.println("Error clicking Documents tab: " + e.getMessage());
        }
    }

    public void clickFirstDocumentLink() {
        try {
            WebElement firstDocLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("MainContent_ctrlDocumentList_gvDocuments_hnkView_0")
            ));
            firstDocLink.click();
            // Optionally wait a moment for the download to initiate
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            System.err.println("Interrupted while clicking first document link: " + ie.getMessage());
        } catch (Exception e) {
            System.err.println("Error clicking first document link: " + e.getMessage());
        }
    }

    public BibliographicData readBibliographicData() {
        BibliographicData data = new BibliographicData();
        data.setApplicationNumber(getApplicationNumber());
        data.setApplicantName(getApplicantName());
        data.setApplicantAddress(getApplicantAddress());
        data.setTrademarkClass(getTrademarkClass());
        data.setMarkType(getMarkType());
        data.setMarkName(getMarkName());
        data.setRedParty(getRedParty());

        String[] actionDetails = getFirstActionDetails();
        data.setFirstActionType(actionDetails[0]);
        data.setFirstActionDate(actionDetails[1]);

        return data;
    }
}
