/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geoshear2013;

/**
 * This is basically just a re-hash of the observer/observable pattern
 * (simplified), but setting up the observable side as an interface instead of
 * a class. This lets me treat a class that extends something else as an
 * observable.
 * 
 * @author cwarren
 */
public interface Watchable {
    public void addWatcher(Watcher w);
    public void removeWatcher(Watcher w);
    public void removeAllWatchers();

    public void notifyWatchers();
    public void notifyWatchers(Object arg);
}
