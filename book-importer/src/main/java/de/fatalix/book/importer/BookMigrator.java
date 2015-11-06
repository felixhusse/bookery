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
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author felix.husse
 */
public class BookMigrator {
    
    public static void clearDB(String solrURL, String solrCore) throws SolrServerException, IOException {
        SolrServer server = SolrHandler.createConnection(solrURL, solrCore);

            System.out.println("RESET:");
            server.deleteByQuery("*:*");
            server.commit();

        
    }
    
    /**
     *
     * @param solrURL
     * @param solrCore
     * @param batchSize
     * @param importPath
     * @throws IOException
     * @throws SolrServerException
     */
    public static void importBooks(String solrURL, String solrCore, int batchSize, String importPath, boolean reset) throws IOException, SolrServerException {
        
        File importFolder = new File(importPath);
        if(!importFolder.isDirectory()) {
            throw new IOException(importFolder.getAbsolutePath() + " is not a folder!");
        }

        SolrServer server = SolrHandler.createConnection(solrURL, solrCore);
        if (reset) {
            System.out.println("RESET:");
            server.deleteByQuery("*:*");
            server.commit();

        }
        
        System.out.println("Connection established");

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
            } else if (file.getName().contains(".opf")) {
                bookEntry = parseOPF(file, bookEntry);
                
                bookEntry.setMimeType("mobi").setUploadDate(new DateTime(DateTimeZone.UTC).toDate());
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
    
    private static BookEntry parseOPF(File pathToOPF,BookEntry bmd) throws IOException {
        List<String> lines = Files.readAllLines(pathToOPF.toPath(), Charset.forName("UTF-8"));
        boolean multiLineDescription = false;
        String description = "";
        for (String line : lines) {
            if (multiLineDescription) {
                multiLineDescription = false;
                if (line.split("<").length == 1) {
                    multiLineDescription = true;
                    description = description + line;
                }
                else {
                    description = description + line.split("<")[0];
                    description = StringEscapeUtils.unescapeXml(description);
                    bmd.setDescription(description); 
                }
            }
            else {
                if (line.contains("dc:title")) {
                    String title = line.split(">")[1].split("<")[0];
                    bmd.setTitle(title);
                }
                else if (line.contains("dc:creator")) {
                    String creator = line.split(">")[1].split("<")[0];
                    bmd.setAuthor(creator);
                }
                else if (line.contains("dc:description")) {
                    String value = line.split(">")[1];
                    if (value.split("<").length == 1) {
                        multiLineDescription = true;
                        description = value;
                    }
                    else {
                        value = value.split("<")[0];
                        value = StringEscapeUtils.unescapeXml(value);
                        bmd.setDescription(value);
                    }
                } 
                else if (line.contains("dc:publisher")) {
                    String value = line.split(">")[1].split("<")[0];
                    bmd.setPublisher(value);
                }
                else if (line.contains("dc:date")) {
                    String value = line.split(">")[1].split("<")[0];
                    DateTime dtReleaseDate = new DateTime(value,DateTimeZone.UTC);
                    if (dtReleaseDate.getYear() != 101) {
                        bmd.setReleaseDate(dtReleaseDate.toDate());
                    }
                }
                else if (line.contains("dc:language")) {
                    String value = line.split(">")[1].split("<")[0];
                    bmd.setLanguage(value);
                }
                else if (line.contains("opf:scheme=\"ISBN\"")) {
                    String value = line.split(">")[1].split("<")[0];
                    bmd.setIsbn(value);
                }
            }
        }
        return bmd;
    }
    

}
