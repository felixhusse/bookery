/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view;

import com.vaadin.cdi.UIScoped;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
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

public class BookDetailDialog extends Window{
    
    @Inject private BookDetailPresenter presenter;
    @Inject private Logger logger;
    
    private BookEntry bookEntry;
    
    private Label titleLabel;
    private Label descriptionLabel;
    private Image image;
    private FileDownloader fileDownloader;
    private Button shareButton;
    private Button downloadButton;
    private Button likeButton;
    
    private Label likeCountLabel;
    private Label loadCountLabel;
    
    @PostConstruct
    private void postInit() {
        setCaption("Buch details");

        HorizontalLayout root = new HorizontalLayout(createRightSide(),createLeftSide());
        root.setSpacing(true);
        root.setMargin(true);
        setWidth(600, Unit.PIXELS);
        setHeight(600, Unit.PIXELS);
        setModal(true);
        this.setContent(root);
    }
    
    private VerticalLayout createRightSide() {
        image = new Image();
        image.setImmediate(true);
        image.setHeight("200px");
        image.setWidth("130px");
        
        likeCountLabel = new Label("0 likes");
        loadCountLabel = new Label("0 downloads");
        
        VerticalLayout rightRoot = new VerticalLayout(image,likeCountLabel,loadCountLabel);
        rightRoot.setWidth("140px");
        //rightRoot.setSpacing(true);
        
        return rightRoot;
    }
    
    private VerticalLayout createLeftSide() {
        
        titleLabel = new Label("Title");
        titleLabel.addStyleName(ValoTheme.LABEL_H3);
        
        descriptionLabel = new Label("Description", ContentMode.HTML);
        descriptionLabel.addStyleName(ValoTheme.LABEL_LIGHT);
        descriptionLabel.setValue("Keine Beschreibung vorhanden");
        
        shareButton = new Button(FontAwesome.BOOK);
        shareButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        shareButton.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    presenter.shareBookWithKindle(bookEntry, SecurityUtils.getSubject().getPrincipal().toString());
                    Notification.show("Book is sent to kindle", Notification.Type.HUMANIZED_MESSAGE);
                    loadData(presenter.updateShared(bookEntry, SecurityUtils.getSubject().getPrincipal().toString()));
                } catch(SolrServerException ex) {
                    Notification.show("Solr crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                } catch(MessagingException ex) {
                    Notification.show("Mail crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                } catch (IOException ex) {
                    Notification.show("Unexpected Error!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                }
            }
        });
        
        downloadButton = new Button(FontAwesome.DOWNLOAD);
        downloadButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        downloadButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    loadData(presenter.updateShared(bookEntry, SecurityUtils.getSubject().getPrincipal().toString()));
                } catch(SolrServerException ex) {
                    Notification.show("Solr crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                } catch (IOException ex) {
                    Notification.show("Unexpected Error!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                }
            }
        });
        //downloadButton.setEnabled(false);
        
        likeButton = new Button(FontAwesome.THUMBS_O_UP);
        likeButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        likeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    BookEntry updatedEntry = presenter.updateLike(bookEntry, SecurityUtils.getSubject().getPrincipal().toString());
                    loadData(updatedEntry);
                } catch (SolrServerException ex) {
                    Notification.show("Solr crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                } catch (IOException ex) {
                     Notification.show("Unexpected Error!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    logger.error(ex, ex);
                }
            }
        });
        
        HorizontalLayout buttonLayout = new HorizontalLayout(shareButton,downloadButton,likeButton);
        buttonLayout.setSpacing(true);
        VerticalLayout leftRoot = new VerticalLayout(titleLabel,descriptionLabel,buttonLayout);
        leftRoot.setWidth("470px");
        
        return leftRoot;
    }
    
    
    public void loadData(BookEntry bookEntry) {
        String user = SecurityUtils.getSubject().getPrincipal().toString();
        this.bookEntry = bookEntry;
        titleLabel.setValue(bookEntry.getTitle() + " - " + bookEntry.getAuthor());
        descriptionLabel.setValue(bookEntry.getDescription());
        likeCountLabel.setValue(bookEntry.getLikes() + " likes");
        loadCountLabel.setValue(bookEntry.getDownloads()+ " downloads");
        if (bookEntry.getLikedby()!=null) {
            for (String likedBy : bookEntry.getLikedby()) {
                if (likedBy.equals(user)) {
                    likeButton.setIcon(FontAwesome.THUMBS_O_DOWN);
                    break;
                }
            }
        }
        
        if(bookEntry.getThumbnail() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getThumbnail());
            image.setSource(new StreamResource(source, bookEntry.getId() + "_thumb.png"));
        } else if(bookEntry.getCover() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getCover());
            image.setSource(new StreamResource(source, bookEntry.getId() + ".png"));
        }

        fileDownloader = new FileDownloader(new StreamResource(new EbookStreamSource(presenter, bookEntry),bookEntry.getTitle() + "-" + bookEntry.getAuthor()+".epub"));
        fileDownloader.extend(downloadButton);
        
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
