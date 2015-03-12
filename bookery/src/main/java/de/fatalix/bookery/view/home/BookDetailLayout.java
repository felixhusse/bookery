/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.annotation.PostConstruct;

/**
 *
 * @author Fatalix
 */
public class BookDetailLayout extends HorizontalLayout{
    
    private Image image;
    
    private Label titleLabel;
    private Label authorLabel;
    private Label descriptionLabel;
    
    
    
    @PostConstruct
    private void postInit() {
        image = new Image();
        //image.setWidth(300, Unit.PIXELS);
        //image.setHeight(400, Unit.PIXELS);
        image.setImmediate(true);
        image.addStyleName("book-cover");
        titleLabel = new Label("Title");

        titleLabel.addStyleName(ValoTheme.LABEL_H2);
        authorLabel = new Label("Author");

        authorLabel.addStyleName(ValoTheme.LABEL_BOLD);
        authorLabel.addStyleName(ValoTheme.LABEL_COLORED);
        
        descriptionLabel = new Label("Description",ContentMode.HTML);
        descriptionLabel.addStyleName(ValoTheme.LABEL_LIGHT);

        
        VerticalLayout infoLayout = new VerticalLayout(titleLabel,authorLabel,descriptionLabel);
        //infoLayout.addStyleName("book-detail-info");

        
        this.setMargin(true);
        this.setSpacing(true);
        //addStyleName("wrapping"); 
        addStyleName("bookery-content");
        this.addComponents(image,infoLayout);
        this.setExpandRatio(infoLayout, 1.0f);
        this.setWidth(100, Unit.PERCENTAGE);
    }
    
    public void loadData(BookEntry bookEntry) {
        StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getCover());
        image.setSource(new StreamResource(source, bookEntry.getId()+".png"));
        titleLabel.setValue(bookEntry.getTitle());
        authorLabel.setValue(bookEntry.getAuthor());
        descriptionLabel.setValue(bookEntry.getDescription());
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
