package de.skuzzle.polly.sdk.httpv2.html;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import de.skuzzle.polly.http.api.HttpEvent;
import de.skuzzle.polly.sdk.MyPolly;
import de.skuzzle.polly.sdk.User;
import de.skuzzle.polly.sdk.httpv2.SuccessResult;

public abstract class AbstractHTMLTableModel<T> implements HTMLTableModel<T> {



    @Override
    public Set<String> getRequiredPermission() {
        return Collections.emptySet();
    }



    @Override
    public boolean isFilterable(int column) {
        return false;
    }



    @Override
    public boolean isSortable(int column) {
        return false;
    }



    @Override
    public boolean isEditable(int column) {
        return false;
    }

    
    
    @Override
    public boolean isFilterOnly() {
        return false;
    }

    

    @Override
    public SuccessResult setCellValue(int column, T element, String value, 
            User executor, MyPolly myPolly) {
        return new SuccessResult(false, "Not implemented");
    }



    @Override
    public Class<?> getColumnClass(int column) {
        return Object.class;
    }



    @Override
    public Map<String, String> getRequestParameters(HttpEvent e) {
        return Collections.emptyMap();
    }
}
