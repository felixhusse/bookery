/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.solr.model.BookEntry;
import de.fatalix.bookery.view.BookDetailPresenter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author Fatalix
 */
public class BookDetailLayout extends HorizontalLayout {

    @Inject
    private BookDetailPresenter presenter;

    private Image image;
    private String bookId;
    private Label titleLabel;
    private Label authorLabel;
    private Label descriptionLabel;
    private Button downloadButton;
    private Label likeCount;
    private Button likeButton;
    private Label downloadCount;
    private BookEntry bookEntry;

    @PostConstruct
    private void postInit() {

        descriptionLabel = new Label("Description", ContentMode.HTML);
        descriptionLabel.addStyleName(ValoTheme.LABEL_LIGHT);
        descriptionLabel.setValue("Keine Beschreibung vorhanden");
        this.setMargin(false);
        this.setSpacing(true);
        addStyleName("bookery-detail");
        this.addComponents(createBookCoverLayout(), createInfoLayout());

    }

    private VerticalLayout createInfoLayout() {
        authorLabel = new Label("Author");
        authorLabel.addStyleName("bookery-italic");
        titleLabel = new Label("Title");
        titleLabel.addStyleName(ValoTheme.LABEL_H3);
        titleLabel.setHeight("60px");
        
        Button shareButton = new Button("", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    presenter.shareBookWithKindle(bookEntry, SecurityUtils.getSubject().getPrincipal().toString());
                    Notification.show("Book is sent to kindle", Notification.Type.HUMANIZED_MESSAGE);
                    loadData(presenter.updateShared(bookEntry, SecurityUtils.getSubject().getPrincipal().toString()));
                } catch(SolrServerException ex) {
                    Notification.show("Solr crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                } catch(MessagingException ex) {
                    Notification.show("Mail crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                } catch (IOException ex) {
                    Notification.show("Unexpected Error!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    Logger.getLogger(BookDetailLayout.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        shareButton.setDescription("Send to Kindle");
        shareButton.setIcon(FontAwesome.BOOK);
        shareButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        //shareButton.setWidth("100px");

        downloadButton = new Button("");
        downloadButton.setDescription("Download");
        downloadButton.setIcon(FontAwesome.DOWNLOAD);
        downloadButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        //downloadButton.setWidth("100px");
        
        downloadCount = new Label("0 downs");
        
        HorizontalLayout shareButtons = new HorizontalLayout(shareButton,downloadButton,downloadCount);
        shareButtons.setSpacing(true);
        
        likeButton = new Button("",new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    BookEntry updatedEntry = presenter.updateLike(bookEntry, SecurityUtils.getSubject().getPrincipal().toString());
                    loadData(updatedEntry);
                } catch (SolrServerException ex) {
                    Notification.show("Solr crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    Logger.getLogger(BookDetailLayout.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                     Notification.show("Unexpected Error!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    Logger.getLogger(BookDetailLayout.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        likeButton.setDescription("like");
        
        likeButton.setIcon(FontAwesome.THUMBS_O_UP);
        likeButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        likeCount = new Label("0 likes");
        HorizontalLayout likeButtons = new HorizontalLayout(likeButton,likeCount);
        likeButtons.setSpacing(true);
        likeButtons.setComponentAlignment(likeCount, Alignment.MIDDLE_CENTER);
        
        VerticalLayout infoLayout = new VerticalLayout(titleLabel, authorLabel, shareButtons,likeButtons);
        infoLayout.setSpacing(true);
        infoLayout.addStyleName("book-info");
        infoLayout.setWidth("170px");
        
        return infoLayout;
    }

    private VerticalLayout createBookCoverLayout() {
        image = new Image();
        image.setImmediate(true);
        image.setWidth("130px");
        image.setHeight("240px");
        
        VerticalLayout result = new VerticalLayout(image);
        result.setWidth("130px");
        result.setHeight("240px");
        result.addStyleName("pointer-cursor");
        result.addStyleName("book-cover");
        result.setComponentAlignment(image, Alignment.MIDDLE_CENTER);

        result.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                Window descriptionWindow = new Window("Description");
                descriptionWindow.setWidth(400.0f, Unit.PIXELS);
                descriptionWindow.setModal(true);
                VerticalLayout content = new VerticalLayout(descriptionLabel);
                content.setMargin(true);
                descriptionWindow.setContent(content);
                
                UI.getCurrent().addWindow(descriptionWindow);
            }
        });
        return result;
    }

    public void loadData(BookEntry bookEntry) {
        this.bookEntry = bookEntry;
        this.bookId = bookEntry.getId();
        if(bookEntry.getThumbnail() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getThumbnail());
            image.setSource(new StreamResource(source, bookEntry.getId() + "_thumb.png"));
        } else if(bookEntry.getCover() != null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getCover());
            image.setSource(new StreamResource(source, bookEntry.getId() + ".png"));
        }

        String user = SecurityUtils.getSubject().getPrincipal().toString();

        titleLabel.setValue(bookEntry.getTitle());
        authorLabel.setValue(bookEntry.getAuthor());
        descriptionLabel.setValue(bookEntry.getDescription());
        likeCount.setValue(bookEntry.getLikes() + " likes");
        if (bookEntry.getLikedby()!=null) {
            for (String likedBy : bookEntry.getLikedby()) {
                if (likedBy.equals(user)) {
                    likeButton.setIcon(FontAwesome.THUMBS_O_DOWN);
                    break;
                }
            }
        }
        downloadCount.setValue(bookEntry.getDownloads() + " downs");
        FileDownloader fileDownloader = new FileDownloader(new StreamResource(new EbookStreamSource(presenter, bookEntry),bookEntry.getTitle() + "-" + bookEntry.getAuthor()+".epub"));
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
