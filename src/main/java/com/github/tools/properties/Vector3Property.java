package com.github.tools.properties;

import java.beans.PropertyDescriptor;
import java.util.Locale;

import com.jme3.math.Vector3f;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.TextField;
import com.simsilica.lemur.VAlignment;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 * 
 * @author capdevon
 */
public class Vector3Property extends JmeProperty<Vector3f> {
    
    private TextField stringProperty;
    private Vector3f vector;
    
    public Vector3Property(Object bean, PropertyDescriptor pd) {
        super(bean, pd);
        vector = getValue();
    }
    
    @Override
    public Panel buildPanel() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even));
        container.addChild(new Label(getName() + ":"));
        
        Container values = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        String initialValue = getAsText();
        stringProperty = values.addChild(new TextField(initialValue));
        stringProperty.setTextVAlignment(VAlignment.Center);

        Button button = values.addChild(new Button("Set"));
        button.addClickCommands(cmd -> {
            String newValue = stringProperty.getText();
            setAsText(newValue);
        });

        return container;
    }
    
    protected String getAsText() {
        if (vector == null) {
            vector = new Vector3f();
        }
        return String.format(Locale.US, "[%.2f, %.2f, %.2f]", vector.x, vector.y, vector.z);
    }

    protected void setAsText(String text) {
        try {
            parseInto(text, vector);
            stringProperty.setText(getAsText());
            setValue(vector);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseInto(String text, Vector3f storeResult) throws IllegalArgumentException {
        text = text.replace('[', ' ');
        text = text.replace(']', ' ').trim();
        String[] a = text.split("\\s*(,|\\s)\\s*");

        if (a.length == 1) {
            if (text.trim().equalsIgnoreCase("nan")) {
                storeResult.set(Vector3f.NAN);
                return;
            }
            float f = Float.parseFloat(text);
            storeResult.set(f, f, f);
            return;
        }

        if (a.length == 3) {
            float x = Float.parseFloat(a[0]);
            float y = Float.parseFloat(a[1]);
            float z = Float.parseFloat(a[2]);
            storeResult.set(x, y, z);
            return;
        }
        throw new IllegalArgumentException("String not correct");
    }

}
