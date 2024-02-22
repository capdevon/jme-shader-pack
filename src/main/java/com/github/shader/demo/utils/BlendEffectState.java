package com.github.shader.demo.utils;

import com.github.tools.SpinnerFloatModel;
import com.github.tools.editor.ReflectedEditorBuilder;
import com.github.tools.material.MatPropertyPanelBuilder;
import com.github.tools.util.ConfigurationBuilder;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RollupPanel;
import com.simsilica.lemur.component.SpringGridLayout;

public class BlendEffectState extends BaseAppState implements ActionListener {

    private AssetManager assetManager;
    private InputManager inputManager;
    private BitmapText debugText;
    private Spatial model;
    private BlendLayerEffect ice;
    private BlendLayerEffect stone;
    private BlendLayerEffect shield;
    private int shaderDebugMode = -1;
    
    public BlendEffectState(Spatial model) {
        this.model = model;
    }
    
    @Override
    protected void initialize(Application app) {
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        
        debugText = makeLabelUI("Debug Mode: -1", ColorRGBA.White, getGuiNode());
        debugText.setLocalTranslation(10f, getSettings().getHeight() - 40f, 0);
        
        setCharacterShader(model);
        initEffects();
        initMaterialEditor(model);
        initKeys();
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }
    
    private AppSettings getSettings() {
        return getApplication().getContext().getSettings();
    }
    
    private Node getGuiNode() {
        return ((SimpleApplication) getApplication()).getGuiNode();
    }
    
    private void initMaterialEditor(Spatial model) {
        MatPropertyPanelBuilder builder = new MatPropertyPanelBuilder();
        builder.setIgnoreParamFilter(mp -> mp.getName().startsWith("BlendLayer"));
        
        Container container = builder.buildPanel(model);
        container.setLocalTranslation(getSettings().getWidth() * 0.7f, getSettings().getHeight() - 10f, 1);
        getGuiNode().attachChild(container);
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
                //MikktspaceTangentGenerator.generate(geom);
            }
        });
    }
    
    private void initEffects() {

        String dirName = "Models/Cracked_Ice/DefaultMaterial_";
        ice = new BlendLayerEffect("Freeze", 0, model);
        ice.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        ice.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        ice.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));

        dirName = "Models/Cracked_Stone/DefaultMaterial_";
        stone = new BlendLayerEffect("Petrify", 1, model);
        stone.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        stone.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        stone.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));

        dirName = "Models/Shield_Armor/DefaultMaterial_";
        shield = new BlendLayerEffect("Shield", 2, model);
        shield.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        shield.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        shield.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));
        shield.setEmissiveMap(assetManager.loadTexture(dirName + "emissive.png"));
        shield.setBlendAlpha(true);

        registerEffects(ice, stone, shield);
    }
    
    private void registerEffects(BlendLayerEffect... effects) {
        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));

        for (BlendLayerEffect effect : effects) {
            ConfigurationBuilder config = new ConfigurationBuilder();
            config.addConstraint("blendValue", new SpinnerFloatModel(0f, 1f, 0.1f));

            ReflectedEditorBuilder builder = new ReflectedEditorBuilder(config);
            Panel panel = builder.buildPanel(effect);

            RollupPanel rollup = new RollupPanel(effect.getName(), panel, "glass");
            rollup.setAlpha(0, false);
            rollup.setOpen(true);
            container.addChild(rollup);
        }

        container.setLocalTranslation(10f, getSettings().getHeight() - 70f, 1);
        getGuiNode().attachChild(container);
    }
    
    private BitmapText makeLabelUI(String text, ColorRGBA color, Node parent) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText bmp = new BitmapText(font);
        bmp.setName("MyLabel");
        bmp.setText(text);
        bmp.setColor(color);
        bmp.setSize(font.getCharSet().getRenderedSize());
        parent.attachChild(bmp);
        return bmp;
    }
    
    /**
     * Mapping a named action to a key input.
     */
    private void initKeys() {
        addMapping("ToggleShaderDebugMode", new KeyTrigger(KeyInput.KEY_O));
        addMapping("ToggleShaderDebugMode_Normals", new KeyTrigger(KeyInput.KEY_L));
    }
    
    private void addMapping(String mappingName, Trigger... triggers) {
        inputManager.addMapping(mappingName, triggers);
        inputManager.addListener(this, mappingName);
    }
    
    /**
     * Defining the named action that can be triggered by key inputs.
     */
    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (!keyPressed) {
            return;
        }

        if (name.equals("ToggleShaderDebugMode")) {
            shaderDebugMode++;
            if (shaderDebugMode > 5) {
                shaderDebugMode = -1;
            }
            ice.setParam("DebugValuesMode", VarType.Int, shaderDebugMode);
            debugText.setText("Shader Debug Mode: " + shaderDebugMode);

        } else if (name.equals("ToggleShaderDebugMode_Normals")) {
            if (shaderDebugMode == 1) {
                shaderDebugMode = -1;
            } else {
                shaderDebugMode = 1;
            }
            ice.setParam("DebugValuesMode", VarType.Int, shaderDebugMode);
            debugText.setText("Shader Debug Mode: " + shaderDebugMode);
        }
    }
    
}
