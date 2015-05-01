/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.home;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
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
public class BookDetailLayout extends HorizontalLayout{
    
    @Inject private HomePresenter presenter;
    
    private Image image;
    private String bookId;
    private Label titleLabel;
    private Label authorLabel;
    private Label descriptionLabel;
    private Button bookIsRead;
    
    private BookEntry bookEntry;
    
    private boolean isRead = false;
    
    @PostConstruct
    private void postInit() {
        authorLabel = new Label("Author");
        authorLabel.addStyleName(ValoTheme.LABEL_BOLD);
        authorLabel.addStyleName(ValoTheme.LABEL_COLORED);
        
        descriptionLabel = new Label("Description",ContentMode.HTML);
        descriptionLabel.addStyleName(ValoTheme.LABEL_LIGHT);

        titleLabel = new Label("Title");
        titleLabel.addStyleName(ValoTheme.LABEL_H2);
        
        bookIsRead = new Button(FontAwesome.CIRCLE);
        bookIsRead.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        bookIsRead.addStyleName(ValoTheme.BUTTON_HUGE);
        bookIsRead.addStyleName("book-is-notread");
        bookIsRead.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (!isRead) {
                    try {
                        String user = SecurityUtils.getSubject().getPrincipal().toString();
                        loadData(presenter.setBookAsRead(bookEntry.getId(), user));
                    } catch(SolrServerException ex) {
                        Notification.show("Solr crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                    }
                }
            }
        });
        VerticalLayout infoLayout = new VerticalLayout(titleLabel,authorLabel);
        infoLayout.addStyleName("book-info");
        this.setMargin(true);
        this.setSpacing(true);
        //addStyleName("wrapping"); 
        addStyleName("bookery-content");
        this.addComponents(createBookCoverLayout(),infoLayout);
        this.setExpandRatio(infoLayout, 1.0f);
        this.setWidth(100, Unit.PERCENTAGE);
    }
    
    private VerticalLayout createBookCoverLayout() {
        image = new Image();
        image.setImmediate(true);
        image.addStyleName("book-cover");
        
        Button shareButton = new Button("to Kindle", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    presenter.shareBookWithKindle(bookId, SecurityUtils.getSubject().getPrincipal().toString());
                    Notification.show("Book is sent to kindle", Notification.Type.HUMANIZED_MESSAGE);
                } catch (SolrServerException ex) {
                    Notification.show("Solr crashed!\n" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                } catch (MessagingException ex) {
                    Notification.show("Mail crashed!\n"+ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        shareButton.setIcon(FontAwesome.SHARE_ALT_SQUARE);
        shareButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        shareButton.setWidth("130px");
        VerticalLayout result = new VerticalLayout(image,shareButton);
        result.setWidth("131px");
        return result;
    }
    
    public void loadData(BookEntry bookEntry) {
        this.bookEntry = bookEntry;
        this.bookId = bookEntry.getId();
        if (bookEntry.getCover()!=null) {
            StreamResource.StreamSource source = new ByteStreamResource(bookEntry.getCover());
            image.setSource(new StreamResource(source, bookEntry.getId()+".png"));
        }
        bookIsRead.setIcon(FontAwesome.CIRCLE);
        isRead = false;
        String user = SecurityUtils.getSubject().getPrincipal().toString();
        if (bookEntry.getReader() != null) {
            for (String reader : bookEntry.getReader()) {
                if (reader.equals(user)) {
                    bookIsRead.setIcon(FontAwesome.CHECK_CIRCLE);
                    bookIsRead.removeStyleName("book-is-notread");
                    bookIsRead.addStyleName("book-is-read");
                    isRead = true;
                    break;
                }
            }    
        }
        
        
        titleLabel.setValue(bookEntry.getTitle());
        authorLabel.setValue(bookEntry.getAuthor());
        //descriptionLabel.setValue(bookEntry.getDescription());
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
