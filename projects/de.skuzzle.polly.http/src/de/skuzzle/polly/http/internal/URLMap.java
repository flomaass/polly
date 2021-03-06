package de.skuzzle.polly.http.internal;

import java.util.HashMap;


class URLMap<T> extends HashMap<String, T> {

    private static final long serialVersionUID = 1L;

    
    
    public String findKey(String path) {
        String key = (String) path;
        T result = null;
        int i = key.length();
        
        // find longest matching path prefix
        do {
            key = key.substring(0, i);
            result = super.get(key);
            i = key.lastIndexOf('/');
        } while (result == null && i != -1);
        return key;
    }
    
    
    
    @Override
    public T get(Object path) {
        assert path instanceof String;
        return super.get(path);
    }
}
