/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.solr.model;

import java.util.Date;
import java.util.UUID;
import org.apache.solr.client.solrj.beans.Field;

/**
 * 
 * @author felix.husse
 */
public class BookEntry {
    @Field("id")
    private final String id;
    
    @Field("author")
    private String author;
    @Field("title")
    private String title;
    @Field("isbn")
    private String isbn;
    @Field("publisher")
    private String publisher;
    @Field("description")
    private String description;
    @Field("language")
    private String language;
    @Field("thumbnailgenerated")
    private String thumbnailGenerated;
    
    @Field("releaseDate")
    private Date releaseDate;
    @Field("uploadDate")
    private Date uploadDate;
    
    @Field("rating")
    private int rating;
    @Field("uploader")
    private String uploader;
    @Field("viewed")
    private String[] viewed;
    @Field("shared")
    private String[] shared;
    @Field("mimetype")
    private String mimeType;
    
    @Field("cover")
    private byte[] cover;

    @Field("thumbnail")
    private byte[] thumbnail;
    
    @Field("epub")
    private byte[] epub;
    @Field("mobi")
    private byte[] mobi;
    
    public BookEntry() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
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

    public Date getReleaseDate() {
        return releaseDate;
    }

    public int getRating() {
        return rating;
    }

    public String getUploader() {
        return uploader;
    }

    public String[] getViewed() {
        return viewed;
    }

    public String[] getShared() {
        return shared;
    }

    public byte[] getEpub() {
        return epub;
    }

    public byte[] getMobi() {
        return mobi;
    }
    
    public byte[] getCover() {
        return cover;
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

    public String getMimeType() {
        return mimeType;
    }

    public Date getUploadDate() {
        return uploadDate;
    }
    
    public BookEntry setMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }
    
    public BookEntry setAuthor(String author) {
        this.author = author;
        return this;
    }

    public BookEntry setTitle(String title) {
        this.title = title;
        return this;
    }

    public BookEntry setIsbn(String isbn) {
        this.isbn = isbn;
        return this;
    }

    public BookEntry setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }
    
    public BookEntry setRating(int rating) {
        this.rating = rating;
        return this;
    }

    public BookEntry setUploader(String uploader) {
        this.uploader = uploader;
        return this;
    }

    public BookEntry setViewed(String[] viewed) {
        this.viewed = viewed;
        return this;
    }

    public BookEntry setShared(String[] shared) {
        this.shared = shared;
        return this;
    }

    public BookEntry setMobi(byte[] mobi) {
        this.mobi = mobi;
        return this;
    }
    
    public BookEntry setEpub(byte[] epub) {
        this.epub = epub;
        return this;
    }

    public BookEntry setCover(byte[] cover) {
        this.cover = cover;
        return this;
    }

    public BookEntry setPublisher(String publisher) {
        this.publisher = publisher;
        return this;
    }

    public BookEntry setDescription(String description) {
        this.description = description;
        return this;
    }

    public BookEntry setLanguage(String language) {
        this.language = language;
        return this;
    }

    public BookEntry setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
        return this;
    }

    public String getThumbnailGenerated() {
        return thumbnailGenerated;
    }

    public BookEntry setThumbnailGenerated(String thumbnailGenerated) {
        this.thumbnailGenerated = thumbnailGenerated;
        return this;
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public BookEntry setThumbnail(byte[] thumbnail) {
        this.thumbnail = thumbnail;
        return this;
    }
    
    @Override
    public String toString() {
        return "BookEntry{" + "author=" + author + ", title=" + title + ", isbn=" + isbn + ", publisher=" + publisher + ", releaseDate=" + releaseDate + '}';
    }
    
}
