/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

import de.fatalix.bookery.bl.model.AppUser;
import javax.ejb.Stateless;

/**
 *
 * @author felix.husse
 */
@Stateless
public class AppUserService {
    
    
    public AppUser getAppUser(String username) {
        if (username.equals("admin")) {
            AppUser user = new AppUser();
            user.setPassword("password");
            user.setUsername("admin");
            return user;
        }
        return null;
    }
    
}
