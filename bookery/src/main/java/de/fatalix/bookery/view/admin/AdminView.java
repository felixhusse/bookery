/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.view.common.AbstractView;
import java.io.IOException;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.mail.MessagingException;
import org.apache.solr.client.solrj.SolrServerException;
import org.vaadin.cdiviewmenu.ViewMenuItem;

/**
 *
 * @author felix.husse
 */
@CDIView(AdminView.id)
@RolesAllowed({"admin"})
@ViewMenuItem(title = "Admin",icon = FontAwesome.GEARS,order = ViewMenuItem.END)
public class AdminView extends AbstractView implements AppUserCard.Listener{
    
    public static final String id = "admin";
    
    @Inject private AdminPresenter presenter;
    
    @Inject private Instance<AppUserCard> appUserCardInstances;
    @Inject private ServerSettingsLayout serverSettingsLayout;
    @Inject private BatchJobsLayout batchJobsLayout;
    
    private HorizontalLayout userManagementLayout;
    
    @PostConstruct
    private void postInit() {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setStyleName("admin-screen");
        tabSheet.addTab(createServerSettings(),"Server Settings");
        tabSheet.addTab(createUserManagement(), "User Management");
        tabSheet.addTab(batchJobsLayout,"Batch Jobs");
        this.setCompositionRoot(tabSheet);
    }
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        serverSettingsLayout.loadData();
        batchJobsLayout.enter();
        loadUser();
    }
    
    private void loadUser() {
        List<AppUser> userList = presenter.loadUserList();
        for (AppUser appUser : userList) {
            AppUserCard appUserCard = appUserCardInstances.get();
            appUserCard.loadAppUser(appUser);
            appUserCard.addAppUserCardListener(this);
            userManagementLayout.addComponent(appUserCard,userManagementLayout.getComponentCount()-1);
            
        }
    }
    
    public HorizontalLayout createUserManagement() {
        userManagementLayout = new HorizontalLayout();
        userManagementLayout.addStyleName("wrapping"); 
        userManagementLayout.setSpacing(true);
        userManagementLayout.setMargin(true);

        Label label = new Label("Add new user...");
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_LARGE);
        VerticalLayout  emptyLayout = new VerticalLayout(label);
        emptyLayout.addStyleName("dashed-border");
        emptyLayout.setWidth(380, Unit.PIXELS);
        emptyLayout.setHeight(220, Unit.PIXELS);
        emptyLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        emptyLayout.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                AppUser appUser = presenter.createNewUser();
                AppUserCard appUserCard = appUserCardInstances.get();
                appUserCard.loadAppUser(appUser);
                appUserCard.addAppUserCardListener(AdminView.this);
                userManagementLayout.addComponent(appUserCard, userManagementLayout.getComponentCount()-1);
            }
        });
        userManagementLayout.addComponent(emptyLayout);
        return userManagementLayout;
    }
    
    public VerticalLayout createServerSettings() {
        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Label titleLabel = new Label("General Settings");
        titleLabel.addStyleName(ValoTheme.LABEL_H2);
        
        //layout.addComponent(titleLabel);
        layout.addComponent(serverSettingsLayout);
        Button resetIndex = new Button("reset Index", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                
                try {
                    presenter.resetIndex();
                    Notification.show("Succesfully reset Index", Notification.Type.HUMANIZED_MESSAGE);
                } catch(IOException | SolrServerException ex) {
                    Notification.show(ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        resetIndex.addStyleName(ValoTheme.BUTTON_DANGER);

        final TextField eMailAdress = new TextField(null, "felix.husse@medavis.de");
        eMailAdress.setColumns(35);
        Button testMail = new Button("Test Mail", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    presenter.sendEmail(eMailAdress.getValue());
                    Notification.show("Mail succesfully sent!", Notification.Type.HUMANIZED_MESSAGE);
                } catch (MessagingException ex) {
                    Notification.show("Mail failed!" + ex.getMessage(), Notification.Type.ERROR_MESSAGE);
                }
            }
        });
        testMail.setEnabled(true);
        HorizontalLayout mailLayout = new HorizontalLayout(eMailAdress,testMail);
        layout.addComponents(resetIndex,mailLayout);
        
        return layout;
    }
    
    @Override
    public void userDeleted(AppUserCard appUserCard) {
        userManagementLayout.removeComponent(appUserCard);
    }
    
}
