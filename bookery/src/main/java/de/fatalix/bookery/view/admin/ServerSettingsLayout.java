/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.event.FieldEvents;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.model.AppSetting;
import de.fatalix.bookery.bl.model.SettingKey;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 *
 * @author Fatalix
 */
public class ServerSettingsLayout extends FormLayout implements FieldEvents.BlurListener{
    
    @Inject
    private AdminPresenter presenter;
    
    private List<AbstractTextField> fields;
    
    @PostConstruct
    private void postInit() {
        addStyleName("bookery-content");
        addStyleName("light");
        setMargin(true);
        setSpacing(true);
        Label titleLabel = new Label("General Settings");
        titleLabel.addStyleName(ValoTheme.LABEL_H2);
        addComponent(titleLabel);
        
        addComponents(generateFields());
    }
    
    
    private AbstractTextField[] generateFields() {
        fields = new ArrayList<>();
        for (SettingKey key : SettingKey.values()) {
            AbstractTextField field = new TextField(key.getName());
            field.setColumns(25);
            field.setId(key.getKey());
            field.addBlurListener(this);
            fields.add(field);
        }
        AbstractTextField[] result = new AbstractTextField[fields.size()];
        return fields.toArray(result);
    }
    
    public void loadData() {
        for (AbstractTextField field : fields) {
            SettingKey key = SettingKey.getEnumByKey(field.getId());
            AppSetting setting = presenter.loadSetting(key);
            field.setValue(setting.getConfigurationValue());
        }
    }
    
    @Override
    public void blur(FieldEvents.BlurEvent event) {
        SettingKey key = SettingKey.getEnumByKey(event.getComponent().getId());
         for (AbstractTextField field : fields) {
             if (field.getId().equals(key.getKey())) {
                 presenter.updateSetting(key, field.getValue());
                 break;
             }
         }
    }
}
