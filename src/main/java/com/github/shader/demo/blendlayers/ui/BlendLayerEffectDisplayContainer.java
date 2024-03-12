package com.github.shader.demo.blendlayers.ui;

import com.github.shader.demo.blendlayers.effects.BlendLayerEffect;
import com.github.tools.SpinnerFloatModel;
import com.github.tools.editor.ReflectedEditorBuilder;
import com.github.tools.util.ConfigurationBuilder;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RollupPanel;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 *
 * @author ryan
 */
public class BlendLayerEffectDisplayContainer extends Container {

    private BlendLayerEffect blendLayerEffect;
    public Button xRemoveButton;
    public RollupPanel propertyPanel;

    public BlendLayerEffect getBlendLayerEffect() {
        return blendLayerEffect;
    }

    public RollupPanel getPropertyPanel() {
        return propertyPanel;
    }

    public BlendLayerEffectDisplayContainer(BlendLayerEffect blendLayerEffect) {
        super();
        this.blendLayerEffect = blendLayerEffect;

        setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.First));

        propertyPanel = makePropertiesPanel(blendLayerEffect.getName(), blendLayerEffect);
        addChild(propertyPanel);

        xRemoveButton = new Button(" X ");

        addChild(xRemoveButton, 0, 1);
    }

    public RollupPanel makePropertiesPanel(String name, Object obj) {

        // probably pass this in as a param so it can differ for effect vs blendLayer
        ConfigurationBuilder config = new ConfigurationBuilder();
        config.addConstraint("duration", new SpinnerFloatModel(0.0f, 60.0f, 0.1f));
        config.addConstraint("fadeInTime", new SpinnerFloatModel(0.0f, 60.0f, 0.1f));
        config.addConstraint("fadeOutTime", new SpinnerFloatModel(0.0f, 60.0f, 0.1f));

        ReflectedEditorBuilder builder = new ReflectedEditorBuilder(config);
        Panel panel = builder.buildPanel(obj);

        RollupPanel rollup = new RollupPanel(name, panel, "glass");
        rollup.setAlpha(0, false);
        rollup.setOpen(false);
        return rollup;
    }

}