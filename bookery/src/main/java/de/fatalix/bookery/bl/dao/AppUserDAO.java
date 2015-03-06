/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.dao;

import de.fatalix.bookery.bl.DAOBean;
import de.fatalix.bookery.bl.model.AppUser;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author felix.husse
 */
public class AppUserDAO extends DAOBean<AppUser>{
    @PersistenceContext(unitName = "bookery-pu")
    private EntityManager entityManager;
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        super.init(entityManager, AppUser.class);
    }
    
    public AppUser findByUserName(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        return getFirstEntity(params, AppUser.FIND_BY_USERNAME);
    }
    
}
