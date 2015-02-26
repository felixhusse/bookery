/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery;

import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.model.AppUser;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

/**
 *
 * @author felix.husse
 */
@Startup
@Singleton
public class AppStartup {
    
    @Inject private AppUserService service;
    
    @PostConstruct
    private void init() {
        if (service.getAllAppUser().isEmpty()) {
            AppUser defaultAdminUser = new AppUser();
            defaultAdminUser.setUsername("admin");
            defaultAdminUser.setPassword("password");
            defaultAdminUser.setFullname("Administrator");
            defaultAdminUser.setRoles("admin,user,visitor");
            service.createUser(defaultAdminUser);
            
            AppUser defaultUser = new AppUser();
            defaultUser.setUsername("user");
            defaultUser.setPassword("felix");
            defaultUser.setFullname("User");
            defaultUser.setRoles("user");
            service.createUser(defaultUser);
        }
    }
    
}
