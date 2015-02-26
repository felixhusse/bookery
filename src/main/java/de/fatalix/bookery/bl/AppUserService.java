/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

import de.fatalix.bookery.bl.authentication.JPASecurityUtil;
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
    
    @Inject private AppUserDAO appUserDao;
    
    public AppUser getAppUser(String username) {
        return appUserDao.findByUserName(username);
    }
    
    public List<AppUser> getAllAppUser() {
        return appUserDao.findAll();
    }
    
    public AppUser createUser(AppUser user) {
        String password = user.getPassword();
        user.setSalt(JPASecurityUtil.getSalt());
        user.setPassword(JPASecurityUtil.hashPassword(password, user.getSalt()));
        return appUserDao.save(user);
    }
    
    public AppUser updateUserPassword(AppUser user, String password) {
        user.setSalt(JPASecurityUtil.getSalt());
        user.setPassword(JPASecurityUtil.hashPassword(password, user.getSalt()));
        return appUserDao.update(user);
    }
    
    public AppUser updateUser(AppUser user) {
        return appUserDao.update(user);
    }
    
}
