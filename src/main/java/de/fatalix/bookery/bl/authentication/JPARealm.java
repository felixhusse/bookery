/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.authentication;

import de.fatalix.bookery.bl.model.AppUser;
import javax.ejb.EJB;
import javax.inject.Inject;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

/**
 *
 * @author felix.husse
 */
public class JPARealm extends AuthorizingRealm {

    @EJB
    private AppUserService service;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        Object availablePrincipal = getAvailablePrincipal(principals);

        if (availablePrincipal != null) {
            String principalName = availablePrincipal.toString();
            return service.getAppUser(principalName).getAsAuthorizationInfo();
        }

        return new SimpleAuthorizationInfo();
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        if(token == null) {
            throw new AuthenticationException("PrincipalCollection method argument cannot be null.");
        }

        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken)token;
        AppUser user = service.getAppUser(usernamePasswordToken.getUsername());
        if (user == null) {
            throw new AuthenticationException("Could not find user");
        }
        if (getCredentialsMatcher().doCredentialsMatch(usernamePasswordToken,user.getAsAuthenticationInfo())) {
            return user.getAsAuthenticationInfo();
        }
        
        throw new AuthenticationException("Failed to authenticate");
    }

}
