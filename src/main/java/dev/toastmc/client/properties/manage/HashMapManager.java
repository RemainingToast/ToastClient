package dev.toastmc.client.properties.manage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class HashMapManager<K, V> implements IManager {

    protected Map<K, V> registry = new HashMap<>();

    public Map<K, V> getRegistry() {
        return registry;
    }

    public Collection<V> getValues() {
        return registry.values();
    }

    public boolean has(K check) {
        return registry.containsKey(check);
    }

    public void include(K key, V val){
        if(!has(key))
            registry.put(key, val);
    }

    public void exclude(K key){
        if(has(key))
            registry.remove(key);
    }

    public V pull(K key) {
        return registry.get(key);
    }

    public void load() {

    }

    public void unload() {

    }
}
