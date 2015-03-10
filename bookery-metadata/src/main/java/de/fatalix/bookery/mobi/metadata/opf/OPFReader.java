/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.mobi.metadata.opf;

import de.fatalix.bookery.mobi.metadata.BookMetaData;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;

/**
 *
 * @author felix.husse
 */
public class OPFReader {
    
    public static BookMetaData parseOPF(Path pathToOPF) throws IOException {
        List<String> lines = Files.readAllLines(pathToOPF, Charset.forName("UTF-8"));
        BookMetaData bmd = new BookMetaData();
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
                    bmd.setReleaseDate(value);
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
    
    private static int bookCounter = 1;
    
    public static void main(String[] args) throws IOException {
            
        File startFolder = new File("C:\\ebooks\\calibre-export\\");
        parseFolder(startFolder);
        
//        Collection<File> files = FileUtils.listFiles(new File("C:\\ebooks\\calibre-export\\"), new String[]{"opf"}, true);
//        System.out.println("Files: " + files.size());
//        int i = 1;
//        for (File file : files) {
//            //System.out.println("File: " + file.getAbsolutePath());
//            System.out.println(i + ": " + parseOPF(file.toPath()).toString());
//            i++;
//        }
    }
    
    private static void parseFolder(File folder) {
         File[] files = folder.listFiles();

        if(files.length == 3 && !files[0].isDirectory()) {
            
            System.out.println(bookCounter + " Book found");
            bookCounter++;
        } else {
            for(File file : files) {
                parseFolder(file);
            }
        }
    }
    
}
