/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.app.bl.authentication;

import de.fatalix.app.bl.model.AppUser;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

/**
 *
 * @author felix.husse
 */
public class AppUserAuthenticationInfo implements AuthenticationInfo{
    
    private final AppUser user;
    
    public AppUserAuthenticationInfo(AppUser user) {
        this.user = user;
    }
    
    @Override
    public PrincipalCollection getPrincipals() {
        SimplePrincipalCollection principalCollection = new SimplePrincipalCollection();
        principalCollection.add(user.getUsername(), "JPA");
        return principalCollection;
    }

    @Override
    public Object getCredentials() {
        return user.getPassword();
    }
    
}
