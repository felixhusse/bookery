/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
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
public class AdminView extends AbstractView {
    
    public static final String id = "admin";
    @Inject
    private AdminPresenter presenter;
    
    @Inject
    private Instance<AppUserCard> appUserCardInstances;
    
    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {

        HorizontalLayout row = new HorizontalLayout();
        row.addStyleName("wrapping");
        row.setSpacing(true);
        
        List<AppUser> userList = presenter.loadUserList();
        for (AppUser appUser : userList) {
            AppUserCard appUserCard = appUserCardInstances.get();
            appUserCard.loadAppUser(appUser);
            row.addComponent(appUserCard);
            
        }
        this.setCompositionRoot(row);
    }
    
    
    
}
