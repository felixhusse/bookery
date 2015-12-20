/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.home;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
    private HomePresenter presenter;

    private Image image;
    private String bookId;
    private Label titleLabel;
    private Label authorLabel;
    private Label descriptionLabel;
    private Button downloadButton;
    private BookEntry bookEntry;

    @PostConstruct
    private void postInit() {

        descriptionLabel = new Label("Description", ContentMode.HTML);
        descriptionLabel.addStyleName(ValoTheme.LABEL_LIGHT);
        descriptionLabel.setValue("Keine Beschreibung vorhanden");
        this.setMargin(true);
        this.setSpacing(true);
        addStyleName("bookery-content");
        this.addComponents(createBookCoverLayout(), createInfoLayout());

    }

    private VerticalLayout createInfoLayout() {
        authorLabel = new Label("Author");

        titleLabel = new Label("Title");
        titleLabel.addStyleName(ValoTheme.LABEL_H3);
        Button shareButton = new Button("to Kindle", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    presenter.shareBookWithKindle(bookId, SecurityUtils.getSubject().getPrincipal().toString());
                    Notification.show("Book is sent to kindle", Notification.Type.HUMANIZED_MESSAGE);
                } catch(SolrServerException ex) {
                    Notification.show("Solr crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                } catch(MessagingException ex) {
                    Notification.show("Mail crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        shareButton.setIcon(FontAwesome.SHARE_ALT_SQUARE);
        shareButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        shareButton.setWidth("130px");

        downloadButton = new Button("download");
        downloadButton.setIcon(FontAwesome.DOWNLOAD);
        downloadButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        downloadButton.setWidth("130px");
        
        
        VerticalLayout infoLayout = new VerticalLayout(titleLabel, authorLabel, shareButton,downloadButton);
        infoLayout.addStyleName("book-info");
        infoLayout.setWidth("200px");
        return infoLayout;
    }

    private VerticalLayout createBookCoverLayout() {
        image = new Image();
        image.setImmediate(true);
        image.addStyleName("book-cover");
        
        VerticalLayout result = new VerticalLayout(image);
        result.addStyleName("pointer-cursor");
        result.setWidth("130px");
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
