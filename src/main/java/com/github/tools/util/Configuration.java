package com.github.tools.util;

import java.util.Map;

import com.github.tools.SpinnerModel;

/**
 * @author capdevon
 */
public interface Configuration {
    
    public Map<String, SpinnerModel> getConstraints();
    
    public String[] getIgnoredProperties();

}
