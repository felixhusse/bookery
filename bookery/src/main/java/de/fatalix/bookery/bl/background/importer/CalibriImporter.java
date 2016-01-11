/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.background.importer;

import com.google.gson.Gson;
import de.fatalix.bookery.bl.background.BatchJobInterface;
import de.fatalix.bookery.bl.model.BatchJobConfiguration;
import de.fatalix.bookery.bl.solr.SolrHandler;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.Timer;
import javax.inject.Inject;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 *
 * @author felix.husse
 */
@Stateless
public class CalibriImporter implements BatchJobInterface{
    
    @Inject private Logger logger;
    
    @Inject private SolrHandler solrHandler;
    
    @Override
    public void executeJob(Timer timer) {
        DateTimeZone.setDefault(DateTimeZone.UTC);
        BatchJobConfiguration jobConfig = (BatchJobConfiguration)timer.getInfo();
        Gson gson = new Gson();
        CalibriImporterConfiguration config = gson.fromJson(jobConfig.getConfigurationXML(), CalibriImporterConfiguration.class);
        
        File importFolder = new File(config.getImportFolder());
        if (importFolder.isDirectory()) {
            File[] zipFiles = importFolder.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".zip");
                }
            });
            for (File zipFile : zipFiles) {
                try {
                    processArchive(zipFile.toPath(),config.getBatchSize());
                    logger.info("Processed file " + zipFile.getName());
                } catch (IOException ex) {
                    logger.error("Cannot process " + zipFile.getName(),ex);
                    
                }
            }
        }
        else {
            logger.error("Import folder: " + importFolder.getAbsolutePath() + " cannot be read!");
        }
    }
    
    private void processArchive(final Path zipFile, final int batchSize) throws IOException {
        try (FileSystem zipFileSystem = FileSystems.newFileSystem(zipFile, null)) {
            final List<BookEntry> bookEntries = new ArrayList<>();
            Path root = zipFileSystem.getPath("/");
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.toString().contains("__MACOSX")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
                        BookEntry bookEntry = new BookEntry().setUploader("admin");
                        for (Path path : directoryStream) {
                            if (!Files.isDirectory(path)) {
                                if (path.toString().contains(".opf")) {
                                    bookEntry = parseOPF(path, bookEntry);
                                }
                                if (path.toString().contains(".mobi")) {
                                    bookEntry.setMobi(Files.readAllBytes(path))
                                            .setMimeType("MOBI");
                                }
                                if (path.toString().contains(".epub")) {
                                    bookEntry.setEpub(Files.readAllBytes(path));
                                }
                                if (path.toString().contains(".jpg")) {
                                    bookEntry.setCover(Files.readAllBytes(path));
                                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                                    Thumbnails.of(new ByteArrayInputStream(bookEntry.getCover()))
                                                .size(130, 200)
                                                .toOutputStream(output);
                                    bookEntry.setThumbnail(output.toByteArray());
                                    bookEntry.setThumbnailGenerated("done");
                                }
                            }
                        }
                        if (bookEntry.getMobi()!=null || bookEntry.getEpub()!=null) {
                            bookEntries.add(bookEntry);
                            if (bookEntries.size()>batchSize) {
                                logger.info("Adding " + bookEntries.size() + " Books...");
                                try {
                                    solrHandler.addBeans(bookEntries);
                                } catch (SolrServerException ex) {
                                    logger.error(ex,ex);
                                }
                                bookEntries.clear();
                            }
                        }
                    } catch (IOException ex) {
                        logger.error(ex,ex);
                    }
                    return super.preVisitDirectory(dir, attrs);
                }
            });
            try {
                if (!bookEntries.isEmpty()) {
                    logger.info("Adding " + bookEntries.size() + " Books...");
                    solrHandler.addBeans(bookEntries);
                }
            } catch (SolrServerException ex) {
                logger.error(ex,ex);
            }  
        } finally {
            Files.delete(zipFile);
        }       
    }
    
    private static BookEntry parseOPF(Path pathToOPF, BookEntry bmd) throws IOException {
        List<String> lines = Files.readAllLines(pathToOPF, Charset.forName("UTF-8"));
        boolean multiLineDescription = false;
        String description = "";
        for (String line : lines) {
            if (multiLineDescription) {
                multiLineDescription = false;
                if (line.split("<").length == 1) {
                    multiLineDescription = true;
                    description = description + line;
                } else {
                    description = description + line.split("<")[0];
                    description = StringEscapeUtils.unescapeXml(description);
                    bmd.setDescription(description);
                }
            } else {
                if (line.contains("dc:title")) {
                    String title = line.split(">")[1].split("<")[0];
                    bmd.setTitle(title);
                } else if (line.contains("dc:creator")) {
                    String creator = line.split(">")[1].split("<")[0];
                    bmd.setAuthor(creator);
                } else if (line.contains("dc:description")) {
                    String value = line.split(">")[1];
                    if (value.split("<").length == 1) {
                        multiLineDescription = true;
                        description = value;
                    } else {
                        value = value.split("<")[0];
                        value = StringEscapeUtils.unescapeXml(value);
                        bmd.setDescription(value);
                    }
                } else if (line.contains("dc:publisher")) {
                    String value = line.split(">")[1].split("<")[0];
                    bmd.setPublisher(value);
                } else if (line.contains("dc:date")) {
                    String value = line.split(">")[1].split("<")[0];
                    DateTime dtReleaseDate = new DateTime(value);
                    if (dtReleaseDate.getYear() != 101) {
                        bmd.setReleaseDate(dtReleaseDate.toDate());
                    }
                } else if (line.contains("dc:language")) {
                    String value = line.split(">")[1].split("<")[0];
                    bmd.setLanguage(value);
                } else if (line.contains("opf:scheme=\"ISBN\"")) {
                    String value = line.split(">")[1].split("<")[0];
                    bmd.setIsbn(value);
                    
                }
            }
        }
        return bmd;
    }
    
}
