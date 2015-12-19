/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.book.convert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.CopyOption;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

/**
 *
 * @author felixhusse1
 */
public class Converter {

    private static String EBOOK_CONVERT = "/Applications/calibre.app/Contents/MacOS/ebook-convert";

    private static String EPUB_FILE = "/Users/felixhusse1/Documents/fabian-books/Adler-Olsen, Jussi/Erbarmen (75)/Erbarmen - Adler-Olsen, Jussi.epub";

    private static String MOBI_FILE = "/Users/felixhusse1/Documents/fabian-books/Adler-Olsen, Jussi/Erbarmen (75)/Erbarmen - Adler-Olsen, Jussi.mobi";

    static public void main(String[] args) throws IOException, InterruptedException {
        convertAllFiles();
    }

    private static void convertAllFiles() throws IOException, InterruptedException {
        File folder = new File("/Users/felixhusse1/Documents/test");
        File[] ebooks = folder.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.contains("epub");
            }
        });
        System.out.println("Converting " +ebooks.length + " books");
        int counter = 0;
        StringBuffer output = new StringBuffer();
        for (File ebook : ebooks) {
            String mobiBook = ebook.getAbsolutePath();
            mobiBook = mobiBook.substring(0, mobiBook.length()-3);
            mobiBook = mobiBook + "mobi";
            String[] cmds = {"/bin/sh", "-c",EBOOK_CONVERT, ebook.getAbsolutePath(), mobiBook};
            Process p = Runtime.getRuntime().exec (cmds);
            p.waitFor();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";			
            while ((line = reader.readLine())!= null) {
                    output.append(line + "\n");
            }

            counter++;
            System.out.println ("Book " + counter + " of " + ebooks.length + " converted");
        }
    }

    private static void copyAllFiles() throws IOException {
        Path start = Paths.get("/Users/felixhusse1/Documents/fabian-books");
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String filename = file.getFileName().toString();
                if (filename.contains(".epub")) {
                    try {
                        Path destination = Paths.get("/Users/felixhusse1/Documents/only-epub", filename);
                        Files.copy(file, destination);
                        System.out.println("copied " + filename);
                    } catch (FileAlreadyExistsException ex) {
                        //IGNORE..ouch
                    }
                }

                return FileVisitResult.CONTINUE;
            }
        });
    }

}
