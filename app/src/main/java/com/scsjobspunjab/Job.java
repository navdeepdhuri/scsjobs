package com.scsjobspunjab;

public class Job {
    private String company;
    private String title;
    private String description;
    private String location;
    private String salary;

    // Default constructor (required for Firebase)
    public Job() {
    }

    public Job(String company, String title, String description, String location, String salary) {
        this.company = company;
        this.title = title;
        this.description = description;
        this.location = location;
        this.salary = salary;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
