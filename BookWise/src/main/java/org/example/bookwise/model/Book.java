package org.example.bookwise.model;

public class Book {
    private String id;
    private String title;
    private String authors;
    private String thumbnail;
    private String description;

    public Book() {}

    public Book(String id, String title, String authors, String thumbnail, String description) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.thumbnail = thumbnail;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}