/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.book.importer;

import com.google.gson.Gson;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;

/**
 *
 * @author felix.husse
 */
public class BookMigrator {

    /**
     *
     * @param solrURL
     * @param solrCore
     * @param batchSize
     * @param importPath
     * @throws IOException
     * @throws SolrServerException
     */
    public static void importBooks(String solrURL, String solrCore, int batchSize, String importPath) throws IOException, SolrServerException {
        
        File importFolder = new File(importPath);
        if(!importFolder.isDirectory()) {
            throw new IOException(importFolder.getAbsolutePath() + " is not a folder!");
        }

        SolrServer server = SolrHandler.createConnection(solrURL, solrCore);
        
        System.out.println("Connection established");
        
        SolrHandler.resetSolrIndex(server);
        Gson gson = new Gson();

        File[] bookFolders = importFolder.listFiles(new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        int total = bookFolders.length;
        int counter = 0;
        List<BookEntry> bookEntries = new ArrayList<>();
        for(File bookFolder : bookFolders) {
            bookEntries.add(importBatchWise(bookFolder, gson));
            counter++;
            if(bookEntries.size() >= batchSize) {
                UpdateResponse response = SolrHandler.addBeans(server, bookEntries);
                if(response.getStatus() != 0) {
                    throw new SolrServerException("Update failed with CODE " + response.getStatus());
                }
                bookEntries.clear();
                System.out.println("Processed " + counter + " of " + total);
            }
        }
        if(bookEntries.size() > 0) {
            UpdateResponse response = SolrHandler.addBeans(server, bookEntries);
            if(response.getStatus() != 0) {
                throw new SolrServerException("Update failed with CODE " + response.getStatus());
            }
            bookEntries.clear();
            System.out.println("Processed " + counter + " of " + total);
        }

    }
    
    
    
    /**
     *
     * @param solrURL
     * @param solrCore
     * @param batchSize
     * @param exportPath
     */
    public static void exportBooks(String solrURL, String solrCore, int batchSize, String exportPath) throws SolrServerException, IOException {
        File exportFolder = new File(exportPath);
        if(!exportFolder.isDirectory()) {
            throw new IOException(exportFolder.getAbsolutePath() + " is not a folder!");
        }

        SolrServer server = SolrHandler.createConnection(solrURL, solrCore);
        System.out.println("Connection established");
        Gson gson = new Gson();

        exportBatchWise(server, exportFolder, batchSize, 0, gson);
    }
    
 
    
    private static BookEntry importBatchWise(File bookFolder, Gson gson) throws IOException {
        BookEntry bookEntry = new BookEntry();

        for(File file : bookFolder.listFiles()) {
            if(file.getName().contains(".mobi")) {
                byte[] bookData = Files.readAllBytes(file.toPath());
                bookEntry.setFile(bookData);
            } else if(file.getName().contains(".jpg")) {
                byte[] coverData = Files.readAllBytes(file.toPath());
                bookEntry.setCover(coverData);
            } else if(file.getName().contains(".json")) {
                BookMetaData bmd = gson.fromJson(IOUtils.toString(new FileInputStream(file), Charset.defaultCharset()), BookMetaData.class);
                bookEntry.setAuthor(bmd.getAuthor()).setTitle(bmd.getTitle()).setIsbn(bmd.getIsbn())
                        .setPublisher(bmd.getPublisher()).setDescription(bmd.getDescription()).setLanguage(bmd.getLanguage())
                        .setMimeType(bmd.getMimeType()).setUploadDate(bmd.getUploadDate()).setReleaseDate(bmd.getReleaseDate());
            }
        }

        return bookEntry;
    }

    private static void exportBatchWise(SolrServer server, File exportFolder, int batchSize, int offset, Gson gson) throws SolrServerException, IOException {

        QueryResponse response = SolrHandler.searchSolrIndex(server, "*:*", batchSize, offset);
        List<BookEntry> bookEntries = response.getBeans(BookEntry.class);
        System.out.println("Retrieved " + (bookEntries.size() + offset) + " of " + response.getResults().getNumFound());
        for(BookEntry bookEntry : bookEntries) {
            String bookTitle = bookEntry.getTitle();
            bookTitle = bookTitle.replaceAll(":", " ");
            File bookFolder = new File(exportFolder, bookEntry.getAuthor() + "-" + bookTitle);
            bookFolder.mkdirs();
            if(bookEntry.getFile() != null && bookEntry.getCover() != null) {
                File bookData = new File(bookFolder, bookEntry.getAuthor() + "-" + bookTitle + ".mobi");
                Files.write(bookData.toPath(), bookEntry.getFile(), StandardOpenOption.CREATE_NEW);

                File coverData = new File(bookFolder, bookEntry.getAuthor() + "-" + bookTitle + ".jpg");
                Files.write(coverData.toPath(), bookEntry.getCover(), StandardOpenOption.CREATE_NEW);

                File metaDataFile = new File(bookFolder, bookEntry.getAuthor() + "-" + bookTitle + ".json");
                BookMetaData metaData = new BookMetaData(bookEntry.getAuthor(), bookEntry.getTitle(), bookEntry.getIsbn(), 
                                                        bookEntry.getPublisher(), bookEntry.getDescription(), bookEntry.getLanguage(),
                                                        bookEntry.getReleaseDate(), bookEntry.getMimeType(),bookEntry.getUploadDate());
                gson.toJson(metaData);
                Files.write(metaDataFile.toPath(), gson.toJson(metaData).getBytes(), StandardOpenOption.CREATE_NEW);
            }

        }

        if(response.getResults().getNumFound() > offset) {
            exportBatchWise(server, exportFolder, batchSize, offset + batchSize, gson);
        }

    }


}
