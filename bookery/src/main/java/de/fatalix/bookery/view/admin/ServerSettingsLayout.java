/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.admin;

import com.vaadin.event.FieldEvents;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import de.fatalix.bookery.bl.model.AppSetting;
import de.fatalix.bookery.bl.model.SettingKey;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrServerException;

/**
 *
 * @author Fatalix
 */
public class ServerSettingsLayout extends VerticalLayout implements FieldEvents.BlurListener {

    @Inject
    private AdminPresenter presenter;
    private FormLayout formLayout;
    private List<AbstractTextField> fields;

    private List<Label> statusFields;

    @PostConstruct
    private void postInit() {
        addStyleName("bookery-content");
        formLayout = new FormLayout();
        formLayout.addStyleName("light");
        formLayout.addComponents(generateFields());
        formLayout.addComponents(generateStatusFields());

        Button checkSolr = new Button("check", new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                checkSolr();
            }
        });
        checkSolr.addStyleName(ValoTheme.BUTTON_SMALL);
        checkSolr.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        formLayout.addComponent(checkSolr);

        Label titleLabel = new Label("General Settings");
        titleLabel.addStyleName(ValoTheme.LABEL_H2);
        addComponents(titleLabel, formLayout);
    }

    private AbstractTextField[] generateFields() {
        fields = new ArrayList<>();
        for(SettingKey key : SettingKey.values()) {
            AbstractTextField field = new TextField(key.getName());
            field.setColumns(25);
            field.setId(key.getKey());
            field.addBlurListener(this);
            fields.add(field);
        }
        AbstractTextField[] result = new AbstractTextField[fields.size()];
        return fields.toArray(result);
    }

    private Label[] generateStatusFields() {
        statusFields = new ArrayList<>();
        Label solrStatus = new Label("Offline " + FontAwesome.TIMES_CIRCLE.getHtml(), ContentMode.HTML);
        solrStatus.addStyleName("red-icon");
        solrStatus.setCaption("Status");
        statusFields.add(solrStatus);
        Label numberOfDocs = new Label("100");
        numberOfDocs.setCaption("Library");
        statusFields.add(numberOfDocs);
        Label sizeOnDisk = new Label("73MB");
        sizeOnDisk.setCaption("Size");
        statusFields.add(sizeOnDisk);

        Label[] result = new Label[statusFields.size()];
        return statusFields.toArray(result);
    }

    public void loadData() {
        for(AbstractTextField field : fields) {
            SettingKey key = SettingKey.getEnumByKey(field.getId());
            AppSetting setting = presenter.loadSetting(key);
            field.setValue(setting.getConfigurationValue());
        }
        checkSolr();
    }

    @Override
    public void blur(FieldEvents.BlurEvent event) {
        SettingKey key = SettingKey.getEnumByKey(event.getComponent().getId());
        for(AbstractTextField field : fields) {
            if(field.getId().equals(key.getKey())) {
                presenter.updateSetting(key, field.getValue());
                break;
            }
        }
    }

    private void checkSolr() {
        try {
            long numDocs = presenter.getSolrInfo();
            for(Label label : statusFields) {
                if(label.getCaption().equals("Status")) {
                    label.setValue("Online " + FontAwesome.CHECK_CIRCLE.getHtml());
                    label.removeStyleName("red-icon");
                    label.addStyleName("green-icon");
                } else if(label.getCaption().equals("Library")) {
                    label.setValue(numDocs + " Books");
                }
            }
        } catch(SolrServerException | IOException ex) {
            Logger.getLogger(ServerSettingsLayout.class.getName()).log(Level.SEVERE, null, ex);
            for(Label label : statusFields) {
                if(label.getCaption().equals("Status")) {
                    label.setValue(ex.getLocalizedMessage() + " " + FontAwesome.CHECK_CIRCLE.getHtml());
                    label.addStyleName("red-icon");
                    label.removeStyleName("green-icon");
                } else if(label.getCaption().equals("Num Docs")) {
                    label.setValue("NA");
                }
            }
        }
    }
}
