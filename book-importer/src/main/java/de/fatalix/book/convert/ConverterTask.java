/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.book.convert;

import java.io.File;
import java.util.concurrent.Callable;

/**
 *
 * @author Fatalix
 */
public class ConverterTask implements Callable<String> {
    
    private static String EBOOK_CONVERT = "/Applications/calibre.app/Contents/MacOS/ebook-convert";
    
    private final File epubFile;

    public ConverterTask(File epubFile) {
        this.epubFile = epubFile;
    }

    @Override
    public String call() throws Exception {
        String mobiBook = epubFile.getAbsolutePath();
        mobiBook = mobiBook.substring(0, mobiBook.length() - 4);
        mobiBook = mobiBook + "mobi";
        String[] cmds = {EBOOK_CONVERT, epubFile.getAbsolutePath(), mobiBook};
        long runtime = System.currentTimeMillis();
        Process p = Runtime.getRuntime().exec(cmds);
        p.waitFor();
        runtime = (System.currentTimeMillis() - runtime)/1000;
        System.out.println("Converted " + epubFile.getName() + " in " + runtime + "s");
        return epubFile.getName();
    }

}
