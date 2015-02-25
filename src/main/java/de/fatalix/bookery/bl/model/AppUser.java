/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.model;

import de.fatalix.bookery.bl.authentication.AppUserAuthenticationInfo;
import de.fatalix.bookery.bl.authentication.AppUserAuthorizationInfo;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;

/**
 *
 * @author felix.husse
 */
public class AppUser {
    
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public AuthenticationInfo getAsAuthenticationInfo() {
        return new AppUserAuthenticationInfo(this);
    }
    
    public AuthorizationInfo getAsAuthorizationInfo() {
        return new AppUserAuthorizationInfo(this);
    }
    
}
