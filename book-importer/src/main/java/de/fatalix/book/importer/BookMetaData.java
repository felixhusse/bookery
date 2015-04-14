/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.book.importer;

import java.util.Date;

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
    private final Date releaseDate;
    private final String mimeType;
    private final Date uploadDate;

    public BookMetaData(String author, String title, String isbn, String publisher, String description, String language, Date releaseDate, String mimeType, Date uploadDate) {
        this.author = author;
        this.title = title;
        this.isbn = isbn;
        this.publisher = publisher;
        this.description = description;
        this.language = language;
        this.releaseDate = releaseDate;
        this.mimeType = mimeType;
        this.uploadDate = uploadDate;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public String getMimeType() {
        return mimeType;
    }

    public Date getUploadDate() {
        return uploadDate;
    }
    
        
}
