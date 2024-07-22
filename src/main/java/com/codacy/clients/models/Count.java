package com.codacy.clients.models;

public  class Count {
    public Count(String name, int total) {
        super();
        this.name = name;
        this.total = total;
    }

    private String name;
    private int total;

    public String getName() {
        return name;
    }

    public int getTotal() {
        return total;
    }
}
