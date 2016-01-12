/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.model;

import de.fatalix.bookery.bl.EntityIntf;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;

/**
 *
 * @author felix.husse
 */
@Entity
@NamedQueries({
    @NamedQuery(name=WatchList.FIND_BY_USERNAME, query="SELECT s FROM WatchList s WHERE s.user.username =:username"),
    @NamedQuery(name=WatchList.FIND_BY_USERNAME_BOOKID, query="SELECT s FROM WatchList s WHERE s.user.username =:username AND s.bookId =:bookId")
    
})
public class WatchList implements EntityIntf, Serializable{
    
    public static final String FIND_BY_USERNAME = "WatchList.findByUser";
    public static final String FIND_BY_USERNAME_BOOKID = "WatchList.findByUserAndBookID";
    
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    
    private String bookId;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date watchDate; 
    
    @ManyToOne
    @JoinColumn(name="userID")
    private AppUser user;
    
    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Date getWatchDate() {
        return watchDate;
    }

    public void setWatchDate(Date watchDate) {
        this.watchDate = watchDate;
    }

    public AppUser getUser() {
        return user;
    }

    public void setUser(AppUser user) {
        this.user = user;
    }
    
    
}
