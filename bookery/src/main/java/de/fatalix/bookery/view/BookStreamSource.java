/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view;


import com.vaadin.server.StreamResource;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 *
 * @author felix.husse
 */
public class BookStreamSource implements StreamResource.StreamSource{
    
    private final byte[] data;

    public BookStreamSource(byte[] data) {
        this.data = data;
    }
    
  
    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(data);
    }
    
}
