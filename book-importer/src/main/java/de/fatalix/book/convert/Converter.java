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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 *
 * @author felixhusse1
 */
public class Converter {

    private static String EBOOK_CONVERT = "C:\\Program Files\\Calibre2\\ebook-convert.exe";

    private static String EPUB_FILE = "/Users/felixhusse1/Documents/fabian-books/Adler-Olsen, Jussi/Erbarmen (75)/Erbarmen - Adler-Olsen, Jussi.epub";

    private static String MOBI_FILE = "/Users/felixhusse1/Documents/fabian-books/Adler-Olsen, Jussi/Erbarmen (75)/Erbarmen - Adler-Olsen, Jussi.mobi";

    static public void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        convertAllFiles();
    }

    private static void convertAllFiles() throws IOException, InterruptedException, ExecutionException {
        File folder = new File("F:\\test");
        File[] ebooks = folder.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.contains("epub");
            }
        });
        System.out.println("Converting " +ebooks.length + " books");

        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Set<Callable<String>> callables = new HashSet<>();

        for (File ebook : ebooks) {
            ConverterTask task = new ConverterTask(ebook);
            callables.add(task);
        }
        
        List<Future<String>> results = executorService.invokeAll(callables);
        boolean running = true;
        while (running) {
            System.out.println("Check threads: " + results.size());
            Thread.sleep(250);
            for(Future<String> future : results){
                System.out.println("future.get = " + future.get());
            }
            if (results.size() == ebooks.length) {
                running = false;
            }
            else {
                System.out.println("Finished: " + results.size());
            }
        }
        executorService.shutdown();
        System.out.println("Finished");
        
        
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
