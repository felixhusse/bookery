/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;

/**
 *
 * @author Fatalix
 */
public class BookDetailLayout extends CssLayout {

    @Inject private Logger logger;
    @Inject private BookDetailPresenter presenter;
    
    private BookEntry bookEntry;
    
    private Image image;
    private Label titleLabel;
    private Label authorLabel;
    private Label descriptionLabel;
    
    private Label likeCount;
    private Label downloadCount;

    private Button likeButton;
    private Button downloadButton;
    private Button sendToKindleButton;
    private Button cancelButton;
    
    @PostConstruct
    private void postInit() {
        addStyleName("product-form-wrapper");
        addStyleName("product-form");
        image = new Image();
        image.setImmediate(true);
        image.setHeight("200px");
        image.setWidth("130px");
        
        descriptionLabel = new Label("Description", ContentMode.HTML);
        descriptionLabel.addStyleName(ValoTheme.LABEL_LIGHT);
        descriptionLabel.setValue("Keine Beschreibung vorhanden");
        
        cancelButton = new Button("cancle", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                setLayoutVisible(false);
            }
        });
        cancelButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        
        VerticalLayout rootLayout = new VerticalLayout(image,descriptionLabel,cancelButton);
        rootLayout.setHeight(100, Unit.PERCENTAGE);
        rootLayout.setSpacing(true);
        
        addComponent(rootLayout);
    }

    public void loadData(BookEntry bookEntry) {
        this.bookEntry = bookEntry;
        descriptionLabel.setValue(bookEntry.getDescription());
        if(bookEntry.getThumbnail() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getThumbnail());
            image.setSource(new StreamResource(source, bookEntry.getId() + "_thumb.png"));
        } else if(bookEntry.getCover() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getCover());
            image.setSource(new StreamResource(source, bookEntry.getId() + ".png"));
        }
    }
    
    public void setLayoutVisible(boolean visible) {
        if (visible) {
            addStyleName("visible");
        }
        else {
            removeStyleName("visible");
        }
        setEnabled(visible);
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
