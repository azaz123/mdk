package org.mdk.battle.mysqlagent.respool;

import org.mdk.net.nio.Session;
import org.mdk.net.nio.AbstractSession;
import org.mdk.battle.mysqlagent.beans.MysqlMetaBeans;

public interface pool<K,V> {
    public V get(K key);
    public void put(K key,V value);
}
