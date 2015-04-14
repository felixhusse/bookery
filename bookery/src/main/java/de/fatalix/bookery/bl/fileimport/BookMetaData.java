/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.fileimport;

import org.joda.time.DateTime;

/**
 *
 * @author felix.husse
 */
public class BookMetaData {
    
    private String author;
    private String title;
    private String isbn;
    private String realeaseDate;
    private String description;
    private String publisher;
    private String language;

    public BookMetaData setAuthor(String author) {
        this.author = author;
        return this;
    }
    
    public BookMetaData setTitle(String title) {
        this.title = title;
        return this;
    }

    public BookMetaData setIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public BookMetaData setReleaseDate(String releaseYear) {
        DateTime dtReleaseDate = new DateTime(releaseYear);
        if (dtReleaseDate.getYear() == 101) {
            this.realeaseDate = null;
        }
        else {
            this.realeaseDate = dtReleaseDate.toString("YYYY-MM-dd");
        }
        return this;
    }

    public BookMetaData setDescription(String description) {
        this.description = description;
        return this;
    }

    public BookMetaData setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public BookMetaData setLanguage(String language) {
        this.language = language;
        return this;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getRealeaseDate() {
        return realeaseDate;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    @Override
    public String toString() {
        return "BookMetaData{" + "author=" + author + ", title=" + title + ", isbn=" + isbn + ", releaseYear=" + realeaseDate + ", description=" + description + ", publisher=" + publisher +
               ", language=" + language + '}';
    }
    
    
    
}
