/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.authentication;

import de.fatalix.bookery.bl.AppUserService;
import de.fatalix.bookery.bl.model.AppUser;
import javax.inject.Inject;
import org.apache.shiro.authc.AccountException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 *
 * @author felix.husse
 */
public class JPARealm extends AuthorizingRealm {

    @Inject
    private AppUserService service;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        final String username = (String)principals.getPrimaryPrincipal();
        AppUser user = service.getAppUser(username);
        return user.getAsAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if(!(token instanceof UsernamePasswordToken)) {
            throw new IllegalStateException("Token has to be instance of UsernamePasswordToken class");
        }
        
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
        if (usernamePasswordToken.getUsername() == null) {
            throw new AccountException("Null usernames are not allowed by this realm.");
        }
        
        AppUser user = service.getAppUser(usernamePasswordToken.getUsername());
        if(user == null) {
            throw new AuthenticationException("Could not find user");
        }
        
        if(getCredentialsMatcher().doCredentialsMatch(usernamePasswordToken, user.getAsAuthenticationInfo())) {
            return user.getAsAuthenticationInfo();
        }

        throw new AuthenticationException("Failed to authenticate!");
    }


}
