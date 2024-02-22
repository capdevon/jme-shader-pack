package com.github.tools.properties;

public interface ReflectedItem<T> {

    T getValue();

    void setValue(T value);
}
