package com.github.shader.demo;


import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.Queue;



import com.github.shader.demo.states.BlendEffectState;
import com.github.shader.demo.states.DefaultSceneState;
import com.github.shader.demo.utils.GameObject;
import com.github.shader.demo.utils.blendlayers.BlendGroup;

import com.github.shader.demo.utils.blendlayers.ShaderBlendLayer;
import com.github.shader.demo.utils.blendlayers.effects.BlendLayerEffect;
import com.github.shader.demo.utils.blendlayers.ShaderBlendLayerGenerator;
import com.github.shader.demo.utils.blendlayers.ShaderBlendLayerManager;
import com.github.shader.demo.utils.blendlayers.effects.HSVCycleEffect;
import com.github.shader.demo.utils.blendlayers.effects.HeightScanEffect;
import com.github.shader.demo.utils.blendlayers.effects.TimedBlendEffect;
import com.github.shader.demo.utils.blendlayers.interfaceelements.BlendLayerEffectDisplayContainer;
import com.github.shader.demo.utils.blendlayers.interfaceelements.ShaderBlendLayerDisplayContainer;
import com.github.tools.SpinnerIntegerModel;
import com.github.tools.editor.ReflectedEditorBuilder;
import com.github.tools.util.ConfigurationBuilder;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.app.ChaseCameraAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.custom.ArmatureDebugAppState;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Insets3f;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RollupPanel;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.style.BaseStyles;
import java.util.ArrayList;

/**
 * 
 * @author capdevon
 */
public class Test_CharacterEffects3 extends SimpleApplication {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Test_CharacterEffects3 app = new Test_CharacterEffects3();
        AppSettings settings = new AppSettings(true);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int) (screenSize.getWidth() * .95f);
        int height = (int) (screenSize.getHeight() * .95f);

        settings.put("Width", width);
        settings.put("Height", height);

        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();
    }

    private static final String MODEL_ASSET_PATH = "Models/Erika/Erika.j3o";
//    private static final String MODEL_ASSET_PATH = "Models/YBot/YBot.j3o";
    

    private BitmapText animUI;
    private Spatial model;
    private AnimComposer animComposer;
    private SkinningControl skinningControl;
    private ArmatureDebugAppState armatureDebugState;
    private final Queue<String> animsQueue = new LinkedList<>();
  
    private BlendGroup blendGroup;
    
    private Container appliedLayersContainer;

    
    
    @Override
    public void simpleInitApp() {
        armatureDebugState = new ArmatureDebugAppState();
        stateManager.attach(armatureDebugState);

        animUI = makeTextUI("CurrentAction:", ColorRGBA.Blue);
        animUI.setLocalTranslation(10f, settings.getHeight() - 10f, 0);

        shaderBlendLayerManager = new ShaderBlendLayerManager();
        stateManager.attach(shaderBlendLayerManager);
        
        initLemur();
        configureCamera();
        setupCamera();
        loadModel();
        initKeys();

        initReservedLayers();
        stateManager.attach(new DefaultSceneState());
        // stateManager.attach(new BlendEffectState(model));
        // stateManager.attach(new DebugGridState());
        // stateManager.attach(new DetailedProfilerState());
    }
    
    
    private void initReservedLayers(){
        
        ShaderBlendLayer baseHsvScaleLayer = new ShaderBlendLayer("Base_HSV");
        baseHsvScaleLayer.setMultiplicative(true);
        baseHsvScaleLayer.setDissolveNoiseVal0(new Vector2f(0.02f, 1.0f));
        
        ShaderBlendLayer ghostStateLayer = new ShaderBlendLayer("Ghost");
        ghostStateLayer.setMultiplicative(true); 
        ghostStateLayer.setSaturationScalar(-0.23f);
        ghostStateLayer.setBaseColor(new ColorRGBA(0.4f, 0.8f, 0.85f, 0.75f));
        ghostStateLayer.setDissolveNoiseVal0(new Vector2f(0.12f, 1.0f));
        ghostStateLayer.setDissolveNoiseVal1(new Vector2f(0.06f, 0.6f));
        ghostStateLayer.setDissolveNoiseVal2(new Vector2f(0.03f, 0.3f));
        ghostStateLayer.setEdgeFadeThickness(0.07f);
        
        ShaderBlendLayerDisplayContainer layerDisplayContainer0 = this.makeShaderBlendLayerDisplayContainer(baseHsvScaleLayer);
        ShaderBlendLayerDisplayContainer layerDisplayContainer1 = this.makeShaderBlendLayerDisplayContainer(ghostStateLayer);
        
        activeBlendLayersContainer.addChild(layerDisplayContainer0, 0, 1);
        activeBlendLayersContainer.addChild(layerDisplayContainer1, 1 ,1);
        
        this.shaderBlendLayerManager.registerReservedLayerToModel(baseHsvScaleLayer, blendGroup, 0);
        this.shaderBlendLayerManager.registerReservedLayerToModel(ghostStateLayer, blendGroup, 1);
    }
    
    private void setCharacterShader(Spatial spatial) {
        
        ArrayList<Material> characterMatsToBlend = new ArrayList<>();
        
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
                
                characterMatsToBlend.add(newMat);
            }
        });
        
    //    spatial.move(3,3,0);
        
        blendGroup = new BlendGroup(spatial, characterMatsToBlend);
        blendGroup.setReservedLayerCount(3);
    }
    

    
    private Container activeBlendLayersContainer;
    
    
    private ArrayList<ShaderBlendLayer> registeredBlendLayers = new ArrayList();
    
    public ShaderBlendLayerManager shaderBlendLayerManager;
    
    private Container queuedLayerStagingContainer;
    private Container queuedEffectStagingContainer;
    
    private ShaderBlendLayerDisplayContainer queuedBlendLayerDisplayContainer;
    private BlendLayerEffectDisplayContainer queuedEffectDisplayContainer;
    
    private ShaderBlendLayer queuedShaderBlendLayer;
    private BlendLayerEffect queuedBlendLayerEffect;
    
    private RollupPanel defaultLayerSelectionRollup, defaultEffectSelectionRollup;
    
    private Button addDynamicLayerButton;
    
    private Container dynamicLayersContainer;
    
    
    private final int reservedLayersCount = 2;
    
    private void initLemur() {
        // initialize lemur
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        
        activeBlendLayersContainer = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        activeBlendLayersContainer.setLocalTranslation(10f, settings.getHeight() - 40f, 1);
        
        for(int l = 0; l < reservedLayersCount; l++){
            Label Label = null;
            Label = new Label("RL " + l + ":");
            activeBlendLayersContainer.addChild(Label, l, 0);
            
        }
        
        dynamicLayersContainer = new Container();
        
        activeBlendLayersContainer.addChild(dynamicLayersContainer,reservedLayersCount + 1, 1);
        
        
        addDynamicLayerButton = new Button("Add Queued Layer");
        dynamicLayersContainer.addChild(addDynamicLayerButton, 0, 1);
        
        addDynamicLayerButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {          
            if(queuedShaderBlendLayer != null){
                this.shaderBlendLayerManager.registerTemporaryLayerToModel(queuedShaderBlendLayer, blendGroup);
                dynamicLayersContainer.addChild(this.queuedBlendLayerDisplayContainer, blendGroup.getCurrentDynamicLayerCount() + 1, 1);
                
                
                //re-display the BlendLayerDeisplayContainers to match new blend order
                for(int i = 0; i <  blendGroup.getRegisteredLayers().length; i++){
                    
                    
                    
                }
               
                
            }
        });
        
        guiNode.attachChild(activeBlendLayersContainer);
        
        Container layerAndEffectsQueueContainer = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        
        layerAndEffectsQueueContainer.setPreferredSize(new Vector3f(settings.getWidth() * 0.73f, settings.getHeight() * 0.1f,1.0f));
        layerAndEffectsQueueContainer.setLocalTranslation(new Vector3f(settings.getWidth() * 0.25f, settings.getHeight() * 0.99f,1.0f));
        
        queuedLayerStagingContainer = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        queuedEffectStagingContainer = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        
        queuedLayerStagingContainer.addChild(new Label("Queued Layer:"));
        queuedEffectStagingContainer.addChild(new Label("Queued Effect:"));
        
        layerAndEffectsQueueContainer.addChild(queuedLayerStagingContainer);
        layerAndEffectsQueueContainer.addChild(queuedEffectStagingContainer, 0, 1);
        
        
       //make rollup for selecting a new Layer to queue up 
        Container defaultBlendLayersContainer = new Container();
        
        Button addIceButton = new Button("Ice");
        addIceButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {      
            setQueuedShaderBlendLayer(ShaderBlendLayerGenerator.makeDefaultIceLayer(assetManager));
        });
        Button addStoneButton = new Button("Stone");
        addStoneButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {      
            setQueuedShaderBlendLayer(ShaderBlendLayerGenerator.makeDefaultStoneLayer(assetManager));
        });
        Button addShieldArmorButton = new Button("Shield");
        addShieldArmorButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {      
            setQueuedShaderBlendLayer(ShaderBlendLayerGenerator.makeDefaultShieldArmorLayer(assetManager));
        });
        Button addRustButton = new Button("Rust");
        addRustButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {      
            setQueuedShaderBlendLayer(ShaderBlendLayerGenerator.makeDefaultRustLayer(assetManager));
        });
        Button addBlankLayerButton = new Button("Custom_Layer");
        addBlankLayerButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {      
            setQueuedShaderBlendLayer(ShaderBlendLayerGenerator.makeBlankLayer("" + blendGroup.getCurrentLayerCount()));
        });
        defaultBlendLayersContainer.addChild(addIceButton);
        defaultBlendLayersContainer.addChild(addStoneButton);
        defaultBlendLayersContainer.addChild(addShieldArmorButton);
        defaultBlendLayersContainer.addChild(addRustButton);
        defaultBlendLayersContainer.addChild(addBlankLayerButton);
        
        defaultLayerSelectionRollup = new RollupPanel("Queue up New Layer", defaultBlendLayersContainer, "glass");
        defaultLayerSelectionRollup.setAlpha(0, false);
        defaultLayerSelectionRollup.setOpen(false);
        defaultLayerSelectionRollup.setInsets(new Insets3f(5,5,0,5));
        
        queuedLayerStagingContainer.addChild(defaultLayerSelectionRollup, 2, 0);
        
     //make rollup for selecting a new Effect to queue up 
        Container defaultBlendEffectsContainer = new Container();
        
        Button addSimpleTimeBlendEffectButton = new Button("Timed_Blend");
        addSimpleTimeBlendEffectButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {      
            setQueuedEffect(new TimedBlendEffect());
        });
        Button addHeightScanEffectButton = new Button("Height_Scan");
        addHeightScanEffectButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {      
            setQueuedEffect(new HeightScanEffect());
        });
        Button addHsvCycleBlendEffectButton = new Button("HSV_Effect");
        addHsvCycleBlendEffectButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {      
            setQueuedEffect(new HSVCycleEffect());
        });
        defaultBlendEffectsContainer.addChild(addSimpleTimeBlendEffectButton);
        defaultBlendEffectsContainer.addChild(addHeightScanEffectButton);
        defaultBlendEffectsContainer.addChild(addHsvCycleBlendEffectButton);
        
        defaultEffectSelectionRollup = new RollupPanel("Queue up New Effect", defaultBlendEffectsContainer, "glass");
        defaultEffectSelectionRollup.setAlpha(0, false);
        defaultEffectSelectionRollup.setOpen(false);
        defaultEffectSelectionRollup.setInsets(new Insets3f(5,5,0,5));
        
        queuedEffectStagingContainer.addChild(defaultEffectSelectionRollup, 2, 0);
        
        
        //effect queue area
        
        guiNode.attachChild(layerAndEffectsQueueContainer);
        
    }

    private void configureCamera() {
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustumPerspective(45, aspect, 0.01f, 1000f);

//       flyCam.setDragToRotate(true);
//       flyCam.setMoveSpeed(25);
    }

    private void setupCamera() {
        // disable the default 1st-person flyCam!
        flyCam.setEnabled(false);

        Node target = new Node("CamTarget");
        target.move(0, 1, 0);

        ChaseCameraAppState chaseCam = new ChaseCameraAppState();
        chaseCam.setTarget(target);
        stateManager.attach(chaseCam);
        chaseCam.setInvertHorizontalAxis(true);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setZoomSpeed(0.5f);
        chaseCam.setMinDistance(1);
        chaseCam.setMaxDistance(10);
        chaseCam.setDefaultDistance(3);
        chaseCam.setMinVerticalRotation(-FastMath.HALF_PI);
        chaseCam.setRotationSpeed(3);
        chaseCam.setDefaultVerticalRotation(0.3f);
    }

    private void loadModel() {
        model = assetManager.loadModel(MODEL_ASSET_PATH);
        rootNode.attachChild(model);
        
        this.setCharacterShader(model);

        animComposer = GameObject.getComponentInChildren(model, AnimComposer.class);
        animsQueue.addAll(animComposer.getAnimClipsNames());
        animsQueue.forEach(System.out::println);

        skinningControl = GameObject.getComponentInChildren(model, SkinningControl.class);
    //    armatureDebugState.addArmatureFrom(skinningControl);

        // skinningControl.setHardwareSkinningPreferred(false);
        
        
    }
    
    private BitmapText makeTextUI(String text, ColorRGBA color) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText bmp = new BitmapText(font);
        bmp.setName("MyLabel");
        bmp.setText(text);
        bmp.setColor(color);
        bmp.setSize(font.getCharSet().getRenderedSize());
        guiNode.attachChild(bmp);
        return bmp;
    }

    /**
     * Mapping a named action to a key input.
     */
    private void initKeys() {
        addMapping("Speed+", new KeyTrigger(KeyInput.KEY_U));
        addMapping("Speed-", new KeyTrigger(KeyInput.KEY_I));
        addMapping("Next", new KeyTrigger(KeyInput.KEY_N));
        addMapping("ToggleArmature", new KeyTrigger(KeyInput.KEY_H));
    }

    private void addMapping(String mappingName, Trigger... triggers) {
        inputManager.addMapping(mappingName, triggers);
        inputManager.addListener(actionListener, mappingName);
    }

    /**
     * Defining the named action that can be triggered by key inputs.
     */
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (!keyPressed) {
                return;
            }

            if (name.equals("Next")) {
                String anim = animsQueue.poll();
                animsQueue.add(anim);
                animComposer.setCurrentAction(anim);
                animUI.setText("CurrentAction: " + anim);

            } else if (name.equals("Speed+")) {
                float animSpeed = animComposer.getGlobalSpeed();
                if (animSpeed > 2.0f) {
                    animSpeed = 2;
                }
                animComposer.setGlobalSpeed(animSpeed + 0.1f);

            } else if (name.equals("Speed-")) {
                float animSpeed = animComposer.getGlobalSpeed();
                if (animSpeed < 0.0f)
                    animSpeed = 0;
                animComposer.setGlobalSpeed(animSpeed - 0.1f);

            } else if (name.equals("ToggleArmature")) {
                armatureDebugState.setEnabled(!armatureDebugState.isEnabled());
            }
        }
    };
    
    private void removeBlendLayerEffect(BlendLayerEffectDisplayContainer blendLayerEffectDisplayContainer) {        
        if(blendLayerEffectDisplayContainer.equals(this.queuedEffectDisplayContainer)){ 
            this.queuedBlendLayerEffect = null;            
        }else{
            queuedBlendLayerEffect.cancel();
        }
        
        //remove from display
        if(blendLayerEffectDisplayContainer.getParent() != null){
            blendLayerEffectDisplayContainer.getParent().detachChild(blendLayerEffectDisplayContainer);
        }        
    }

    private void removeShaderBlendLayer(ShaderBlendLayerDisplayContainer shaderBlendLayerDisplayContainer) {

        if(shaderBlendLayerDisplayContainer.getShaderBlendLayer() == this.queuedShaderBlendLayer){ 
            this.queuedShaderBlendLayer = null;            
        }
        
        this.shaderBlendLayerManager.unregisterLayer(blendGroup, shaderBlendLayerDisplayContainer.getShaderBlendLayer());
        
        //remove from display
        if(shaderBlendLayerDisplayContainer.getParent() != null){
            shaderBlendLayerDisplayContainer.getParent().detachChild(shaderBlendLayerDisplayContainer);
        }
        
    }
        
    private void applyEffectToLayer(ShaderBlendLayer layer, BlendLayerEffect effect){
        if(layer != null && effect != null){
            layer.registerEffect(effect);
        }
    }

    private void setQueuedShaderBlendLayer(ShaderBlendLayer layer) {
        queuedShaderBlendLayer = layer;
        
        queuedBlendLayerDisplayContainer = makeShaderBlendLayerDisplayContainer(layer);        
        queuedBlendLayerDisplayContainer.getPropertyPanel().setOpen(true);
        
        queuedLayerStagingContainer.addChild(queuedBlendLayerDisplayContainer, 1, 0);
        
        defaultLayerSelectionRollup.setOpen(false);        
    }
    
    private void setQueuedEffect(BlendLayerEffect effect){
        queuedBlendLayerEffect = effect;
        
        queuedEffectDisplayContainer = makeBlendLayerDisplayContainer(effect);     
        queuedEffectDisplayContainer.getPropertyPanel().setOpen(true);
        
        queuedEffectStagingContainer.addChild(queuedEffectDisplayContainer, 1, 0);        
        
        defaultEffectSelectionRollup.setOpen(false);
    }
    

    private ShaderBlendLayerDisplayContainer makeShaderBlendLayerDisplayContainer(ShaderBlendLayer layer){
        ShaderBlendLayerDisplayContainer shaderBlendLayerDisplayContainer = new ShaderBlendLayerDisplayContainer(layer);
        
        shaderBlendLayerDisplayContainer.xRemoveButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {                      
            removeShaderBlendLayer(shaderBlendLayerDisplayContainer);                
        });
        
        shaderBlendLayerDisplayContainer.addQueuedEffectToThisLayerButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {                      
           
            if(queuedEffectDisplayContainer != null){
                 applyEffectToLayer(layer, queuedBlendLayerEffect);
                queuedBlendLayerEffect = null;
                if(queuedEffectDisplayContainer.getParent() != null){
                    queuedEffectDisplayContainer.getParent().detachChild(queuedEffectDisplayContainer);
                }    
            }
            
        });
        
        return shaderBlendLayerDisplayContainer;        
    }            
                        
     private BlendLayerEffectDisplayContainer makeBlendLayerDisplayContainer(BlendLayerEffect effect){
        BlendLayerEffectDisplayContainer shaderBlendLayerDisplayContainer = new BlendLayerEffectDisplayContainer(effect);
        
        shaderBlendLayerDisplayContainer.xRemoveButton.addCommands(Button.ButtonAction.Click, (Command<Button>) (Button source) -> {                      
            removeBlendLayerEffect(shaderBlendLayerDisplayContainer);                
        });
        
        
        return shaderBlendLayerDisplayContainer;        
    }                  
    
            
            

}