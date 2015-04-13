/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fatalix.bookery.bl.solr;

/**
 *
 * @author felix.husse
 */
public class LukeIndex {
    private long numDocs;
    private long maxDocs;
    private long deletedDocs;
    private long indexHeapUsageBytes;
    private long version;
    private boolean current;

    public long getNumDocs() {
        return numDocs;
    }

    public long getMaxDocs() {
        return maxDocs;
    }

    public long getDeletedDocs() {
        return deletedDocs;
    }

    public long getIndexHeapUsageBytes() {
        return indexHeapUsageBytes;
    }

    public long getVersion() {
        return version;
    }

    public boolean isCurrent() {
        return current;
    }
}
