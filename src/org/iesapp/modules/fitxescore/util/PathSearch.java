/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.iesapp.modules.fitxescore.util;

/**
 *
 * @author Josep
 */
public class PathSearch {
    private String directory;
    private boolean recursive=false;
    private boolean writable=true;
    private long lastmodified;

    public PathSearch() {
       
    }
     
    public PathSearch(String directory) {
        this.directory = directory;
    }
     
      public PathSearch(String directory, boolean recursive) {
        this.directory = directory;
        this.recursive = recursive;
    }

    /**
     * @return the recursive
     */
    public boolean isRecursive() {
        return recursive;
    }

    /**
     * @param recursive the recursive to set
     */
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    /**
     * @return the directory
     */
    public String getDirectory() {
        return directory;
    }

    /**
     * @param directory the directory to set
     */
    public void setDirectory(String directory) {
        this.directory = directory;
    }

    /**
     * @return the writable
     */
    public boolean isWritable() {
        return writable;
    }

    /**
     * @param writable the writable to set
     */
    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    /**
     * @return the lastmodified
     */
    public long getLastmodified() {
        return lastmodified;
    }

    /**
     * @param lastmodified the lastmodified to set
     */
    public void setLastmodified(long lastmodified) {
        this.lastmodified = lastmodified;
    }
}
