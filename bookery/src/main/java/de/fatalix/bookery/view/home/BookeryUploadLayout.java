/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.shiro.SecurityUtils;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author Fatalix
 */
public class BookeryUploadLayout extends VerticalLayout{
    
    @Inject private BookeryUploader uploader;
    @Inject private BookeryBookDetails bookDetails;
    
    @Inject private HomePresenter presenter;
    
    private Button saveButton;
    private Button cancelButton;
    
    private byte[] bookData;
    
    @PostConstruct
    private void postInit() {
        addStyleName("bookery-content");
        setMargin(true);
        setSpacing(true);
        addComponent(createMainLayout());
        HorizontalLayout footerLayout = createFooter();
        addComponent(footerLayout);
        setComponentAlignment(footerLayout, Alignment.MIDDLE_RIGHT);
    }
    
    private HorizontalLayout createMainLayout() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addStyleName("wrapping");
        layout.setSpacing(true);
        layout.addComponents(uploader,bookDetails);
        uploader.setWidth(270, Unit.PIXELS);
        uploader.setHeight(160, Unit.PIXELS);
        bookDetails.setWidth(270, Unit.PIXELS);
        bookDetails.setHeight(160, Unit.PIXELS);
        uploader.addBookeryUploaderListener(new BookeryUploader.Listener() {

            @Override
            public void onUploadFinished(byte[] data) {
                saveButton.setEnabled(true);
                bookData = data;
            }

            @Override
            public void removeUpload() {
                saveButton.setEnabled(false);
                bookData = null;
            }

            @Override
            public void onUploadStarted(String filename) {
                if (filename.split("-").length>1) {
                    String author = filename.split("-")[0].trim();
                    String title = filename.split("-")[1].trim();
                    bookDetails.changeBookData(new BookEntry().setTitle(title).setAuthor(author));
                }
                else {
                    bookDetails.changeBookData(new BookEntry().setTitle(filename));
                }
            }
        });
        return layout;
    }
    
    private HorizontalLayout createFooter() {
        
        saveButton = new Button("save", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                BookEntry bookEntry = bookDetails.getBookData();
                if (bookEntry.getTitle() != null || !bookEntry.getTitle().isEmpty()) {
                    bookEntry.setFile(bookData)
                                .setUploader(SecurityUtils.getSubject().getPrincipal().toString());
                    try {
                        presenter.addBook(bookEntry);
                        
                    } catch(SolrServerException | IOException ex) {
                        Notification.show("Error " + ex.getMessage(), Notification.Type.WARNING_MESSAGE);
                    }
                    
                }
                else {
                    Notification.show("Title is empty", Notification.Type.WARNING_MESSAGE);
                }
                
            }
        });
        saveButton.setEnabled(false);
        saveButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        
        cancelButton = new Button("cancel", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                
            }
        });
        cancelButton.addStyleName(ValoTheme.BUTTON_LINK);
        
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponents(saveButton,cancelButton);
        
        return layout;
    }
    
    
}
