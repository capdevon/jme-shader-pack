package com.github.mat.editor;

import com.jme3.material.Material;
import com.jme3.math.Vector2f;
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
public class MatVec2Property extends MatParamProperty<Vector2f> implements MatPropertyBuilder {

    private float x;
    private float y;
    private SpinnerFloatModel range;
    private Vector2f tmpVec = new Vector2f();

    public MatVec2Property(String name, Material material, SpinnerFloatModel model) {
        super(name, material);
        this.range = model;
        tmpVec = getValue();
    }

    @Override
    public Panel buildPanel() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.ForcedEven, FillMode.Even));
        // container.addChild(new Label(name));

        PropertyPanel propertyPanel = container.addChild(new PropertyPanel("glass"));
        propertyPanel.addFloatProperty("x", this, "x", range.getMinValue(), range.getMaxValue(), range.getStep());
        propertyPanel.addFloatProperty("y", this, "y", range.getMinValue(), range.getMaxValue(), range.getStep());

//        Button update = container.addChild(new Button("Set"));
//        update.addClickCommands(cmd -> {
//            material.setColor(name, color);
//        });

        RollupPanel rollup = new RollupPanel(this.getName(), propertyPanel, "glass");
        // rollup.setAlpha(0, false);
        rollup.setOpen(false);
        container.addChild(rollup);

        return rollup;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
        updateVector();
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
        updateVector();
    }
    
    private void updateVector() {
        tmpVec.x = x;
        tmpVec.y = y;
        setValue(tmpVec);
    }

}