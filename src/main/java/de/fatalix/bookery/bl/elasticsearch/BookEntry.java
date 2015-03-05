/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.elasticsearch;

/**
 * 
 * @author felix.husse
 */
public class BookEntry {
    private String author;
    private String title;
    private String isbn;
    private String category;
    
    private int rating;
    private String uploader;
    private String[] reader;
    private String[] shared;
    
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

    public String getCategory() {
        return category;
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

    public BookEntry setCategory(String category) {
        this.category = category;
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
    
}
