/*
 * Copyright (c) 2016 Felix Husse under MIT License
 * see LICENSE file
 */
package de.fatalix.bookery;


/**
 *
 * @author Fatalix
 */
public class SolrSearchUtil {
    
    public static final String DEFAULT_FIELDS = "id,author,title,isbn,publisher,description,language,releaseDate,likes,downloadcount,uploader,viewed,shared,cover,thumbnail,thumbnailgenerated,likedby";
    
    public static final String generateSearchString(String searchword) {
        String queryString = "*:*";
        if (searchword != null && !searchword.isEmpty()) {
            queryString = "author:*" + searchword + "* OR title:*" + searchword + "*";
        }
        
        return queryString;
    }
    
    public static final String addNewBooksSearchString(String viewer, String queryString) {
        String newQueryString = queryString + " AND -viewed:" + viewer;
        
        return newQueryString;
    }
    
    
}
