package com.github.tools.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.github.tools.SpinnerFloatModel;
import com.github.tools.SpinnerIntegerModel;
import com.github.tools.SpinnerModel;

/**
 * 
 * @author capdevon
 */
public class ConfigurationBuilder implements Configuration {

    public static final SpinnerFloatModel DEFAULT_SPINNER_FLOAT = new SpinnerFloatModel(0f, 1f, 0.1f);
    public static final SpinnerIntegerModel DEFAULT_SPINNER_INT = new SpinnerIntegerModel(0, 10, 1);

    private String[] ignoredProperties;
    private Map<String, SpinnerModel> constraints;
    
    /**
     * Creates a new instance of {@code ConfigurationBuilder}.
     */
    public ConfigurationBuilder() {
        constraints = new HashMap<>();
    }

    @Override
    public Map<String, SpinnerModel> getConstraints() {
        return constraints;
    }

    public ConfigurationBuilder setConstraints(Map<String, SpinnerModel> map) {
        constraints = new HashMap<>(map);
        return this;
    }

    public ConfigurationBuilder addConstraint(String paramName, SpinnerModel model) {
        constraints.put(paramName, model);
        return this;
    }

    public ConfigurationBuilder addConstraints(Map<String, SpinnerModel> map) {
        constraints.putAll(map);
        return this;
    }

    @Override
    public String[] getIgnoredProperties() {
        return ignoredProperties;
    }

    public ConfigurationBuilder setIgnoredProperties(String[] ignoredProperties) {
        this.ignoredProperties = ignoredProperties;
        return this;
    }
    
    public ConfigurationBuilder addIgnoredProperties(String... ignoredProperties) {
        this.ignoredProperties = (this.ignoredProperties == null) ? ignoredProperties
                : Stream.concat(Arrays.stream(this.ignoredProperties), Arrays.stream(ignoredProperties))
                    .distinct().toArray(String[]::new);
        return this;
    }
    
}
