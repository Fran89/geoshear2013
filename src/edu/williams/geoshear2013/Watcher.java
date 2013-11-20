/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.williams.geoshear2013;

/**
 *
 * @author cwarren
 */
public interface Watcher {
    public void reactTo(Watchable w, Object arg);
    public void setWatched(Watchable w);
    public void clearWatched();
    public void clearWatched(Watchable w);
    public Watchable getWatched();    
}
