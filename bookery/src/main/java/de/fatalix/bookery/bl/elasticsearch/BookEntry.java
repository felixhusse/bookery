/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.elasticsearch;

import java.util.Date;

/**
 * 
 * @author felix.husse
 */
public class BookEntry {
    private String author;
    private String title;
    private String isbn;
    private String publisher;
    private String description;
    private String language;
    private String releaseDate;
    
    private int rating;
    private String uploader;
    private String[] reader;
    private String[] shared;
    
    
    private byte[] cover;
    private byte[] file;
    
    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public int getRating() {
        return rating;
    }

    public String getUploader() {
        return uploader;
    }

    public String[] getReader() {
        return reader;
    }

    public String[] getShared() {
        return shared;
    }

    public byte[] getFile() {
        return file;
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

    public BookEntry setReleaseDate(String releaseDate) {
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

    public BookEntry setReader(String[] reader) {
        this.reader = reader;
        return this;
    }

    public BookEntry setShared(String[] shared) {
        this.shared = shared;
        return this;
    }

    public BookEntry setFile(byte[] file) {
        this.file = file;
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

    @Override
    public String toString() {
        return "BookEntry{" + "author=" + author + ", title=" + title + ", isbn=" + isbn + ", publisher=" + publisher + ", releaseDate=" + releaseDate + '}';
    }
    
}
