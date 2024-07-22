package com.codacy;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.codacy.clients.CodacyAPIClient;
import com.codacy.clients.models.Count;
import com.codacy.clients.models.IssuesOverview;
import com.codacy.clients.models.RepositoryIssue;
import com.codacy.clients.models.RepositoryWithAnalysis;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        try {
            if (args.length < 4) {
                System.out.println("Usage: java -jar pdf_generator.jar <provider> <organization> <repository> <api-token>");
                System.exit(1);
            }

            String provider = args[0];
            String organization = args[1];
            String repository = args[2];
            String apiToken = args[3];

            CodacyAPIClient cac = new CodacyAPIClient(apiToken);
            RepositoryWithAnalysis rwa = cac.getRepositoryWithAnalysis(provider, organization, repository);
            IssuesOverview io = cac.gerIssuesOverview(provider, organization, repository);
            List<RepositoryIssue> issues = cac.gerRepositoryIssues(provider, organization, repository);

            createHelloWorldPDF("audit_report.pdf", rwa, io, issues);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    private static void renderLabelAndValue(PDPageContentStream contentStream, String label, String value)
            throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
        contentStream.showText(String.format("%s: ", label));
        contentStream.setFont(PDType1Font.HELVETICA, 8);
        contentStream.showText(value);
        contentStream.newLineAtOffset(0, -15);
    }

    private static void renderTitle(PDPageContentStream contentStream, String title) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
        contentStream.showText(title);
        contentStream.newLineAtOffset(0, -20);
    }

    private static void renderSubTitle(PDPageContentStream contentStream, String title) throws IOException {
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
        contentStream.newLineAtOffset(0, -5);
        contentStream.showText(title);
        contentStream.newLineAtOffset(0, -15);
    }

    private static void createHelloWorldPDF(String fileName, RepositoryWithAnalysis rwa, IssuesOverview io,
            List<RepositoryIssue> issues) {
        PDDocument document = new PDDocument();
        
        // Doc cover
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(100, 700);
            renderTitle(contentStream, "Codacy Audit Report");
            renderSubTitle(contentStream, "Summary");
            renderLabelAndValue(contentStream, "Provider", rwa.getProvider());
            renderLabelAndValue(contentStream, "Owner", rwa.getOwner());
            renderLabelAndValue(contentStream, "Repository", rwa.getRepository());
            renderLabelAndValue(contentStream, "Grade", rwa.getGrade());
            renderLabelAndValue(contentStream, "Languages", String.join(", ", rwa.getLanguages()));
            renderLabelAndValue(contentStream, "Issues Count", String.valueOf(rwa.getIssuesCount()));
            renderLabelAndValue(contentStream, "Complex Files", String.valueOf(rwa.getComplexFilesCount()));
            renderLabelAndValue(contentStream, "Date", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Issues Overview
        PDPage issuesOverviewPage = new PDPage();
        document.addPage(issuesOverviewPage);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, issuesOverviewPage)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(100, 700);
            renderTitle(contentStream, "Issues Overview");
            renderSubTitle(contentStream, "Categories");

            for (Count item : io.getCategories()) {
                renderLabelAndValue(contentStream, item.getName(), String.valueOf(item.getTotal()));

            }

            renderSubTitle(contentStream, "Languages");

            for (Count item : io.getLanguages()) {
                renderLabelAndValue(contentStream, item.getName(), String.valueOf(item.getTotal()));
            }
            renderSubTitle(contentStream, "Levels");

            for (Count item : io.getLevels()) {
                renderLabelAndValue(contentStream, item.getName(), String.valueOf(item.getTotal()));
            }

            contentStream.newLineAtOffset(0, -20);
            contentStream.endText();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Most Common Patterns
        List<Count> patterns = io.getPatterns();
        int patternsPerPage = 40;
        AtomicInteger patternsCounter = new AtomicInteger();
        Map<Integer, List<Count>> mapOfPatternChunks = patterns.stream()
                .collect(Collectors.groupingBy(it -> patternsCounter.getAndIncrement() / patternsPerPage));
        List<List<Count>> listOfPatternsChunks = new ArrayList<>(mapOfPatternChunks.values());
        int pageCounter = 0;
        for (List<Count> patternsList : listOfPatternsChunks) {
            PDPage patternsPage = new PDPage();
            document.addPage(patternsPage);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, patternsPage)) {
                contentStream.beginText();
                contentStream.newLineAtOffset(100, 700);
                renderTitle(contentStream,
                        String.format("Detected Patterns - Page %s of %s", ++pageCounter, listOfPatternsChunks.size()));
                for (Count item : patternsList) {
                    renderLabelAndValue(contentStream, item.getName(), String.valueOf(item.getTotal()));
                }
                contentStream.endText();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // issues list
        int groupSize = 10;
        AtomicInteger counter = new AtomicInteger();
        Map<Integer, List<RepositoryIssue>> mapOfChunks = issues.stream()
                .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / groupSize));
        // Create a list containing the lists of chunks
        List<List<RepositoryIssue>> listOfChunks = new ArrayList<>(mapOfChunks.values());
        pageCounter = 0;
        for (List<RepositoryIssue> issuesList : listOfChunks) {
            PDPage issuesPage = new PDPage();
            document.addPage(issuesPage);
            try (PDPageContentStream contentStream = new PDPageContentStream(document, issuesPage)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(100, 700);
                renderTitle(contentStream,
                        String.format("Issues List - Page %s of %s", ++pageCounter, listOfChunks.size()));

                for (RepositoryIssue issue : issuesList) {
                    contentStream.newLineAtOffset(0, -10);
                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 8);
                    contentStream.showText(String.format("(%s) %s - %s %s", issue.getPatternCategory(),
                            issue.getPatternTitle(), issue.getPatternLevel(), issue.getPatternSeverity()));
                    contentStream.setFont(PDType1Font.HELVETICA, 8);
                    contentStream.newLineAtOffset(0, -10);
                    contentStream.showText(String.format("%s [Ln %s]", issue.getFilePath(), issue.getLineNumber()));
                    contentStream.newLineAtOffset(0, -10);
                    contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
                    contentStream.showText(issue.getLineText());
                    contentStream.newLineAtOffset(0, -10);
                }
                contentStream.endText();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            document.save(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                document.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}