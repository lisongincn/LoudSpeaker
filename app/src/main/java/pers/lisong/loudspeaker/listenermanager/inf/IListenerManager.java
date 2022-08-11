package pers.lisong.loudspeaker.listenermanager.inf;

import java.util.HashSet;

public interface IListenerManager<L> {
    boolean addListener(L listener);
    boolean removeListener(L listener);
    HashSet<L> getAllListener();
}