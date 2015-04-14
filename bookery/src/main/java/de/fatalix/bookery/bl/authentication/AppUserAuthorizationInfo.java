/*
 * Copyright (c) 2015 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.authentication;

import de.fatalix.bookery.bl.model.AppUser;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.Permission;

/**
 *
 * @author felix.husse
 */
public class AppUserAuthorizationInfo implements AuthorizationInfo{
    
    private final AppUser user;

    public AppUserAuthorizationInfo(AppUser user) {
        this.user = user;
    }
    
    @Override
    public Collection<String> getRoles() {
        return Collections.unmodifiableCollection(Arrays.asList(user.getRoles().split(",")));
    }

    @Override
    public Collection<String> getStringPermissions() {
        return Collections.emptySet();
    }

    @Override
    public Collection<Permission> getObjectPermissions() {
        return Collections.emptySet();
    }
    
}
