/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl;

import javax.annotation.sql.DataSourceDefinition;

/**
 *
 * @author felix.husse
 */
@DataSourceDefinition(
        className="org.h2.jdbcx.JdbcDataSource",
        name="java:global/datasources/BookeryDS",
        databaseName="bookery-db",
        url = "jdbc:h2:mem:bookerydb;DB_CLOSE_DELAY=-1",
        user = "sa",
        password = "sa"
)
public class DBConfiguration {
    
}
