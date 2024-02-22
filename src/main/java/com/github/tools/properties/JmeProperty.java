package com.github.tools.properties;

import java.beans.PropertyDescriptor;

import com.simsilica.lemur.Panel;

/**
 * 
 * @author capdevon
 */
public abstract class JmeProperty<T> implements PropertyItem<T> {

    private Object bean;
    private PropertyDescriptor pd;

    public JmeProperty(Object bean, PropertyDescriptor pd) {
        this.bean = bean;
        this.pd = pd;
    }

    @Override
    public void setValue(T value) {
        setPropertyValue(pd, bean, value);
    }

    @Override
    public T getValue() {
        return getPropertyValue(pd, bean);
    }

    public Class<?> getType() {
        return pd.getPropertyType();
    }
    
    public String getName() {
        return pd.getDisplayName();
    }
    
    /**
     * Gets the method that should be used to read the property value and invokes it
     * on the specified object with the specified parameters.
     */
    protected <T> T getPropertyValue(PropertyDescriptor pd, Object bean) {
        try {
            return (T) pd.getReadMethod().invoke(bean);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error getting value", e);
        }
    }

    /**
     * Gets the method that should be used to write the property value and invokes
     * it on the specified object with the specified parameters.
     */
    protected void setPropertyValue(PropertyDescriptor pd, Object bean, Object value) {
        try {
            pd.getWriteMethod().invoke(bean, value);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Error setting value", e);
        }
    }
    
    public abstract Panel buildPanel();
    
}
