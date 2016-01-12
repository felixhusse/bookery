/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery.bl.dao;

import de.fatalix.bookery.bl.DAOBean;
import de.fatalix.bookery.bl.model.WatchList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author felix.husse
 */
@Stateless
public class WatchListDAO extends DAOBean<WatchList>{
    @PersistenceContext(unitName = "bookery-pu")
    private EntityManager entityManager;
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        super.init(entityManager, WatchList.class);
    }
    
    public List<WatchList> findByUserName(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        return getList(params, WatchList.FIND_BY_USERNAME);
    }
    
}
