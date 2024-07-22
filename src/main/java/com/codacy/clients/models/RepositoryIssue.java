package com.codacy.clients.models;

public class RepositoryIssue {

    private String filePath;
    private String lineText;
    private int lineNumber;

    private String patternTitle;
    private String patternCategory;
    private String patternSubcategory;
    private String patternLevel;
    private String patternSeverity;

    public RepositoryIssue(String filePath, String lineText, int lineNumber, String patternTitle,
            String patternCategory, String patternSubcategory, String patternLevel, String patternSeverity) {
        this.filePath = filePath;
        this.lineText = lineText;
        this.lineNumber = lineNumber;
        this.patternTitle = patternTitle;
        this.patternCategory = patternCategory;
        this.patternSubcategory = patternSubcategory;
        this.patternLevel = patternLevel;
        this.patternSeverity = patternSeverity;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getLineText() {
        return lineText;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getPatternTitle() {
        return patternTitle;
    }

    public String getPatternCategory() {
        return patternCategory;
    }

    public String getPatternSubcategory() {
        return patternSubcategory;
    }

    public String getPatternLevel() {
        return patternLevel;
    }

    public String getPatternSeverity() {
        return patternSeverity;
    }

}
