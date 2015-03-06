/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.cdi.CDIView;
import com.vaadin.event.LayoutEvents;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.model.AppUser;
import de.fatalix.bookery.view.AbstractView;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
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
    private HorizontalLayout userManagementLayout;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        TabSheet tabSheet = new TabSheet();
        tabSheet.setStyleName("admin-screen");
        tabSheet.addTab(createServerSettings(),"Server Settings");
        tabSheet.addTab(createUserManagement(), "User Managements");
        serverSettingsLayout.loadData();
        this.setCompositionRoot(tabSheet);
    }
    
    public HorizontalLayout createUserManagement() {
        userManagementLayout = new HorizontalLayout();
        userManagementLayout.addStyleName("wrapping"); 
        userManagementLayout.setSpacing(true);
        userManagementLayout.setMargin(true);
        
        List<AppUser> userList = presenter.loadUserList();
        for (AppUser appUser : userList) {
            AppUserCard appUserCard = appUserCardInstances.get();
            appUserCard.loadAppUser(appUser);
            appUserCard.addAppUserCardListener(this);
            userManagementLayout.addComponent(appUserCard);
            
        }
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
        layout.addComponent(serverSettingsLayout);
        return layout;
    }
    
    @Override
    public void userDeleted(AppUserCard appUserCard) {
        userManagementLayout.removeComponent(appUserCard);
    }
    
}
