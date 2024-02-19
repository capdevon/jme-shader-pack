package com.github.shader.pack;

import java.util.ArrayList;
import java.util.List;

import com.github.mat.editor.MatPropertyPanelBuilder;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.CenterQuad;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.mikktspace.MikktspaceTangentGenerator;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.style.BaseStyles;

/**
 * 
 * @author ryan
 */
public class Test_CharacterEffects extends SimpleApplication {

    private Spatial erikaSpatial, yBotSpatial;
    
    public BlendLayerEffect iceEffect, stoneEffect, shieldEffect;
    
    private List<BlendLayerEffect> activeEffectsInOrder = new ArrayList<>();
    private List<Slider> blendValSliders = new ArrayList<>();
    private List<VersionedReference> sliderVersionedReferences = new ArrayList<>();
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        Test_CharacterEffects app = new Test_CharacterEffects();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);

        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();
    }
    
    @Override
    public void simpleInitApp() {

//        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        configureCamera();
        initScene();
        initFilters();
        initLemur(this);

        iceEffect = new BlendLayerEffect("Freeze", 0, yBotSpatial);
        iceEffect.addMaterialsFromSpatial(erikaSpatial);
        iceEffect.setBaseColorMap(assetManager.loadTexture("Models/Cracked_Ice/DefaultMaterial_baseColor.png"));
        iceEffect.setNormalMap(assetManager.loadTexture("Models/Cracked_Ice/DefaultMaterial_normal.png"));
        iceEffect.setMetallicRoughnessAoMap(
                assetManager.loadTexture("Models/Cracked_Ice/DefaultMaterial_occlusionRoughnessMetallic.png"));

        stoneEffect = new BlendLayerEffect("Petrify", 1, erikaSpatial);
        stoneEffect.addMaterialsFromSpatial(yBotSpatial);
        stoneEffect.setBaseColorMap(assetManager.loadTexture("Models/Cracked_Stone/DefaultMaterial_baseColor.png"));
        stoneEffect.setNormalMap(assetManager.loadTexture("Models/Cracked_Stone/DefaultMaterial_normal.png"));
        stoneEffect.setMetallicRoughnessAoMap(
                assetManager.loadTexture("Models/Cracked_Stone/DefaultMaterial_occlusionRoughnessMetallic.png"));

        shieldEffect = new BlendLayerEffect("Shield", 2, erikaSpatial);
        shieldEffect.addMaterialsFromSpatial(yBotSpatial);
        shieldEffect.setBaseColorMap(assetManager.loadTexture("Models/Shield_Armor/DefaultMaterial_baseColor.png"));
        shieldEffect.setNormalMap(assetManager.loadTexture("Models/Shield_Armor/DefaultMaterial_normal.png"));
        shieldEffect.setMetallicRoughnessAoMap(
                assetManager.loadTexture("Models/Shield_Armor/DefaultMaterial_occlusionRoughnessMetallic.png"));
        shieldEffect.setEmissiveMap(assetManager.loadTexture("Models/Shield_Armor/DefaultMaterial_emissive.png"));
        shieldEffect.setBlendAlpha(true);

        registerBlendEffectToSlider(iceEffect);
        registerBlendEffectToSlider(stoneEffect);
        registerBlendEffectToSlider(shieldEffect);

        stateManager.attach(new DebugGridState());
//        stateManager.attach(new DetailedProfilerState());
    }
    
    private void registerBlendEffectToSlider(BlendLayerEffect blendLayerEffect) {
        activeEffectsInOrder.add(blendLayerEffect);

        // make container for tweaking this blend effect 
        // (currently only has a slider for blendVal)
        Container blendLayerContainer = new Container();
        Label label = new Label(blendLayerEffect.getName());
        Slider slider = new Slider();
        DefaultRangedValueModel sizeSliderModel = new DefaultRangedValueModel(0, 1.0, 0);
        slider.setModel(sizeSliderModel);

        blendValSliders.add(slider);
        sliderVersionedReferences.add(sizeSliderModel.createReference());

        blendLayerContainer.setLocalTranslation(10f, settings.getHeight() - 10f, 1);
        blendLayerContainer.addChild(label);
        blendLayerContainer.addChild(slider);
        guiNode.attachChild(blendLayerContainer);
    }

    @Override
    public void simpleUpdate(float tpf) {
        
        for (int i = 0; i < sliderVersionedReferences.size(); i++) {
            VersionedReference sliderVersionedReference = sliderVersionedReferences.get(i);
            Slider slider = blendValSliders.get(i);
            if (sliderVersionedReference.update()) {
                float val = (float) slider.getModel().getValue();
                if (val > 1) {
                    val = 1;
                }
                if (val < 0) {
                    val = 0;
                }

                BlendLayerEffect blendEffect = activeEffectsInOrder.get(i);
                blendEffect.setBlendValue(val);
            }
        }
                    
//        //sync all materials with the first material so params for effects are applied to whole spatial, and not just 1 material/geometry at a time
//        Material firstMat = null;
//        for(int i = 0; i < characterMaterials.size(); i++){
//            if(firstMat == null){
//                firstMat = characterMaterials.get(i);
//            }else{
//                Material mat = characterMaterials.get(i);
//                
//                ArrayList<MatParam> matParams = new ArrayList<>(firstMat.getParams());
//                for(int p = 0; p < matParams.size(); p++){
//                    MatParam matParam = matParams.get(p);
//                    
//                    if(paramsToSync.contains(matParam.getName())){
//                        MatParam paramToCopyTo = mat.getParam(matParam.getName());
//                        if(paramToCopyTo == null || !paramToCopyTo.getValue().equals(matParam.getValue())){
//                            mat.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
//                        }                
//                    }
//                }
//            }
//        }
    }
    
    private void configureCamera() {
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustumPerspective(45, aspect, 0.01f, 1000f);

        flyCam.setMoveSpeed(5f);
        flyCam.setDragToRotate(true);
    }
    
    private void initLemur(Application app) {
        // initialize lemur
        GuiGlobals.initialize(app);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        
        initMaterialEditor();
    }
    
    private void initMaterialEditor() {
        MatPropertyPanelBuilder builder = new MatPropertyPanelBuilder();
        Container container = builder.buildPanel(erikaSpatial);
        container.setLocalTranslation(settings.getWidth() - settings.getWidth() / 4f, settings.getHeight() - 10f, 1);
        guiNode.attachChild(container);
    }
    
    private void initScene() {

        addQuad(new Vector3f(0, 0, -2), Quaternion.IDENTITY, rootNode);
        addQuad(new Vector3f(0, 0, 0), new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X), rootNode);

        yBotSpatial = assetManager.loadModel("Models/YBot/YBot.j3o");
        erikaSpatial = assetManager.loadModel("Models/Erika/Erika.j3o");

        rootNode.attachChild(erikaSpatial);
        rootNode.attachChild(yBotSpatial);

        yBotSpatial.move(0.8f, 0, 0);
        erikaSpatial.move(-0.8f, 0, 0);

        setCorrectShader(yBotSpatial);
        setCorrectShader(erikaSpatial);
    }

    private void setCorrectShader(Spatial spatial) {
        spatial.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                Material oldMat = geom.getMaterial();
                Material characterMat = new Material(assetManager, "MatDefs/PBRCharacters.j3md");

                List<MatParam> matParams = new ArrayList<>(oldMat.getParams());
                for (int p = 0; p < matParams.size(); p++) {
                    MatParam matParam = matParams.get(p);
                    if (matParam.getValue() != null) {
                        Object val = matParam.getValue();
                        characterMat.setParam(matParam.getName(), matParam.getVarType(), val);
                    }
                }
                geom.setMaterial(characterMat);
                MikktspaceTangentGenerator.generate(geom);
            }
        });
    }
    
    /**
     */
    private void addQuad(Vector3f position, Quaternion rotation, Node parent) {
        Material pbr = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        Texture tex = assetManager.loadTexture("Textures/default_grid.png");
        tex.setWrap(WrapMode.Repeat);
        pbr.setTexture("BaseColorMap", tex);
        pbr.setColor("BaseColor", ColorRGBA.Gray);
        pbr.setFloat("Metallic", 0.6f);
        pbr.setFloat("Roughness", 0.4f);

        CenterQuad quad = new CenterQuad(20, 10);
        quad.scaleTextureCoordinates(new Vector2f(2, 1));
        Geometry geo = new Geometry("Quad", quad);
        geo.setMaterial(pbr);
        geo.setLocalTranslation(position);
        geo.setLocalRotation(rotation);
        parent.attachChild(geo);
    }

    private void initFilters() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        rootNode.addLight(sun);

//        Node probeNode = (Node) assetManager.loadModel("LightProbes/defaultProbe.j3o");
//        LightProbe lightProbe = (LightProbe) probeNode.getLocalLightList().get(0);
        LightProbe lightProbe = (LightProbe) assetManager.loadAsset("LightProbes/quarry_Probe.j3o");
        rootNode.addLight(lightProbe);

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 4_096, 3);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        dlsr.setEdgesThickness(5);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.65f);
        viewPort.addProcessor(dlsr);

        AmbientLight al = new AmbientLight(new ColorRGBA(1.2f, 1.2f, 1.3f, 0.0f));
        rootNode.addLight(al);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);

        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setBloomIntensity(5.0f);
        fpp.addFilter(bloom);

        FXAAFilter fxaa = new FXAAFilter();
        fpp.addFilter(fxaa);
    }

}
