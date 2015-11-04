/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.dao;

import de.fatalix.bookery.bl.DAOBean;
import de.fatalix.bookery.bl.model.BatchJobConfiguration;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author felix.husse
 */
@Stateless
public class BatchJobConfigurationDAO extends DAOBean<BatchJobConfiguration>{
    @PersistenceContext(unitName = "bookery-pu")
    private EntityManager entityManager;
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        super.init(entityManager, BatchJobConfiguration.class);
    }
    
}
