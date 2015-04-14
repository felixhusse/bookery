/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.authentication;

import de.fatalix.bookery.bl.model.AppUser;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;

/**
 *
 * @author felix.husse
 */
public class AppUserAuthenticationInfo implements SaltedAuthenticationInfo{
    
    private final AppUser user;
    public static final String PW_SALT = "5e4989e7b7571323fae65f7abe299f0e";
    
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
    
    @Override
    public ByteSource getCredentialsSalt() {
        return ByteSource.Util.bytes(PW_SALT);
    }
}
