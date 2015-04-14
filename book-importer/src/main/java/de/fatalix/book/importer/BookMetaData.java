/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.book.importer;

/**
 *
 * @author felix.husse
 */
public class BookMetaData {
    
    private final String author;
    private final String title;
    private final String isbn;
    private final String publisher;
    private final String description;
    private final String language;
    private final String releaseDate;
    private final String mimeType;

    public BookMetaData(String author, String title, String isbn, String publisher, String description, String language, String releaseDate, String mimeType) {
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.publisher = publisher;
        this.description = description;
        this.language = language;
        this.releaseDate = releaseDate;
        this.mimeType = mimeType;
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

    public String getPublisher() {
        return publisher;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getMimeType() {
        return mimeType;
    }
    
}
