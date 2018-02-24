package org.mdk.battle.mysqlagent.respool;
import java.io.IOException;
import java.lang.ThreadLocal;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.HashSet;

import org.mdk.net.nio.ReactorContext;

public class Dspool<K,V> implements pool<K,V> {
	private final int nThreadhold;
	private ThreadLocal<LRUPooledCache<K,V>> Tcache = new ThreadLocal<LRUPooledCache<K,V>>();
	private ConcurrentHashMap<K,V> gPool = new ConcurrentHashMap<K,V>();
	//private ConcurrentSkipListSet<K> judgeSet = new ConcurrentSkipListSet<K>();
	//private HashSet<K> judgeSet = new HashSet<K>();
	private CopyOnWriteArraySet<K> judgeSet = new CopyOnWriteArraySet<K>();
	
	public Dspool(int nTh){
		nThreadhold = nTh;
	}
	
	public V get(K key){
		V retval = null;
		LRUPooledCache<K,V> ttmp = Tcache.get();
		if(ttmp == null){
			Tcache.set(new LRUPooledCache<K,V>());
			ttmp = Tcache.get();
		}
		if(ttmp.containsKey(key)){
			retval = ttmp.get(key);
		}else{
			retval = getFromGlobalpool(key);
			ttmp.put(key, retval);
		}
		return retval;
	}
	
    public void put(K key,V value){
    	LRUPooledCache<K,V> ttmp = Tcache.get();
    	if(!ttmp.containsKey(key)){
    		ttmp.put(key, value);
		}
    }
    
    public void putToGlobalpool(K key,V value){
    	if(!judgeSet.contains(key)){
    		judgeSet.add(key);
    		gPool.put(key, value);
    	}
    }
    
    public V getFromGlobalpool(K key){
    	V retval = null;
    	if(judgeSet.contains(key)){
    		judgeSet.remove(key);
    		retval = gPool.get(key);
    		put(key,retval);
    	}
    	return retval;
    }
    
    private void putOuterSet(Object key){
    	judgeSet.add((K) key);
    }
   
    
    class LRUPooledCache<K,V> extends LinkedHashMap<K, V> {
    	@Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
    		if(size() > nThreadhold){
    			Object tmp = (Object)eldest.getKey();
    			putOuterSet(tmp);
    			return true;
    		}
            return false;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<K, V> entry : entrySet()) {
                sb.append(String.format("%s:%s ", entry.getKey(), entry.getValue()));
            }
            return sb.toString();
        }
    }
}
