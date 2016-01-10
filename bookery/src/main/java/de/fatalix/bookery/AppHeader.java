/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery;

import com.vaadin.cdi.UIScoped;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.view.home.HomeView;
import de.fatalix.bookery.view.newbooks.NewBooksView;
import de.fatalix.bookery.view.search.SearchView;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.vaadin.viritin.layouts.MHorizontalLayout;
import org.vaadin.viritin.layouts.MVerticalLayout;

/**
 *
 * @author Fatalix
 */
@UIScoped
public class AppHeader extends MVerticalLayout{
    
    private TextField searchText;
    private Button logoutButton;
    
    private Map<String,Button> navbarButtons = new HashMap<>();
    
    @PostConstruct
    private void postInit() {
        addStyleName("bookery-header");
        setWidth(100, Unit.PERCENTAGE);
        add(createTop());       
        add(createSearchBar());
        add(createSmallNavBar());
        setSpacing(true);
    }
    
    private HorizontalLayout createTop() {
        Label header = new Label("Bookerys");
        header.addStyleName(ValoTheme.LABEL_BOLD);
        //header.addStyleName(ValoTheme.LABEL_H3);
        header.setSizeUndefined();
        logoutButton = new Button("Hallo", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                ((App)UI.getCurrent()).logout();
            }
        });
        logoutButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        
        MHorizontalLayout layout = new MHorizontalLayout(header,logoutButton);
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setExpandRatio(header, 1.0f);
        layout.setComponentAlignment(header, Alignment.MIDDLE_LEFT);
        layout.setComponentAlignment(logoutButton, Alignment.BOTTOM_RIGHT);
        return layout;
    }
    
    private MHorizontalLayout createSearchBar() {
        searchText = new TextField();
        searchText.setIcon(FontAwesome.SEARCH);
        searchText.addStyleName(ValoTheme.TEXTFIELD_LARGE);
        searchText.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        searchText.setWidth(100, Unit.PERCENTAGE);
        searchText.setInputPrompt("hier einfach suchen..");
        Button searchButton = new Button("such!", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                Navigator navigator = ((App)UI.getCurrent()).getNavigator();
                if (navigator.getState().contains("search")) {
                    navigator.navigateTo(navigator.getState());
                }
                else {
                    navigator.navigateTo(SearchView.id);
                }
                
            }
        });
        searchButton.addStyleName(ValoTheme.BUTTON_LARGE);
        searchText.addShortcutListener(new Button.ClickShortcut(searchButton, ShortcutAction.KeyCode.ENTER));

        MHorizontalLayout layout = new MHorizontalLayout(searchText,searchButton);
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setExpandRatio(searchText, 1.0f);
        return layout;
    }
    
    private MHorizontalLayout createSmallNavBar() {
        Button homeButton = new Button("Home", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ((App)UI.getCurrent()).getNavigator().navigateTo(HomeView.id);
            }
        });
        homeButton.addStyleName(ValoTheme.BUTTON_TINY);
        Button newBooks = new Button("neue Bücher", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ((App)UI.getCurrent()).getNavigator().navigateTo(SearchView.id + "/author/true");
            }
        });
        newBooks.addStyleName(ValoTheme.BUTTON_TINY);
        Button bestBooks = new Button("Die besten Bücher", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ((App)UI.getCurrent()).getNavigator().navigateTo(SearchView.id + "/likes");
            }
        });
        bestBooks.addStyleName(ValoTheme.BUTTON_TINY);
        
        MHorizontalLayout layout = new MHorizontalLayout(homeButton,newBooks,bestBooks);
        
        return layout;
    } 
    
    public void setLoginName(String loginName) {
        logoutButton.setCaption("Hallo, " + loginName);
    }
    
    public String getSearchText() {
        return searchText.getValue();
    }
    
    
}
