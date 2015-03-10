/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.fileimport;

import de.fatalix.bookery.bl.BookService;
import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.lang3.StringEscapeUtils;
import org.joda.time.DateTime;

/**
 *
 * @author felix.husse
 */
@Stateless
public class FileImportService {

    @Inject
    private Executor executor;
    
    @Inject private BookService bookService;
    
    
    public void invoke(final String folder, final String username) {
        Runnable command = new Runnable() {

            @Override
            public void run() {
                File startFolder = new File(folder);
                parseFolder(startFolder,username);
            }
        };

        executor.execute(command);
    }

    private void parseFolder(File folder, String username) {
        File[] files = folder.listFiles();

        if(files.length == 3 && !files[0].isDirectory()) {
            parseFiles(files,username);
        } else {
            for(File file : files) {
                parseFolder(file,username);
            }
        }
    }

    private void parseFiles(final File[] files,String username) {
        System.out.println("***Try to add Book " + files[0].getName());
        BookEntry bookEntry = new BookEntry().setUploader(username);
        for (File file : files) {
            try {
                if (file.getName().contains(".opf")) {
                    bookEntry = parseOPF(file.toPath(),bookEntry);
                }
                if (file.getName().contains(".mobi")) {
                    bookEntry.setFile(Files.readAllBytes(file.toPath()));
                }
                if (file.getName().contains(".jpg")) {
                    bookEntry.setCover(Files.readAllBytes(file.toPath()));
                }
            } catch(IOException ex) {
                    Logger.getLogger(FileImportService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (bookService.addBook(bookEntry)) {
            System.out.println("Added Book " + bookEntry.getTitle());
        }
        else {
            System.out.println("Failed to add Book " + bookEntry.getTitle());
        }
    }
    
    private BookEntry parseOPF(Path pathToOPF,BookEntry bmd) throws IOException {
        List<String> lines = Files.readAllLines(pathToOPF, Charset.forName("UTF-8"));
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
                    DateTime dtReleaseDate = new DateTime(value);
                    if (dtReleaseDate.getYear() != 101) {
                        bmd.setReleaseDate(dtReleaseDate.toString("YYYY-MM-dd"));
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
