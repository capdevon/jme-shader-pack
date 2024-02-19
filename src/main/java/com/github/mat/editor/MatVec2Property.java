package com.github.mat.editor;

import java.util.Locale;

import com.jme3.material.Material;
import com.jme3.math.Vector2f;
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
public class MatVec2Property extends MatParamProperty<Vector2f> implements MatPropertyBuilder {

    private TextField textField;
    private Vector2f tmpVec;

    public MatVec2Property(String name, Material material) {
        super(name, material);
        tmpVec = getValue();
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
        if (tmpVec == null) {
            tmpVec = new Vector2f();
        }
        return String.format(Locale.US, "[%.2f, %.2f]", tmpVec.x, tmpVec.y);
    }

    protected void setAsText(String text) {
        try {
            parseInto(text, tmpVec);
            textField.setText(getAsText());
            setValue(tmpVec);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseInto(String text, Vector2f storeResult) throws IllegalArgumentException {
        text = text.replace('[', ' ');
        text = text.replace(']', ' ').trim();
        String[] a = text.split("\\s*(,|\\s)\\s*");

        if (a.length == 1) {
            if (text.trim().equalsIgnoreCase("nan")) {
                storeResult.set(Float.NaN, Float.NaN);
                return;
            }
            float f = Float.parseFloat(text);
            storeResult.set(f, f);
            return;
        }

        if (a.length == 2) {
            float x = Float.parseFloat(a[0]);
            float y = Float.parseFloat(a[1]);
            storeResult.set(x, y);
            return;
        }
        throw new IllegalArgumentException("String not correct");
    }

}