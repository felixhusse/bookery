/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.solr.BookEntry;
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
    
    
    
    @PostConstruct
    private void postInit() {
        image = new Image();
        image.setImmediate(true);
        image.addStyleName("book-cover");

        authorLabel = new Label("Author");
        authorLabel.addStyleName(ValoTheme.LABEL_BOLD);
        authorLabel.addStyleName(ValoTheme.LABEL_COLORED);
        
        descriptionLabel = new Label("Description",ContentMode.HTML);
        descriptionLabel.addStyleName(ValoTheme.LABEL_LIGHT);

        VerticalLayout infoLayout = new VerticalLayout(createBookTitleLayout(),authorLabel,descriptionLabel);
        this.setMargin(true);
        this.setSpacing(true);
        //addStyleName("wrapping"); 
        addStyleName("bookery-content");
        this.addComponents(image,infoLayout);
        this.setExpandRatio(infoLayout, 1.0f);
        this.setWidth(100, Unit.PERCENTAGE);
    }
    
    private HorizontalLayout createBookTitleLayout() {
        HorizontalLayout titleLayout = new BookDetailLayout();
        titleLabel = new Label("Title");
        titleLabel.addStyleName(ValoTheme.LABEL_H2);
        
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
        
        titleLayout.addComponents(titleLabel,shareButton);
        titleLayout.setWidth(100, Unit.PERCENTAGE);
        titleLayout.setComponentAlignment(shareButton, Alignment.MIDDLE_RIGHT);
        
        return titleLayout;
    }
    
    public void loadData(BookEntry bookEntry) {
        this.bookId = bookEntry.getId();
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
