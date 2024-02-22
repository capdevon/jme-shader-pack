package com.github.tools.material;

import com.github.tools.properties.PropertyItem;
import com.jme3.material.Material;
import com.jme3.shader.VarType;

/**
 * 
 * @author capdevon
 */
public class MatParamProperty<T> implements PropertyItem<T> {

    private T value;
    private final String name;
    private final Material material;

    public MatParamProperty(String name, Material material) {
        this.name = name;
        this.material = material;
        this.value = getValue();
    }
    
    public String getName() {
        return name;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
        material.setParam(name, getVarType(), value);
    }

    @Override
    public T getValue() {
        return material.getParamValue(name);
    }

    private VarType getVarType() {
        return material.getParam(name).getVarType();
    }
    
}