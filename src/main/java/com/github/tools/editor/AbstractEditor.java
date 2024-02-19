package com.github.tools.editor;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

import com.github.tools.SpinnerModel;
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
    
    protected final Map<String, SpinnerModel> constraints = new HashMap<>();
    
    /**
     * Add a new constraint definition.
     * @param paramName
     * @param model
     */
    public final void addConstraint(String paramName, SpinnerModel<?> model) {
        constraints.put(paramName, model);
    }

    /**
     * Copies all of the constraints from the specified map to this map.
     * @param map
     */
    public final void addConstraints(Map<String, SpinnerModel> map) {
        constraints.putAll(map);
    }
    
    public final void setConstraints(Map<String, SpinnerModel> map) {
        constraints.clear();
        constraints.putAll(map);
    }

    public final Panel addVector2Property(String displayName, Object bean, String propertyName) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        pd.setDisplayName(displayName);
        return new Vector2Property(bean, pd).buildPanel();
    }
    
    public final Panel addVector3Property(String displayName, Object bean, String propertyName) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        pd.setDisplayName(displayName);
        return new Vector3Property(bean, pd).buildPanel();
    }
    
    public final Panel addVector4Property(String displayName, Object bean, String propertyName) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        pd.setDisplayName(displayName);
        return new Vector4Property(bean, pd).buildPanel();
    }
    
    public final Panel addQuaternionProperty(String displayName, Object bean, String propertyName) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        pd.setDisplayName(displayName);
        return new QuaternionProperty(bean, pd).buildPanel();
    }
    
    public final Panel addColorRGBAProperty(String displayName, Object bean, String propertyName) throws IntrospectionException {
        PropertyDescriptor pd = new PropertyDescriptor(propertyName, bean.getClass());
        pd.setDisplayName(displayName);
        return new ColorRGBAProperty(bean, pd).buildPanel();
    }
    
    public abstract Container buildPanel(T bean);
}
