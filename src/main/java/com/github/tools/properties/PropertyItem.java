package com.github.tools.properties;

public interface PropertyItem<T> {

    T getValue();

    void setValue(T value);
}
