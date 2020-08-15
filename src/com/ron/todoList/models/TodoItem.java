package com.ron.todoList.models;

import java.time.LocalDate;
import java.util.UUID;

public class TodoItem {
    private String id;
    private String shortDescription;
    private String description;
    private LocalDate expireDate;

    public TodoItem(String shortDescription, String description, LocalDate expireDate) {
        this.id = UUID.randomUUID().toString();
        this.shortDescription = shortDescription;
        this.description = description;
        this.expireDate = expireDate;
    }

    public TodoItem(String shortDescription, String description, LocalDate expireDate, String id) {
        this.id = id;
        this.shortDescription = shortDescription;
        this.description = description;
        this.expireDate = expireDate;
    }

    public String getId() {
        return id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(LocalDate expireDate) {
        this.expireDate = expireDate;
    }

    @Override
    public String toString() {
        return this.shortDescription;
    }
}
