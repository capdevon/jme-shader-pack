package com.github.tools.editor;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tools.SpinnerModel;
import com.github.tools.properties.ColorRGBAProperty;
import com.github.tools.properties.QuaternionProperty;
import com.github.tools.properties.Vector2Property;
import com.github.tools.properties.Vector3Property;
import com.github.tools.properties.Vector4Property;
import com.github.tools.util.Configuration;
import com.github.tools.util.ConfigurationBuilder;
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
    
    private static final Logger log = LoggerFactory.getLogger(ReflectedEditorBuilder.class);
    
    private final Configuration config;
    
    /**
     * Creates a new instance of {@code ReflectedEditorBuilder}.
     */
    public ReflectedEditorBuilder() {
        config = new ConfigurationBuilder();
    }
    
    public ReflectedEditorBuilder(Configuration config) {
        this.config = config;
    }
    
    public Configuration getConfiguration() {
        return config;
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

                log.debug("Inspect: {}, {}, {}, {}", 
                        pd.getPropertyType().getSimpleName(), pd.getName(), 
                        pd.getReadMethod().getName(), pd.getWriteMethod().getName());
                
                String name = pd.getName();
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

                } else if (fieldType == float.class || fieldType == Float.class) {
                    SpinnerModel<Float> range = getOrDefaultSpinner(name, ConfigurationBuilder.DEFAULT_SPINNER_FLOAT);
                    propertyPanel.addFloatProperty(name, bean, name, range.getMinValue(), range.getMaxValue(), range.getStep());

                } else if (fieldType == int.class || fieldType == Integer.class) {
                    SpinnerModel<Integer> range = getOrDefaultSpinner(name, ConfigurationBuilder.DEFAULT_SPINNER_INT);
                    propertyPanel.addIntProperty(name, bean, name, range.getMinValue(), range.getMaxValue(), range.getStep());

                } else if (fieldType == boolean.class || fieldType == Boolean.class) {
                    propertyPanel.addBooleanProperty(name, bean, name);

                } else if (fieldType.isEnum()) {
                    propertyPanel.addEnumProperty(name, bean, name);
                    
                } else {
                    log.warn("Not supported yet: {}, Type: {}", name, fieldType);
                }
            }
        }

        return container;
    }
    
    @SuppressWarnings("unchecked")
    private <T extends Number> SpinnerModel<T> getOrDefaultSpinner(String name, SpinnerModel<T> defaultSpinner) {
        return config.getConstraints().getOrDefault(name, defaultSpinner);
    }

    /**
     * @param pd
     * @return
     */
    private boolean ignoreProperty(PropertyDescriptor pd) {
        boolean ignoreProperty = false;
        if (config.getIgnoredProperties() != null) {
            for (String ignoredProperty : config.getIgnoredProperties()) {
                if (pd.getName().equalsIgnoreCase(ignoredProperty)) {
                    ignoreProperty = true;
                    break;
                }
            }
        }
        return ignoreProperty;
    }
    
}
