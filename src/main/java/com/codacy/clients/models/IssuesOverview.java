package com.codacy.clients.models;

import java.util.List;



public class IssuesOverview {

    public IssuesOverview(List<Count> categories, List<Count> languages, List<Count> levels, List<Count> pattenrs) {
        super();
        this.categories = categories;
        this.languages = languages;
        this.levels = levels;
        this.patterns = pattenrs;
    }

    private List<Count> categories;

    private List<Count> languages;

    private List<Count> levels;

    private List<Count> patterns;

    public List<Count> getCategories() {
        return categories;
    }

    public List<Count> getLanguages() {
        return languages;
    }

    public List<Count> getLevels() {
        return levels;
    }

    public List<Count> getPatterns() {
        return patterns;
    }
}
