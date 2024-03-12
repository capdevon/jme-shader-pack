package com.github.shader.demo.states;

import com.github.shader.demo.utils.blendlayers.ShaderBlendLayer;
import com.github.tools.SpinnerIntegerModel;
import com.github.tools.editor.ReflectedEditorBuilder;
import com.github.tools.material.MatPropertyPanelBuilder;
import com.github.tools.util.ConfigurationBuilder;
import com.jme3.app.Application;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RollupPanel;
import com.simsilica.lemur.component.SpringGridLayout;

public class BlendEffectState extends MySimpleState {

    private Spatial model;
    
    public BlendEffectState(Spatial model) {
        this.model = model;
    }
    
    @Override
    protected void initialize(Application app) {
        super.initialize(app);
        
        setCharacterShader(model);
        initEffects();
        initMaterialEditor(model);
    }

    private void setCharacterShader(Spatial spatial) {
        spatial.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                Material oldMat = geom.getMaterial();
                Material newMat = new Material(assetManager, "MatDefs/PBRCharacters.j3md");

                for (MatParam matParam : oldMat.getParams()) {
                    if (matParam.getValue() != null) {
                        newMat.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
                    }
                }
                geom.setMaterial(newMat);
                newMat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
                geom.setQueueBucket(RenderQueue.Bucket.Translucent);
                //MikktspaceTangentGenerator.generate(geom);
            }
        });
    }
    
    private void initEffects() {

        String dirName = "Models/Cracked_Ice/DefaultMaterial_";
        ShaderBlendLayer ice = new ShaderBlendLayer("Freeze", 0, model);
        ice.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        ice.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        ice.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));

        dirName = "Models/Cracked_Stone/DefaultMaterial_";
        ShaderBlendLayer stone = new ShaderBlendLayer("Petrify", 1, model);
        stone.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        stone.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        stone.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));

        dirName = "Models/Shield_Armor/DefaultMaterial_";
        ShaderBlendLayer shield = new ShaderBlendLayer("Shield", 2, model);
        shield.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        shield.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        shield.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));
        shield.setEmissiveMap(assetManager.loadTexture(dirName + "emissive.png"));
        shield.setBlendAlpha(true);
        
        ShaderBlendLayer dissolve = new ShaderBlendLayer("Dissolve", 3, model);
        dissolve.setBaseColor(new ColorRGBA(1.0f, 0.0f, 1.0f, 0.0f));

        registerEffects(ice, stone, shield, dissolve);
    }
    
    private void registerEffects(ShaderBlendLayer... effects) {
        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));

        for (ShaderBlendLayer effect : effects) {
            ConfigurationBuilder config = new ConfigurationBuilder();
            //config.addConstraint("linearBlendVal", new SpinnerFloatModel(0f, 1f, 0.1f));
            config.addConstraint("debugMode", new SpinnerIntegerModel(-1, 5, 1));

            ReflectedEditorBuilder builder = new ReflectedEditorBuilder(config);
            Panel panel = builder.buildPanel(effect);

            RollupPanel rollup = new RollupPanel(effect.getName(), panel, "glass");
            rollup.setAlpha(0, false);
            rollup.setOpen(false);
            container.addChild(rollup);
        }

        container.setLocalTranslation(10f, settings.getHeight() - 40f, 1);
        guiNode.attachChild(container);
    }
    
    private void initMaterialEditor(Spatial spatial) {
        MatPropertyPanelBuilder builder = new MatPropertyPanelBuilder();
        builder.setIgnoreParamFilter(mp -> mp.getName().startsWith("BlendLayer"));
        
        Container container = builder.buildPanel(spatial);
        container.setLocalTranslation(settings.getWidth() * 0.75f, settings.getHeight() - 10f, 1);
        guiNode.attachChild(container);
    }
    
}
