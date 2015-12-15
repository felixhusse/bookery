/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.book.importer;

import de.fatalix.bookery.solr.model.BookEntry;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;

/**
 *
 * @author Fatalix
 */
public class CalibriImporter {

    public static void main(String[] args) throws IOException, URISyntaxException {
        readArchive(new File("E:/dumps/calibre-export.zip"));
    }

    public static void readArchive(File archive) throws IOException, URISyntaxException {
        Path zipFile = archive.toPath();
        FileSystem zipFileSystem = FileSystems.newFileSystem(zipFile, null);
        Path root = zipFileSystem.getPath("/");
        final List<BookEntry> bookEntries = new ArrayList<>();
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                //System.out.println(file.toString());

                return super.visitFile(file, attrs);
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {

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
                            }
                        }
                    }
                    if (bookEntry.getMobi()!=null) {
                        bookEntries.add(bookEntry);
                        if (bookEntries.size()>10) {
                            System.out.println("Adding " + bookEntries.size() + " Books...");
                            bookEntries.clear();
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return super.preVisitDirectory(dir, attrs); //To change body of generated methods, choose Tools | Templates.
            }

        });
        
        System.out.println("Adding " + bookEntries.size() + " Books...");
        bookEntries.clear();
        System.out.println("MÖP");

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
