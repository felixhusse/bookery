/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.view.watchlist;

import com.vaadin.cdi.UIScoped;
import de.fatalix.bookery.bl.WatchListService;
import javax.inject.Inject;
import org.apache.solr.client.solrj.SolrQuery;

/**
 *
 * @author felix.husse
 */
@UIScoped
public class WatchListPresenter {
    
    @Inject private WatchListService watchListService;
    
    public SolrQuery getWatchListQuery(String username) {
        return watchListService.getSolrQuery(watchListService.getUserWatchList(username));
    }
}
