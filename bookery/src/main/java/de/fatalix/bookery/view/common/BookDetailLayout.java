/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.common;

import com.vaadin.cdi.UIScoped;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.solr.model.BookEntry;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class BookDetailLayout extends CssLayout {

    @Inject private Logger logger;
    @Inject private BookDetailPresenter presenter;
    
    private BookEntry bookEntry;
    
    private FileDownloader fileDownloader;
   
    private Label titleLabel;
    private Image image;
    private Button downloadButton;
    private Button sendToKindleButton;
    
    private Button likeButton;
    private Label downloadCount;
    
    private Label descriptionLabel;
    private Button cancelButton;
    
    
    @PostConstruct
    private void postInit() {
        addStyleName("product-form-wrapper");
        addStyleName("product-form");
        titleLabel = new Label("Title - Author");
        titleLabel.addStyleName(ValoTheme.LABEL_COLORED);
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        descriptionLabel = new Label("Description", ContentMode.HTML);
        descriptionLabel.addStyleName(ValoTheme.LABEL_LIGHT);
        descriptionLabel.setValue("Keine Beschreibung vorhanden");
        
        cancelButton = new Button("close", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                setLayoutVisible(false);
            }
        });
        cancelButton.setClickShortcut(ShortcutAction.KeyCode.ESCAPE);
        cancelButton.addStyleName(ValoTheme.BUTTON_DANGER);
        
        VerticalLayout rootLayout = new VerticalLayout(titleLabel,createImageLayout(),descriptionLabel,cancelButton);
        rootLayout.setSpacing(true);
        addComponent(rootLayout);
    }
    
    private HorizontalLayout createImageLayout() {
        image = new Image();
        image.setImmediate(true);
        image.setHeight("200px");
        image.setWidth("130px");
        
        
        downloadButton = new Button("download", FontAwesome.DOWNLOAD);
        downloadButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        downloadButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    loadData(presenter.updateShared(bookEntry, SecurityUtils.getSubject().getPrincipal().toString()));
                } catch(SolrServerException | IOException ex) {
                    Notification.show("Unexpected Error!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                }
            }
        });
        
        fileDownloader = new FileDownloader(new StreamResource(new BookStreamSource(null),"blbla.epub"));
        fileDownloader.extend(downloadButton);
        
        
        sendToKindleButton = new Button("Kindle", FontAwesome.BOOK);
        sendToKindleButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        sendToKindleButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    presenter.shareBookWithKindle(bookEntry, SecurityUtils.getSubject().getPrincipal().toString());
                    Notification.show("Book is sent to kindle", Notification.Type.HUMANIZED_MESSAGE);
                    loadData(presenter.updateShared(bookEntry, SecurityUtils.getSubject().getPrincipal().toString()));
                } catch(SolrServerException | IOException | MessagingException ex) {
                    Notification.show("Unexpected Error!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                }
            }
        });
        
        likeButton = new Button("0 likes", FontAwesome.THUMBS_O_UP);
        likeButton.addStyleName(ValoTheme.BUTTON_LINK);
        likeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    BookEntry updatedEntry = presenter.updateLike(bookEntry, SecurityUtils.getSubject().getPrincipal().toString());
                    loadData(updatedEntry);
                } catch (SolrServerException | IOException ex) {
                    Notification.show("Unexpected Error!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                }
            }
        });
        downloadCount = new Label("0 downloads");
        
        VerticalLayout rightLayout = new VerticalLayout(sendToKindleButton,downloadButton,likeButton,downloadCount);
        rightLayout.setComponentAlignment(downloadCount, Alignment.MIDDLE_CENTER);
        rightLayout.setSpacing(true);
        
        
        HorizontalLayout layout = new HorizontalLayout(image,rightLayout);
        layout.setSpacing(true);
        return layout;
    }

    public void loadData(BookEntry bookEntry) {
        this.bookEntry = bookEntry;
        titleLabel.setValue(bookEntry.getTitle() + "-" + bookEntry.getAuthor());
        descriptionLabel.setValue(bookEntry.getDescription());
        StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getCover());
        image.setSource(new StreamResource(source, bookEntry.getId() + ".png"));
        likeButton.setCaption(bookEntry.getLikes() + " likes");
        downloadCount.setValue(bookEntry.getDownloads() + " downloads");
        try {
            byte[] data = presenter.getEbookFile(bookEntry.getId());
            fileDownloader.setFileDownloadResource(new StreamResource(new BookStreamSource(data), bookEntry.getTitle() + "-" + bookEntry.getAuthor()+".epub"));
        } catch(SolrServerException ex) {
            logger.error(ex, ex);
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
