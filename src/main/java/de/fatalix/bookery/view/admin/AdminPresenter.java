/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.model.AppUser;
import java.util.List;
import javax.inject.Inject;

/**
 *
 * @author felix
 */
@UIScoped
public class AdminPresenter {
    
    @Inject private AppUserService service;
    
    public List<AppUser> loadUserList() {
        return service.getAllAppUser();
    }
    
    public AppUser updateUser(AppUser user) {
        return null;
    }
    
    public AppUser createNewUser() {
        AppUser user = new AppUser();
        user.setUsername("newuser");
        return service.createUser(user);
    }
    
}
