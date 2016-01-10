/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view;

import com.vaadin.server.StreamResource;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import javax.annotation.PostConstruct;

/**
 *
 * @author Fatalix
 */
public class SuggestLaneLayout extends VerticalLayout{
    
    private Label titleLabel;
    
    private HorizontalLayout layout;
    
    @PostConstruct
    private void postInit() {
        setSpacing(true);
        addStyleName("bookery-lane");
        titleLabel = new Label("Your Title here..");
        titleLabel.addStyleName(ValoTheme.LABEL_H2);
        layout = new HorizontalLayout();
        layout.setSpacing(true);
        addComponents(titleLabel,layout);
    }
    
    public void loadLane(String title, List<BookEntry> bookEntries) {
        layout.removeAllComponents();
        titleLabel.setValue(title);
        for (BookEntry bookEntry : bookEntries) {
            layout.addComponent(createImage(bookEntry));
        }
    }
    
    private Image createImage(BookEntry bookEntry) {
        
        Image image = new Image();
        image.setAlternateText(bookEntry.getTitle() + " von " + bookEntry.getAuthor());
        image.setHeight("200px");
        image.setImmediate(true);
        if(bookEntry.getThumbnail() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getThumbnail());
            image.setSource(new StreamResource(source, bookEntry.getId() + "_thumb.png"));
        } else if(bookEntry.getCover() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getCover());
            image.setSource(new StreamResource(source, bookEntry.getId() + ".png"));
        }
        return image;
    }
    
    protected class ByteStreamResource implements StreamResource.StreamSource {

        private final ByteArrayInputStream imageBuffer;

        public ByteStreamResource(byte[] data) {
            imageBuffer = new ByteArrayInputStream(data);
        }

        @Override
        public InputStream getStream() {
            return imageBuffer;
        }

    }
    
}
