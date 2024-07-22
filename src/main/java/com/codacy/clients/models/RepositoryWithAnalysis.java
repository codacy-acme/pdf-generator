package com.codacy.clients.models;

import java.util.List;

public class RepositoryWithAnalysis {

    public RepositoryWithAnalysis(String provider, String owner, String repository, List<String> languages,
            String grade, int issuesCount, int complexFilesCount) {
        super();
        this.provider = provider;
        this.owner = owner;
        this.repository = repository;
        this.languages = languages;
        this.grade = grade;
        this.issuesCount = issuesCount;
        this.complexFilesCount = complexFilesCount;
    }

    private String provider;
    private String owner;
    private String repository;
    private List<String> languages;
    private String grade;

    private int issuesCount;

    private int complexFilesCount;

    public int getComplexFilesCount() {
        return complexFilesCount;
    }

    public String getProvider() {
        return provider;
    }

    public String getOwner() {
        return owner;
    }

    public String getRepository() {
        return repository;
    }

    public String getGrade() {
        return grade;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public int getIssuesCount() {
        return issuesCount;
    }
}
