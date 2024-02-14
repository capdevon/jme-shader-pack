package com.github.tools.properties;

import java.awt.Color;
import java.beans.PropertyDescriptor;

import javax.swing.JColorChooser;
import javax.swing.SwingUtilities;

import com.github.tools.ColorUtils;
import com.jme3.app.Application;
import com.jme3.math.ColorRGBA;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 * 
 * @author capdevon
 */
public class ColorRGBAProperty extends JmeProperty<ColorRGBA> {

    public ColorRGBAProperty(Object bean, PropertyDescriptor pd) {
        super(bean, pd);
    }

    @Override
    public Panel buildPanel() {
        Container container = new Container(new SpringGridLayout(Axis.X, Axis.Y, FillMode.First, FillMode.Even));
        container.addChild(new Label(getName() + ":"));

        // Color selection button
        Button button = container.addChild(new Button("Change..."));
        IconComponent icon = new IconComponent("Icons/color-plus.png");
        icon.setHAlignment(HAlignment.Right);
        button.setIcon(icon);
        
        Color initialColor = ColorUtils.toColorAWT(getValue());
        button.addClickCommands(cmd -> {
            
            // invoke on AWT Thread
            SwingUtilities.invokeLater(() -> {
                Color selectedColor = JColorChooser.showDialog(null, "Choose Color...", initialColor, true);
                if (selectedColor != null) {
                    ColorRGBA jmeColor = ColorUtils.toColorRGBA(selectedColor);
                    
                    // set value from the JME Thread
                    getApplication().enqueue(() -> {
                        setValue(jmeColor);
                    });
                }
            });
        });

        return container;
    }
    
    /**
     * Trick to access the Application through the GuiGlobals 
     * which has no getter method for direct access.
     * 
     * @return
     */
    private Application getApplication() {
        return GuiGlobals.getInstance().getPopupState().getApplication();
    }
}
