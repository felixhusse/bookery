/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
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
