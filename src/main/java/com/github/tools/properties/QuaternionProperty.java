package com.github.tools.properties;

import java.beans.PropertyDescriptor;
import java.util.Locale;

import com.jme3.math.Quaternion;
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
public class QuaternionProperty extends JmeProperty<Quaternion> {

    private TextField textField;
    private Quaternion quaternion;

    public QuaternionProperty(Object bean, PropertyDescriptor pd) {
        super(bean, pd);
        quaternion = getValue();
    }
    
    @Override
    public Panel buildPanel() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.Last, FillMode.Even));
        container.addChild(new Label(getName() + ":"));
        
        Container values = container.addChild(new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even)));
        String initialValue = getAsText();
        textField = values.addChild(new TextField(initialValue));
        textField.setTextVAlignment(VAlignment.Center);

        Button button = values.addChild(new Button("Set"));
        button.addClickCommands(cmd -> {
            String newValue = textField.getText();
            setAsText(newValue);
        });

        return container;
    }

    protected String getAsText() {
        if (quaternion == null) {
            quaternion = new Quaternion();
        }
        
        float[] angles = quaternion.toAngles(new float[3]);
        float x = (float) Math.toDegrees(angles[0]);
        float y = (float) Math.toDegrees(angles[1]);
        float z = (float) Math.toDegrees(angles[2]);
        return String.format(Locale.US, "[%.2f, %.2f, %.2f]", x, y, z);
    }

    protected void setAsText(String text) {
        try {
            parseInto(text, quaternion);
            textField.setText(getAsText());
            setValue(quaternion);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseInto(String text, Quaternion storeResult) throws IllegalArgumentException {
        text = text.replace('[', ' ');
        text = text.replace(']', ' ').trim();
        String[] a = text.split("\\s*(,|\\s)\\s*");

        if (a.length == 1) {
            if (text.trim().equalsIgnoreCase("nan")) {
                storeResult.set(Float.NaN, Float.NaN, Float.NaN, Float.NaN);
                return;
            }
            float f = Float.parseFloat(text);
            f = (float) Math.toRadians(f);
            storeResult.fromAngles(f, f, f);
            return;
        }

        if (a.length == 3) {
            float[] floats = new float[3];
            for (int i = 0; i < a.length; i++) {
                floats[i] = (float) Math.toRadians(Float.parseFloat(a[i]));
            }
            storeResult.fromAngles(floats);
            return;
        }
        throw new IllegalArgumentException("String not correct");
    }

}
