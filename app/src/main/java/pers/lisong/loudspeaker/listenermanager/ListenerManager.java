package pers.lisong.loudspeaker.listenermanager;

import java.util.HashSet;

import pers.lisong.loudspeaker.listenermanager.inf.IListenerManager;

public class ListenerManager<L> implements IListenerManager<L> {
    private final HashSet<L> listenerHashSet;

    public ListenerManager() {
        listenerHashSet = new HashSet<>();
    }

    @Override
    public boolean addListener(L listener) {
        return listenerHashSet.add(listener);
    }

    @Override
    public boolean removeListener(L listener) {
        return listenerHashSet.remove(listener);
    }

    @Override
    public HashSet<L> getAllListener() {
        return listenerHashSet;
    }
}