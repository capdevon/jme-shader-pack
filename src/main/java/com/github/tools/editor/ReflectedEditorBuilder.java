package com.github.tools.editor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import com.github.tools.properties.ColorRGBAProperty;
import com.github.tools.properties.QuaternionProperty;
import com.github.tools.properties.Vector2Property;
import com.github.tools.properties.Vector3Property;
import com.github.tools.properties.Vector4Property;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.props.PropertyPanel;

/**
 * 
 * @author capdevon
 */
public class ReflectedEditorBuilder extends AbstractEditor<Object> {
    
    private final String[] ignoredProperties;
    
    /**
     * @param ignoredProperties
     */
    public ReflectedEditorBuilder(String... ignoredProperties) {
        this.ignoredProperties = ignoredProperties;
    }

    @Override
    public Container buildPanel(Object bean) {

        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        PropertyPanel propertyPanel = container.addChild(new PropertyPanel("glass"));

        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(bean.getClass(), Object.class);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }

        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            
            if (ignoreProperty(pd)) {
                continue;
            }
            
            if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {

                System.out.printf("Inspect: %s, %s, %s, %s%n", 
                        pd.getPropertyType().getSimpleName(), pd.getName(), 
                        pd.getReadMethod().getName(), pd.getWriteMethod().getName());
                
                String propertyName = pd.getName();
                Class<?> fieldType = pd.getPropertyType();

                if (fieldType == Vector2f.class) {
                    container.addChild(new Vector2Property(bean, pd).buildPanel());

                } else if (fieldType == Vector3f.class) {
                    container.addChild(new Vector3Property(bean, pd).buildPanel());

                } else if (fieldType == Vector4f.class) {
                    container.addChild(new Vector4Property(bean, pd).buildPanel());

                } else if (fieldType == Quaternion.class) {
                    container.addChild(new QuaternionProperty(bean, pd).buildPanel());

                } else if (fieldType == ColorRGBA.class) {
                    container.addChild(new ColorRGBAProperty(bean, pd).buildPanel());

                } else if (fieldType == float.class || fieldType == Float.class) { //TODO: add constraints
                    propertyPanel.addFloatProperty(propertyName, bean, propertyName, -100, 100, 0.1f);

                } else if (fieldType == int.class || fieldType == Integer.class) { //TODO: add constraints
                    propertyPanel.addIntProperty(propertyName, bean, propertyName, -100, 100, 1);

                } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                    propertyPanel.addBooleanProperty(propertyName, bean, propertyName);

                } else if (fieldType.isEnum()) {
                    propertyPanel.addEnumProperty(propertyName, bean, propertyName);
                    
                } else {
                    System.err.println("Not supported yet: " + propertyName + ", Type: " + fieldType);
                }
            }
        }

        return container;
    }

    /**
     * @param pd
     * @return
     */
    private boolean ignoreProperty(PropertyDescriptor pd) {
        boolean ignoreProperty = false;
        for (String ignoredProperty : ignoredProperties) {
            if (pd.getName().equalsIgnoreCase(ignoredProperty)) {
                ignoreProperty = true;
                break;
            }
        }
        return ignoreProperty;
    }
    
}
