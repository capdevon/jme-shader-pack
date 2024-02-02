package com.github.mat.editor;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RollupPanel;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.props.PropertyPanel;

/**
 * 
 * @author capdevon
 */
public class MatColorProperty extends MatParamProperty<ColorRGBA> implements MatPropertyBuilder {

    private float red, green, blue, alpha;
    private ColorRGBA color;

    public MatColorProperty(String name, Material material) {
        super(name, material);
        this.color = getValue();
        this.red = color.r;
        this.green = color.g;
        this.blue = color.b;
        this.alpha = color.a;
    }

    @Override
    public Panel buildPanel() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));
        // container.addChild(new Label(name));

        PropertyPanel propertyPanel = container.addChild(new PropertyPanel("glass"));
        propertyPanel.addFloatProperty("R", this, "red", 0, 1, 0.01f);
        propertyPanel.addFloatProperty("G", this, "green", 0, 1, 0.01f);
        propertyPanel.addFloatProperty("B", this, "blue", 0, 1, 0.01f);
        propertyPanel.addFloatProperty("A", this, "alpha", 0, 1, 0.01f);

//      Button update = container.addChild(new Button("Set"));
//      update.addClickCommands(cmd -> {
//          material.setColor(name, color);
//      });

        RollupPanel rollup = new RollupPanel(this.getName(), propertyPanel, "glass");
        // rollup.setAlpha(0, false);
        rollup.setOpen(false);
        container.addChild(rollup);

        return rollup;
    }

    public float getRed() {
        return red;
    }

    public void setRed(float red) {
        this.red = red;
        updateColor();
    }

    public float getGreen() {
        return green;
    }

    public void setGreen(float green) {
        this.green = green;
        updateColor();
    }

    public float getBlue() {
        return blue;
    }

    public void setBlue(float blue) {
        this.blue = blue;
        updateColor();
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
        updateColor();
    }

    private void updateColor() {
        color.set(red, green, blue, alpha);
        setValue(color);
    }

}