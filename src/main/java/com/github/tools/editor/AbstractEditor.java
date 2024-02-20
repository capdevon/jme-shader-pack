package com.github.tools.editor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.github.tools.properties.ColorRGBAProperty;
import com.github.tools.properties.QuaternionProperty;
import com.github.tools.properties.Vector2Property;
import com.github.tools.properties.Vector3Property;
import com.github.tools.properties.Vector4Property;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Panel;

/**
 * 
 * @author capdevon
 */
public abstract class AbstractEditor<T> {
    
    public final Panel addVector2Property(String displayName, Object bean, String propertyName) {
        PropertyDescriptor pd = propertyDesc(displayName, bean, propertyName);
        return new Vector2Property(bean, pd).buildPanel();
    }

    public final Panel addVector3Property(String displayName, Object bean, String propertyName) {
        PropertyDescriptor pd = propertyDesc(displayName, bean, propertyName);
        return new Vector3Property(bean, pd).buildPanel();
    }
    
    public final Panel addVector4Property(String displayName, Object bean, String propertyName) {
        PropertyDescriptor pd = propertyDesc(displayName, bean, propertyName);
        return new Vector4Property(bean, pd).buildPanel();
    }
    
    public final Panel addQuaternionProperty(String displayName, Object bean, String propertyName) {
        PropertyDescriptor pd = propertyDesc(displayName, bean, propertyName);
        return new QuaternionProperty(bean, pd).buildPanel();
    }
    
    public final Panel addColorRGBAProperty(String displayName, Object bean, String propertyName) {
        PropertyDescriptor pd = propertyDesc(displayName, bean, propertyName);
        return new ColorRGBAProperty(bean, pd).buildPanel();
    }
    
    private PropertyDescriptor propertyDesc(String displayName, Object bean, String propertyName) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
            pd.setDisplayName(displayName);
            return pd;
            
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
    
    public abstract Container buildPanel(T bean);
}
