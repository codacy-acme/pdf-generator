package com.codacy.clients;

import com.codacy.clients.models.Count;
import com.codacy.clients.models.IssuesOverview;
import com.codacy.clients.models.RepositoryIssue;
import com.codacy.clients.models.RepositoryWithAnalysis;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CodacyAPIClient {
    private static final String API_URL = "https://app.codacy.com/api/v3";
    private final String apiToken;

    public CodacyAPIClient(String apiToken) {
        this.apiToken = apiToken;
    }

    public int getRepositoryIssues(String organization, String repository) throws IOException {
        String url = String.format("%s/organizations/%s/repositories/%s/issues", API_URL, organization, repository);
        HttpGet request = new HttpGet(url);
        request.addHeader("api-token", apiToken);

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getEntity().getContent());

            // Assuming the response contains a field "total" with the number of issues
            return root.path("total").asInt();
        }
    }

    public RepositoryWithAnalysis getRepositoryWithAnalysis(String provider, String organization, String repository)
            throws IOException {
        String url = String.format("%s/analysis/organizations/%s/%s/repositories/%s", API_URL, provider, organization,
                repository);
        HttpGet request = new HttpGet(url);
        request.addHeader("api-token", apiToken);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getEntity().getContent());

            Iterator<JsonNode> languagesIterator = root.get("data").get("repository").withArray("languages").elements();
            List<String> languages = new ArrayList<String>();
            while (languagesIterator.hasNext()) {
                JsonNode node = languagesIterator.next();
                languages.add(node.asText());
            }

            String grade = root.get("data").get("gradeLetter").asText();
            String providerName = root.get("data").get("repository").get("provider").asText();
            String organizationName = root.get("data").get("repository").get("owner").asText();
            String repositoryName = root.get("data").get("repository").get("name").asText();

            int issuesCount = root.get("data").get("issuesCount").asInt();
            int complexFilesCount = root.get("data").get("complexFilesCount").asInt();

            RepositoryWithAnalysis rwa = new RepositoryWithAnalysis(providerName, organizationName, repositoryName,
                    languages, grade, issuesCount, complexFilesCount);
            return rwa;
        }
    }

    public IssuesOverview gerIssuesOverview(String provider, String organization, String repository)
            throws IOException {
        String url = String.format("%s/analysis/organizations/%s/%s/repositories/%s/issues/overview", API_URL, provider,
                organization,
                repository);
        HttpPost request = new HttpPost(url);
        request.addHeader("api-token", apiToken);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
                CloseableHttpResponse response = httpClient.execute(request)) {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getEntity().getContent()).get("data").get("counts");

            List<Count> categories = new ArrayList<Count>();
            List<Count> languages = new ArrayList<Count>();
            List<Count> levels = new ArrayList<Count>();
            List<Count> patterns = new ArrayList<Count>();
            Iterator<JsonNode> categoriesIterator = root.withArray("categories").elements();
            while (categoriesIterator.hasNext()) {
                JsonNode node = categoriesIterator.next();
                categories.add(new Count(node.get("name").asText(), node.get("total").asInt()));
            }

            Iterator<JsonNode> languagesIterator = root.withArray("languages").elements();
            while (languagesIterator.hasNext()) {
                JsonNode node = languagesIterator.next();
                languages.add(new Count(node.get("name").asText(), node.get("total").asInt()));
            }

            Iterator<JsonNode> levelsIterator = root.withArray("levels").elements();
            while (levelsIterator.hasNext()) {
                JsonNode node = levelsIterator.next();
                levels.add(new Count(node.get("name").asText(), node.get("total").asInt()));
            }

            Iterator<JsonNode> patternsIterator = root.withArray("patterns").elements();
            while (patternsIterator.hasNext()) {
                JsonNode node = patternsIterator.next();
                patterns.add(new Count(node.get("title").asText(), node.get("total").asInt()));
            }
            IssuesOverview io = new IssuesOverview(categories, languages, levels, patterns);
            return io;
        }
    }

    public List<RepositoryIssue> gerRepositoryIssues(String provider, String organization, String repository)
            throws IOException {
        String cursor = "";
        boolean hasNext = true;
        List<RepositoryIssue> issues = new ArrayList<RepositoryIssue>();

        while (hasNext) {
            String url = String.format("%s/analysis/organizations/%s/%s/repositories/%s/issues/search?limit=100%s",
                    API_URL,
                    provider,
                    organization, repository, cursor);
            HttpPost request = new HttpPost(url);
            request.addHeader("api-token", apiToken);
            try (CloseableHttpClient httpClient = HttpClients.createDefault();
                    CloseableHttpResponse response = httpClient.execute(request)) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response.getEntity().getContent());

                Iterator<JsonNode> issuesIterator = root.withArray("data")
                        .elements();
                while (issuesIterator.hasNext()) {
                    JsonNode node = issuesIterator.next();
                    String filePath = node.get("filePath").asText();
                    String lineText = node.get("lineText").asText();
                    int lineNumber = node.get("lineNumber").asInt();
                    String patternTitle = node.get("patternInfo").get("title") != null
                            ? node.get("patternInfo").get("title").asText()
                            : node.get("patternInfo").get("id").asText();
                    String patternCategory = node.get("patternInfo").get("category").asText();
                    String patternSubcategory = node.get("patternInfo").get("subCategory") != null
                            ? node.get("patternInfo").get("subCategory").asText()
                            : "";
                    String patternLevel = node.get("patternInfo").get("level").asText();
                    String patternSeverity = node.get("patternInfo").get("severityLevel").asText();
                    RepositoryIssue ri = new RepositoryIssue(filePath, lineText, lineNumber, patternTitle,
                            patternCategory, patternSubcategory, patternLevel, patternSeverity);
                    issues.add(ri);
                }

                JsonNode pagination = root.get("pagination");
                if (pagination != null) {
                    JsonNode cursorNode = pagination.get("cursor");
                    if (cursorNode != null) {
                        cursor = String.format("&cursor=%s", cursorNode.asText());
                        hasNext = true;
                    } else {
                        hasNext = false;
                    }
                } else {
                    hasNext = false;
                }

            }
        }
        return issues;
    }
}
