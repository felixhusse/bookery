/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.CDIView;
import com.vaadin.data.Property;
import com.vaadin.event.FieldEvents;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.elasticsearch.BookEntry;
import de.fatalix.bookery.view.AbstractView;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author Fatalix
 */
@CDIView(HomeView.id)
@ViewMenuItem(title = "Home",icon = FontAwesome.HOME,order = ViewMenuItem.BEGINNING)
public class HomeView extends AbstractView implements View{
    public static final String id = "home";
    
    @Inject private HomePresenter presenter;
    
    @Inject private BookeryUploadLayout bookeryUploadLayout;
    private TextField authorField;
    private TextField titleField;
    private TextField categoryField;
    private TextField fileName;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        VerticalLayout root = new VerticalLayout();
        root.addStyleName("bookery-screen");
        root.addComponent(new Label("Home"));     
        root.addComponents(bookeryUploadLayout,createSearchLayout());
        
        this.setCompositionRoot(root);
    }
    
    private VerticalLayout createSearchLayout() {
        final TextField searchText = new TextField();
        final Label resultLabel = new Label("Result:", ContentMode.PREFORMATTED);
        searchText.setImmediate(true);
        searchText.addTextChangeListener(new FieldEvents.TextChangeListener() {

            @Override
            public void textChange(FieldEvents.TextChangeEvent event) {
                System.out.println("New Value: " + event.getText());
                List<BookEntry> bookEntries = presenter.searchBooks(event.getText());
                String resultText = "";
                for (BookEntry bookEntry : bookEntries) {
                    resultText += bookEntry.toString() + "\n";
                }
                resultLabel.setValue("Result ("+bookEntries.size()+"):\n" + resultText);
            }
        });
        searchText.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                
                
            }
        });
        VerticalLayout searchLayout = new VerticalLayout(searchText,resultLabel);
        searchLayout.setMargin(true);
        //searchLayout.addStyleName("bookery-content");
        return searchLayout;
    }

    private void showNotification(Notification notification, String style) {
        // keep the notification visible a little while after moving the
        // mouse, or until clicked
        notification.setPosition(Position.TOP_CENTER);
        notification.setStyleName(ValoTheme.NOTIFICATION_BAR);
        notification.setDelayMsec(2000);
        notification.show(Page.getCurrent());
    }
}
