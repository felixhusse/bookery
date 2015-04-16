/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl;


import de.fatalix.bookery.bl.dao.AppUserDAO;
import de.fatalix.bookery.bl.model.AppUser;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.joda.time.DateTime;

/**
 *
 * @author felix.husse
 */
@Stateless
public class AppUserService {

    @Inject
    private AppUserDAO appUserDao;
    
    public AppUser updateLastLogin(String username) {
        AppUser user = appUserDao.findByUserName(username);
        if (user.getCurrentLogin()!=null) {
            if (user.getCurrentLogin().contains("CEST")) {
                user.setLastLogin(null);
            }
            else {
                user.setLastLogin(user.getCurrentLogin());
            }
            
        }
        user.setCurrentLogin(new DateTime().toString());
        return appUserDao.update(user);
    }
    
    public AppUser getAppUser(String username) {
        return appUserDao.findByUserName(username);
    }

    public List<AppUser> getAllAppUser() {
        return appUserDao.findAll();
    }

    public AppUser createUser(AppUser user) {

        return appUserDao.save(user);
    }

    public AppUser updateUserPassword(AppUser user, String password) {
        user.setPassword(password);
        return appUserDao.update(user);
    }

    public AppUser updateUser(AppUser user) {
        return appUserDao.update(user);
    }

    public void deleteUser(AppUser user) {
        appUserDao.delete(user.getId());
    }

}
