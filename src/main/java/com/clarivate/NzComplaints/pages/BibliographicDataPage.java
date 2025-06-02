package com.clarivate.NzComplaints.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

public class BibliographicDataPage {
    private static final Logger logger = LoggerFactory.getLogger(BibliographicDataPage.class);

//    private final WebDriverWait wait;

//    public BibliographicDataPage(WebDriver driver) {
//        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
//    }

    private String getTextOrValue(WebElement element) {
        String tag = element.getTagName();
        if ("input".equalsIgnoreCase(tag) || "textarea".equalsIgnoreCase(tag)) {
            String val = element.getAttribute("value");
            return val != null ? val.trim() : "";
        }
        return element.getText().trim();
    }

    public String getApplicationNumber(WebDriver driver,WebDriverWait wait) {
        try {
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("MainContent_ctrlTM_txtAppNr")));
            return getTextOrValue(element);
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Error extracting application number", e);
            return "not found";
        }
    }

    public String getApplicantName(WebDriver driver,WebDriverWait wait) {
        try {
            WebElement applicantCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@id='MainContent_ctrlTM_ctrlApplicant_ctrlApplicant_gvCustomers']//tr[contains(@class,'alt1')]/td[2]")));
            return applicantCell.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Error extracting applicant name", e);
            return "not found";
        }
    }

    public String getApplicantAddress(WebDriver driver,WebDriverWait wait) {
        try {
            WebElement addressCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@id='MainContent_ctrlTM_ctrlApplicant_ctrlApplicant_gvCustomers']//tr[contains(@class,'alt1')]/td[3]")));
            return addressCell.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Error extracting applicant address", e);
            return "not found";
        }
    }

    public String getTrademarkClass(WebDriver driver,WebDriverWait wait) {
        try {
            WebElement classCell = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//table[@id='MainContent_ctrlTM_ctrlClassif_gvClassifications']//tr[contains(@class,'alt1')]/td[1]")));
            return classCell.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Error extracting trademark class", e);
            return "not found";
        }
    }

    public String getMarkType(WebDriver driver,WebDriverWait wait) {
        try {
            WebElement markTypeTd = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//tr[@id='MainContent_ctrlTM_trTMType']//td[contains(@class,'data')]")));
            return markTypeTd.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Error extracting mark type", e);
            return "not found";
        }
    }

    public String getMarkName(WebDriver driver,WebDriverWait wait) {
        try {
            WebElement markNameTd = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//tr[@id='MainContent_ctrlTM_trTMName']//td[contains(@class,'data')]")));
            return markNameTd.getText().trim();
        } catch (NoSuchElementException | TimeoutException e) {
            logger.error("Error extracting mark name", e);
            return "not found";
        }
    }

    public String getRedParty(WebDriver driver,WebDriverWait wait) {
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
            logger.error("Error extracting Red Party info", e);
            return "not found";
        }
    }

    public String[] getFirstActionDetails(WebDriver driver,WebDriverWait wait) {
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
            logger.error("Interrupted while waiting for history tab", ie);
            return new String[]{"not found", "not found"};
        } catch (Exception e) {
            logger.error("Error extracting first action details", e);
            return new String[]{"not found", "not found"};
        }
    }

    public String getImageAsBase64(WebDriver driver) {
        try {
            WebElement imageLink = driver.findElement(By.id("MainContent_ctrlTM_ctrlPictureList_lvDocumentView_hlnkCasePicture_0"));
            String imageUrl = imageLink.getAttribute("href");
            if (imageUrl != null && !imageUrl.isBlank()) {
                byte[] imageBytes = downloadImageBytes(imageUrl);
                if (imageBytes != null) {
                    return "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(imageBytes);
                }
            }
        } catch (Exception e) {
            logger.error("Error retrieving base64 image", e);
        }
        return null;
    }

    private byte[] downloadImageBytes(String imageUrl) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(imageUrl).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.connect();
        try (InputStream in = connection.getInputStream()) {
            return in.readAllBytes();
        }
    }

    public void clickDocumentsTab(WebDriver driver,WebDriverWait wait) {
        try {
            WebElement documentsTab = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[@href='#MainContent_tabDocuments' and contains(text(),'Documents')]")));
            documentsTab.click();
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while clicking Documents tab", ie);
        } catch (Exception e) {
            logger.error("Error clicking Documents tab", e);
        }
    }

    public void clickFirstDocumentLink(WebDriver driver,WebDriverWait wait) {
        try {
            WebElement firstDocLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.id("MainContent_ctrlDocumentList_gvDocuments_hnkView_0")));
            firstDocLink.click();
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            logger.error("Interrupted while clicking first document link", ie);
        } catch (Exception e) {
            logger.error("Error clicking first document link", e);
        }
    }
}
