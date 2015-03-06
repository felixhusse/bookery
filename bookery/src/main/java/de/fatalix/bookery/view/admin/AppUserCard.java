/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.event.FieldEvents;
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
import javax.inject.Inject;

/**
 *
 * @author felix.husse
 */
public class AppUserCard extends CssLayout {

    private final List<Listener> listeners = new ArrayList<>();

    @Inject
    private AdminPresenter presenter;

    private AppUser appUser;

    private Label captionLabel;
    private TextField usernameField;
    private PasswordField passwordField;
    private TextField fullnameField;
    private TextField eMailField;
    private TextField roles;

    @PostConstruct
    private void postInit() {
        addStyleName("bookery-content");
        addComponents(createHeader(), createContent());
        setWidth(380, Unit.PIXELS);
        setHeight(240, Unit.PIXELS);
    }

    private HorizontalLayout createHeader() {
        captionLabel = new Label("some.user");
        Button deleteUser = new Button(null, new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                presenter.deleteUser(appUser);
                for (Listener listener : listeners) {
                    listener.userDeleted(AppUserCard.this);
                }
            }
        });
        deleteUser.setIcon(FontAwesome.TIMES_CIRCLE);
        deleteUser.addStyleName("borderless");
        deleteUser.addStyleName("icon-only");

        HorizontalLayout captionLayout = new HorizontalLayout();
        captionLayout.addStyleName("v-panel-caption");
        captionLayout.setWidth("100%");
        captionLayout.addComponents(captionLabel, deleteUser);
        captionLayout.setExpandRatio(captionLabel, 1);
        
        return captionLayout;
    }

    private FormLayout createContent() {
        usernameField = new TextField("Username", "some.user");
        passwordField = new PasswordField("Password", "password");
        fullnameField = new TextField("Fullname", "Some User");
        eMailField = new TextField("EMail", "user@some.de");
        roles = new TextField("Roles", "user");

        FormLayout userCardContent = new FormLayout(usernameField, passwordField, fullnameField, eMailField, roles);
        userCardContent.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        userCardContent.setMargin(true);

        return userCardContent;
    }

    public void loadAppUser(AppUser user) {
        this.appUser = user;
        setUserFields();

        usernameField.addBlurListener(new FieldEvents.BlurListener() {

            @Override
            public void blur(FieldEvents.BlurEvent event) {
                if(!appUser.getUsername().equals(usernameField.getValue())) {
                    appUser.setUsername(usernameField.getValue());
                    updateUser();
                }
            }
        });

        passwordField.addBlurListener(new FieldEvents.BlurListener() {

            @Override
            public void blur(FieldEvents.BlurEvent event) {
                if(!passwordField.getValue().equals("nopeNopeNope")) {
                    presenter.updatePassword(appUser, passwordField.getValue());
                    updateUser();
                }
            }
        });
        fullnameField.setNullRepresentation("");
        fullnameField.addBlurListener(new FieldEvents.BlurListener() {

            @Override
            public void blur(FieldEvents.BlurEvent event) {
                if(appUser.getFullname() == null || !appUser.getFullname().equals(fullnameField.getValue())) {
                    appUser.setFullname(fullnameField.getValue());
                    updateUser();
                }
            }
        });
        eMailField.setNullRepresentation("");
        eMailField.addBlurListener(new FieldEvents.BlurListener() {

            @Override
            public void blur(FieldEvents.BlurEvent event) {
                if(appUser.geteMail() == null || !appUser.geteMail().equals(eMailField.getValue())) {
                    appUser.seteMail(eMailField.getValue());
                    updateUser();
                }
            }
        });
        roles.setNullRepresentation("");
        roles.addBlurListener(new FieldEvents.BlurListener() {

            @Override
            public void blur(FieldEvents.BlurEvent event) {
                if(appUser.getRoles() == null || !appUser.getRoles().equals(roles.getValue())) {
                    appUser.setRoles(roles.getValue());
                    updateUser();
                }
            }
        });

    }

    private void setUserFields() {
        usernameField.setValue(appUser.getUsername());
        
        fullnameField.setValue(appUser.getFullname());
        passwordField.setValue("nopeNopeNope");
        eMailField.setValue(appUser.geteMail());
        roles.setValue(appUser.getRoles());
        captionLabel.setValue(appUser.getUsername());
    }

    protected void updateUser() {
        appUser = presenter.updateUser(appUser);
        setUserFields();
    }

    public void addAppUserCardListener(Listener listener) {
        listeners.add(listener);
    }

    public interface Listener {
        void userDeleted(AppUserCard appUserCard);
    }
}
