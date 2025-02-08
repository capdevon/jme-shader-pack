package com.github.tools.properties;

import java.beans.PropertyDescriptor;

import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.ElementId;

/**
 * 
 * @author capdevon
 */
public class ReadOnlyProperty extends JmeProperty<Object> {

    public ReadOnlyProperty(Object bean, PropertyDescriptor pd) {
        super(bean, pd);
    }

    @Override
    public Panel buildPanel() {
        String displayName = getName();
        String roValue = getValue().toString();
        
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));
        container.addChild(new Label(displayName, new ElementId("label")));
        container.addChild(new Label(roValue, new ElementId("label-ro")));
        return container;
    }
    
    @Override
    public void setValue(Object value) {
        throw new UnsupportedOperationException();
    }

}
