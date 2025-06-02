package com.clarivate.NzComplaints.service;

import com.clarivate.NzComplaints.models.*;
import com.clarivate.NzComplaints.pages.BibliographicDataPage;
import com.clarivate.NzComplaints.pages.CaseStatusDialogPage;
import com.clarivate.NzComplaints.pages.NzHomepagePage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NzipotmComplaintService {

    private static final String IPONZ_URL = "https://app.iponz.govt.nz/app/Extra/IP/TM/Qbe.aspx?sid=638835487657819172&op=EXTRA_tm_qbe&fcoOp=EXTRA__Default&directAccess=true";
    private static final String DOWNLOAD_DIR = System.getProperty("user.home") + "/Downloads";
    private static final Logger logger = LoggerFactory.getLogger(NzipotmComplaintService.class);
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    private NzHomepagePage homepage;
    private CaseStatusDialogPage statusDialog;
    private BibliographicDataPage biblioPage;

    private Binder binder;

    private String applicationNo;
    private String applicantName;
    private String applicantAddress;
    private String markName;
    private String markType;
    private String trademarkClass;
    private String redParty;
    private String firstActionType;
    private String firstActionDateStr;
    private String base64Image;

    public Binder runRobot() {
        binder = new Binder();
        initializeDriver();
        try {
            loadHomepage();
        } catch (Exception e) {
            logger.error("Error in loadHomepage: {}", e.getMessage(), e);
        } finally {
            driver.quit();
        }
        return binder;
    }

    // ----------------------- Initialization -----------------------

    private void initializeDriver() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");
        driver = new EdgeDriver(options);
        js = (JavascriptExecutor) driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    private void loadHomepage() throws Exception {
        homepage = new NzHomepagePage();
        statusDialog = new CaseStatusDialogPage();

        homepage.navigateToWebsite(driver,wait,IPONZ_URL);
        statusDialog.selectCaseStatusesAndConfirm(driver,wait);
        Thread.sleep(5000);
        searchCases();
    }

    private void searchCases() throws Exception {
        homepage.clickSearchButton(driver,wait);
        biblioPage = new BibliographicDataPage();
        extractBibliographicData(); // Proceed to extraction
    }

    // ----------------------- Data Extraction -----------------------

    private void extractBibliographicData() throws Exception {
        applicationNo = biblioPage.getApplicationNumber(driver, wait);
        applicantName = biblioPage.getApplicantName(driver, wait);
        applicantAddress = biblioPage.getApplicantAddress(driver, wait);
        markName = biblioPage.getMarkName(driver, wait);
        markType = biblioPage.getMarkType(driver, wait);
        trademarkClass = biblioPage.getTrademarkClass(driver, wait);
        redParty = biblioPage.getRedParty(driver, wait);

        String[] actionDetails = biblioPage.getFirstActionDetails(driver, wait);
        firstActionType = actionDetails[0];
        firstActionDateStr = actionDetails[1];

        try {
            base64Image = biblioPage.getImageAsBase64(driver);
        } catch (Exception e) {
            base64Image = "not found";
            logger.warn("Image extraction failed: {}", e.getMessage(), e);
        }

        populateBinder();
    }

    private void populateBinder() throws Exception {
        LocalDate firstActionDate = parseDate(firstActionDateStr);

        binder.setId(UUID.randomUUID().toString());
        binder.setFirstAction(firstActionType);
        binder.setFirstActionDate(firstActionDate);
        binder.setDomains(List.of("TM", "CR", "DM", "PT"));

        String decisionRef = "nz-nzipotm-op-" + applicationNo + "_" + firstActionDate.format(DateTimeFormatter.BASIC_ISO_DATE) + "_Complaint_IS";
        Decision decision = new Decision();
        decision.setId(UUID.randomUUID().toString());
        decision.setReference(decisionRef);
        decision.setJudgmentDate(firstActionDate);
        decision.setNature("Complaints & Hearings");
        decision.setLevel("Opposition");
        decision.setRobotSource("NZ_IPONZ_TRADEMARKS");
        binder.setDecisions(List.of(decision));

        Party applicant = new Party();
        applicant.setName(applicantName);
        applicant.setType("Applicant");

        Party red = new Party();
        red.setName(redParty);
        red.setType("Red Party");

        binder.setParties(List.of(applicant, red));

        Classification classification = new Classification();
        classification.setName(markName);
        classification.setType(markType);
        classification.setClassName(trademarkClass);
        classification.setImage(base64Image);

        Right right = new Right();
        right.setId("right1");
        right.setOpponent(false);
        right.setClassification(classification);
        right.setName(markName);
        right.setType("TM");
        right.setReference(applicationNo);

        binder.setRights(List.of(right));

        String docketRef = "nz-nzipotm-op-" + applicationNo + "_" + firstActionDate.format(DateTimeFormatter.BASIC_ISO_DATE);
        Docket docket = new Docket();
        docket.setId("docket1");
        docket.setReference(docketRef);
        docket.setCourtId("court1");
        docket.setJudge("Judge X");

        binder.setDockets(List.of(docket));

        downloadAndRenameDocument();
    }

    // ----------------------- Document Handling -----------------------

    private void downloadAndRenameDocument() throws IOException {
        try {
            biblioPage.clickDocumentsTab(driver,wait);
            Set<Path> beforeFiles = listPdfFileSet();
            biblioPage.clickFirstDocumentLink(driver,wait);

            Path downloadedPdf = waitForNewDownload(beforeFiles, ".pdf", 30);
            if (downloadedPdf != null) {
                String fileName = "nz-nzipotm-op-" + applicationNo + "_" +
                        binder.getFirstActionDate().format(DateTimeFormatter.BASIC_ISO_DATE) + "_Complaint_IS_.pdf";
                Path renamedPdf = Paths.get(DOWNLOAD_DIR, fileName);
                Files.move(downloadedPdf, renamedPdf, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("PDF downloaded and renamed to: " + renamedPdf.getFileName());
            } else {
                logger.warn("PDF not found or download timed out.");
            }
        } catch (Exception e) {
            logger.error("Error during PDF download and renaming: {}", e.getMessage(), e);
        }

        exportBinderToJson();
    }

    // ----------------------- JSON Export -----------------------

    private void exportBinderToJson() throws IOException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            String fileName = "nz-nzipotm-op-" + applicationNo + "_" +
                    binder.getFirstActionDate().format(DateTimeFormatter.BASIC_ISO_DATE) + "_Complaint_IS.js";
            Path jsonOutput = Paths.get(DOWNLOAD_DIR, fileName);
            Files.writeString(jsonOutput, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(binder));
            logger.info("JSON exported to: {}", jsonOutput.getFileName());
        } catch (IOException e) {
            logger.error("Error exporting JSON: {}", e.getMessage(), e);
        }
    }

    // ----------------------- Utilities -----------------------

    private LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateStr, formatter);
    }

    private Set<Path> listPdfFileSet() throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DOWNLOAD_DIR), "*.pdf")) {
            Set<Path> set = new HashSet<>();
            for (Path file : stream) set.add(file);
            return set;
        }
    }

    private Path waitForNewDownload(Set<Path> beforeFiles, String extension, int timeoutSeconds) throws InterruptedException, IOException {
        Path dir = Paths.get(DOWNLOAD_DIR);
        for (int i = 0; i < timeoutSeconds; i++) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*" + extension)) {
                for (Path file : stream) {
                    if (!beforeFiles.contains(file)) {
                        Path crDownload = Paths.get(file.toString() + ".crdownload");
                        if (Files.exists(file) && !Files.exists(crDownload)) {
                            long size1 = Files.size(file);
                            Thread.sleep(1000);
                            long size2 = Files.size(file);
                            if (size1 == size2) {
                                return file;
                            }
                        }
                    }
                }
            }
            Thread.sleep(1000);
        }
        return null;
    }
}
