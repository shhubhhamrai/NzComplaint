package com.clarivate.NzComplaints.service;

import com.clarivate.NzComplaints.dto.BibliographicData;
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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NzipotmComplaintService {

    private static final String IPONZ_URL = "https://app.iponz.govt.nz/app/Extra/IP/TM/Qbe.aspx?sid=638835487657819172&op=EXTRA_tm_qbe&fcoOp=EXTRA__Default&directAccess=true";
    private static final String DOWNLOAD_DIR = System.getProperty("user.home") + "/Downloads";

    public Binder runRobot() {
        Binder binder = new Binder();


        EdgeOptions options = new EdgeOptions();
        options.addArguments("--start-maximized");

        WebDriver driver = new EdgeDriver(options);
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            // Initialize pages
            NzHomepagePage homepage = new NzHomepagePage(driver, js);
            CaseStatusDialogPage statusDialog = new CaseStatusDialogPage(driver, js);

            homepage.navigateToWebsite(IPONZ_URL);
            homepage.clickClassificationStatus();
            homepage.clickSelectStatus();
            statusDialog.selectCaseStatusesAndConfirm();
            Thread.sleep(5000);
            homepage.clickSearchButton();
            Thread.sleep(10000);

            homepage.sortByCaseNumber();
            Thread.sleep(10000);

            homepage.openFirstCase();
            Thread.sleep(10000);

            BibliographicDataPage biblioPage = new BibliographicDataPage(driver);
            BibliographicData biblio = biblioPage.readBibliographicData();

            String applicationNo = biblio.getApplicationNumber();
            String firstActionType = biblio.getFirstActionType();
            LocalDate firstActionDate = parseDate(biblio.getFirstActionDate());

            binder.setId(UUID.randomUUID().toString());
            binder.setFirstAction(firstActionType);
            binder.setFirstActionDate(firstActionDate);
            binder.setDomains(List.of("TM", "CR", "DM", "PT"));

            String decisionReference = "nz-nzipotm-op-" + applicationNo + "_" + firstActionDate.format(DateTimeFormatter.BASIC_ISO_DATE);

            Decision decision = new Decision();
            decision.setId(UUID.randomUUID().toString());
            decision.setReference(decisionReference);
            decision.setJudgmentDate(firstActionDate);
            decision.setNature("Complaints & Hearings");
            decision.setLevel("Opposition");
            decision.setRobotSource("NZ_IPONZ_TRADEMARKS");

            binder.setDecisions(List.of(decision));

            Party applicant = new Party();
            applicant.setName(biblio.getApplicantName());
            applicant.setType("Applicant");

            Party redParty = new Party();
            redParty.setName(biblio.getRedParty());
            redParty.setType("Red Party");

            binder.setParties(List.of(applicant, redParty));

            Classification classification = new Classification();
            classification.setName(biblio.getMarkName());
            classification.setType(biblio.getMarkType());
            classification.setClassName(biblio.getTrademarkClass());

            Right right = new Right();
            right.setId("right1");
            right.setOpponent(false);
            right.setClassification(classification);
            right.setName(biblio.getMarkName());
            right.setType("TM");
            right.setReference(applicationNo);

            binder.setRights(List.of(right));

            Docket docket = new Docket();
            docket.setId("docket1");
            docket.setReference(decisionReference);
            docket.setCourtId("court1");
            docket.setJudge("Judge X");

            binder.setDockets(List.of(docket));

            // --- Logging existing PDFs before download ---
            System.out.println("Existing PDF files before download:");
            listPdfFiles();

            // --- Download document ---
            biblioPage.clickDocumentsTab();

            // Get current files before clicking document link
            Set<Path> beforeFiles = listPdfFileSet();

            biblioPage.clickFirstDocumentLink();

            // Wait for new PDF to appear in the folder
            Path downloadedPdf = waitForNewDownload(beforeFiles, ".pdf", 30);
            if (downloadedPdf != null) {
                // Create unique filename with timestamp
                String uniqueFileName = decisionReference + "_" + System.currentTimeMillis() + ".pdf";
                Path renamedPdf = Paths.get(DOWNLOAD_DIR, uniqueFileName);
                Files.move(downloadedPdf, renamedPdf, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("PDF downloaded and renamed to: " + renamedPdf.getFileName());
            } else {
                System.out.println("PDF not found or download timed out.");
            }

            // --- Logging PDFs after download ---
            System.out.println("PDF files after download:");
            listPdfFiles();

            // Export JSON with JavaTimeModule
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            Path jsonOutput = Paths.get(DOWNLOAD_DIR, decisionReference + ".js");
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(binder);
            Files.writeString(jsonOutput, json);
            System.out.println("JSON exported to: " + jsonOutput.getFileName());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return binder;
    }

    private LocalDate parseDate(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH);
        return LocalDate.parse(dateStr, formatter);
    }

    /**
     * Waits for a new file with the specified extension to appear in the directory,
     * ignoring files already present in beforeFiles.
     */
    private Path waitForNewDownload(Set<Path> beforeFiles, String extension, int timeoutSeconds) throws InterruptedException, IOException {
        Path dir = Paths.get(DOWNLOAD_DIR);

        for (int i = 0; i < timeoutSeconds; i++) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*" + extension)) {
                for (Path file : stream) {
                    if (!beforeFiles.contains(file)) {
                        // Ignore temp download files
                        Path crDownload = Paths.get(file.toString() + ".crdownload");
                        if (Files.exists(file) && !Files.exists(crDownload)) {
                            // Confirm the file is not growing (still writing)
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

    /**
     * Lists all PDFs in the download directory.
     */
    private void listPdfFiles() throws IOException {
        Files.list(Paths.get(DOWNLOAD_DIR))
                .filter(p -> p.toString().endsWith(".pdf"))
                .forEach(p -> System.out.println(" - " + p.getFileName()));
    }

    /**
     * Returns a Set of PDF files currently in the download directory.
     */
    private Set<Path> listPdfFileSet() throws IOException {
        Set<Path> set = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(DOWNLOAD_DIR), "*.pdf")) {
            for (Path file : stream) {
                set.add(file);
            }
        }
        return set;
    }

}
