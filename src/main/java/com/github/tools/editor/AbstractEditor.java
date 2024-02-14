package com.github.tools.editor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import com.github.tools.properties.ColorRGBAProperty;
import com.github.tools.properties.QuaternionProperty;
import com.github.tools.properties.Vector2Property;
import com.github.tools.properties.Vector3Property;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.Panel;

/**
 * 
 * @author capdevon
 */
public abstract class AbstractEditor<T> {

    protected Panel addVector2Property(String displayName, Object bean, String propertyName) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        pd.setDisplayName(displayName);
        return new Vector2Property(bean, pd).buildPanel();
    }
    
    protected Panel addVector3Property(String displayName, Object bean, String propertyName) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        pd.setDisplayName(displayName);
        return new Vector3Property(bean, pd).buildPanel();
    }
    
    protected Panel addQuaternionProperty(String displayName, Object bean, String propertyName) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        pd.setDisplayName(displayName);
        return new QuaternionProperty(bean, pd).buildPanel();
    }
    
    protected Panel addColorRGBAProperty(String displayName, Object bean, String propertyName) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        pd.setDisplayName(displayName);
        return new ColorRGBAProperty(bean, pd).buildPanel();
    }
    
    public abstract Container buildPanel(T bean);
}
