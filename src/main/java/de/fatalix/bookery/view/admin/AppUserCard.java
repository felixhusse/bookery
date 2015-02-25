/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.model.AppUser;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

/**
 *
 * @author felix.husse
 */
public class AppUserCard extends CssLayout{
    
    private final List<Listener> listeners = new ArrayList<>();
    
    private AppUser appUser;
    
    private Label captionLabel;
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField fullnameField;
    private TextField eMailField;
    private TextField roles;
    
    @PostConstruct
    private void postInit() {
        addStyleName("card");
        addComponents(createHeader(),createContent());
    }
    
    private HorizontalLayout createHeader() {
        captionLabel = new Label("some.user");
        Button deleteUser = new Button(null, new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                
            }
        });
        deleteUser.setIcon(FontAwesome.TIMES_CIRCLE);
        deleteUser.addStyleName("borderless");
        deleteUser.addStyleName("icon-only");
        
        HorizontalLayout captionLayout = new HorizontalLayout();
        captionLayout.addStyleName("v-panel-caption");
        captionLayout.setWidth("100%");
        captionLayout.addComponents(captionLabel,deleteUser);
        captionLayout.setExpandRatio(captionLabel, 1);
        
        return captionLayout;
    }
    
    private FormLayout createContent() {
        usernameField = new TextField("Username","some.user");
        passwordField = new PasswordField("Password", "password");
        fullnameField = new TextField("Fullname","Some User");
        eMailField = new TextField("EMail","user@some.de");
        roles = new TextField("Roles","user");
        
        FormLayout userCardContent = new FormLayout(usernameField,passwordField,fullnameField,eMailField,roles);
        userCardContent.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        userCardContent.setMargin(true);
        
        return userCardContent;
    }
    
    public void loadAppUser(AppUser user) {
        this.appUser = user;
        usernameField.setValue(user.getUsername());
        passwordField.setValue(user.getPassword());
        fullnameField.setValue(user.getFullname());
        eMailField.setValue(user.geteMail());
        roles.setValue(user.getRoles());
        captionLabel.setValue(user.getUsername());
    }
    
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    public interface Listener {
        void userDeleted(AppUserCard appUserCard);
    }
}
