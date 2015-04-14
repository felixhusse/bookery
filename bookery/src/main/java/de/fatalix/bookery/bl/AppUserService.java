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

/**
 *
 * @author felix.husse
 */
@Stateless
public class AppUserService {

    @Inject
    private AppUserDAO appUserDao;

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
