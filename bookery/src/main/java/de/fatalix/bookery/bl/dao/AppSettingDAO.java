/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.dao;

import de.fatalix.bookery.bl.DAOBean;
import de.fatalix.bookery.bl.model.AppSetting;
import de.fatalix.bookery.bl.model.SettingKey;
import java.util.HashMap;
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
public class AppSettingDAO extends DAOBean<AppSetting>{
    
    @PersistenceContext(unitName = "bookery-pu")
    private EntityManager entityManager;
    
    @SuppressWarnings("unused")
    @PostConstruct
    private void init() {
        super.init(entityManager, AppSetting.class);
    }
    
    public AppSetting findByKey(SettingKey key) {
        Map<String, Object> params = new HashMap<>();
        params.put("key", key.getKey());
        if (getFirstEntity(params, AppSetting.FIND_BY_KEY) == null) {
            AppSetting defaultSetting = new AppSetting();
            defaultSetting.setConfigurationKey(key.getKey());
            defaultSetting.setConfigurationValue(key.getDefaultValue());
            return save(defaultSetting);
        }
        return getFirstEntity(params, AppSetting.FIND_BY_KEY);
    }
    
    
}
