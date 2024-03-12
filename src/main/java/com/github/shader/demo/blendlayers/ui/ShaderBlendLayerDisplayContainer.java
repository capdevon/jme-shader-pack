package com.github.shader.demo.blendlayers.ui;

import com.github.shader.demo.blendlayers.ShaderBlendLayer;
import com.github.tools.SpinnerFloatModel;
import com.github.tools.SpinnerIntegerModel;
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
public class ShaderBlendLayerDisplayContainer extends Container {

    public Button xRemoveButton;
    public Button addQueuedEffectToThisLayerButton;
    public RollupPanel propertyPanel;
    private ShaderBlendLayer shaderBlendLayer;

    public ShaderBlendLayer getShaderBlendLayer() {
        return shaderBlendLayer;
    }

    public RollupPanel getPropertyPanel() {
        return propertyPanel;
    }

    public ShaderBlendLayerDisplayContainer(ShaderBlendLayer shaderBlendLayer) {
        super();
        this.shaderBlendLayer = shaderBlendLayer;

        setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.First));

        propertyPanel = makePropertiesPanel(shaderBlendLayer.getName(), shaderBlendLayer);

        addChild(propertyPanel);

        xRemoveButton = new Button(" X ");
        addQueuedEffectToThisLayerButton = new Button("Add Effect");

        addChild(addQueuedEffectToThisLayerButton, 0, 1);
        addChild(xRemoveButton, 0, 2);
    }

    public RollupPanel makePropertiesPanel(String name, Object obj) {

        // probably pass this in as a parm so it can differ for effect vs blendLayer
        ConfigurationBuilder config = new ConfigurationBuilder();
        config.addConstraint("debugMode", new SpinnerIntegerModel(-1, 5, 1));
        config.addConstraint("scale", new SpinnerFloatModel(0.001f, 4f, 0.1f));

        config.addConstraint("hueScalar", new SpinnerFloatModel(-1.0f, 1.0f, 0.02f));
        config.addConstraint("saturationScalar", new SpinnerFloatModel(-1.0f, 1.0f, 0.02f));
        config.addConstraint("brightnessScalar", new SpinnerFloatModel(-1.0f, 1.0f, 0.02f));

        config.setIgnoredProperties(new String[] { "layerIndex", "finished" });

        ReflectedEditorBuilder builder = new ReflectedEditorBuilder(config);
        Panel panel = builder.buildPanel(obj);

        RollupPanel rollup = new RollupPanel(name, panel, "glass");
        rollup.setAlpha(0, false);
        rollup.setOpen(false);
        return rollup;
    }

}
