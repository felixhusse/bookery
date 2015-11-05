/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery;

import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.BookeryService;
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
    @Inject private BookeryService bookeryService;
    
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
        bookeryService.fireUpBatchJobs();
    }
    
}
