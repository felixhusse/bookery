/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.model;

import de.fatalix.bookery.bl.EntityIntf;
import de.fatalix.bookery.bl.authentication.AppUserAuthenticationInfo;
import de.fatalix.bookery.bl.authentication.AppUserAuthorizationInfo;
import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.crypto.hash.Sha512Hash;

/**
 *
 * @author felix.husse
 */
@Entity
@NamedQueries({
    @NamedQuery(name=AppUser.FIND_BY_USERNAME, query="SELECT s FROM AppUser s WHERE s.username =:username"),
})
public class AppUser implements EntityIntf, Serializable{
    
    public static final String FIND_BY_USERNAME = "AppUser.findByUserName";
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    
    private String username;
    private String password;
    private String eMail;
    private String fullname;
    private String roles;
    private String salt;
    
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
    
    /**
     * Sets the password in human readable format. The password will internally
     * be hashed.
     * 
     * @param password
     */
    public void setHumanReadablePassword(String password) {
        this.password = new Sha512Hash(password, AppUserAuthenticationInfo.PW_SALT)
                .toHex();
    }

    /*
     * This getter shouldn't exist but it's here because apache commons bean
     * utils will fail otherwise.
     */
    public String getHumanReadablePassword() {
        return null;
    }

    public void setPassword(String password) {
        if (password.length() != 128) {
            setHumanReadablePassword(password);
        } else {
            this.password = password;
        }
    }
    
    public AuthenticationInfo getAsAuthenticationInfo() {
        return new AppUserAuthenticationInfo(this);
    }
    
    public AuthorizationInfo getAsAuthorizationInfo() {
        return new AppUserAuthorizationInfo(this);
    }
    
}
