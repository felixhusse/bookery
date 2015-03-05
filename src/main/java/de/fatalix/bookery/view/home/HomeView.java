/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.home;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
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
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        VerticalLayout root = new VerticalLayout();
        root.addStyleName("bookery-screen");
        root.addComponent(new Label("Home"));
        
        Button addBookButton = new Button("Add Book", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                Path path = FileSystems.getDefault().getPath("e:\\", "book.pdf");
                
                try {
                    BookEntry bookEntry = new BookEntry()
                            .setAuthor("Wilhelm Hauff")
                            .setCategory("Märchen")
                            .setTitle("Der Mann im Mond")
                            .setRating(3)
                            .setFile(Files.readAllBytes(path));
                    
                    presenter.addBook(bookEntry);
                } catch (IOException ex) {
                    Logger.getLogger(HomeView.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
            }
        });
        final Label resultLabel = new Label("Result:");
        final TextField searchText = new TextField();
        Button searchButton = new Button("Search", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                
                List<BookEntry> bookEntries = presenter.searchBooks(searchText.getValue());
                String resultText = "";
                for (BookEntry bookEntry : bookEntries) {
                    resultText += bookEntry.toString() + "\n";
                }
                resultLabel.setValue("Result ("+bookEntries.size()+"):\n" + resultText);
            }
        });
        
        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.addComponents(searchText,searchButton);
        root.addComponent(addBookButton);
        root.addComponents(searchLayout,resultLabel);
        
        this.setCompositionRoot(root);
    }
}
