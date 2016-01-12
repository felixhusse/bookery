/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
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
