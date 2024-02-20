package com.github.shader.demo;

import java.util.ArrayList;
import java.util.List;

import com.github.shader.demo.utils.BlendLayerEffect;
import com.github.shader.demo.utils.DebugGridState;
import com.github.shader.demo.utils.GameObject;
import com.github.tools.material.MatPropertyPanelBuilder;
import com.jme3.anim.AnimComposer;
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
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.style.BaseStyles;

/**
 * 
 * @author ryan
 */
public class Test_CharacterEffects extends SimpleApplication {

    private Spatial erikaSpatial;
    private Spatial yBotSpatial;
    
    private List<BlendLayerEffect> lstEffects = new ArrayList<>();
    private List<Slider> blendValSliders = new ArrayList<>();
    private List<VersionedReference<Double>> sliderRefs = new ArrayList<>();
    
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

        initLemur(this);
        configureCamera();
        initScene();
        initFilters();

        stateManager.attach(new DebugGridState());
//        stateManager.attach(new DetailedProfilerState());
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
    }
    
    private void initMaterialEditor(Spatial model) {
        MatPropertyPanelBuilder builder = new MatPropertyPanelBuilder();
        Container container = builder.buildPanel(model);
        container.setLocalTranslation(settings.getWidth() - settings.getWidth() / 4f, settings.getHeight() - 10f, 1);
        guiNode.attachChild(container);
    }
    
    private void initScene() {

        addQuad(new Vector3f(0, 0, -2), Quaternion.IDENTITY, rootNode);
        addQuad(new Vector3f(0, 0, 0), new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X), rootNode);

        yBotSpatial = loadModel("Models/YBot/YBot.j3o", new Vector3f(-1f, 0, 0), rootNode);
        erikaSpatial = loadModel("Models/Erika/Erika.j3o", new Vector3f(1f, 0, 0), rootNode);

//        setCharacterShader(yBotSpatial); // FIXME: ???
//        setCharacterShader(erikaSpatial); // FIXME: ???
        
        initMaterialEditor(erikaSpatial);
        initShaderEffects();
    }
    
    private Spatial loadModel(String asset, Vector3f position, Node parent) {
        Spatial model = assetManager.loadModel(asset);
        model.setLocalTranslation(position);
        parent.attachChild(model);
        
        AnimComposer animComposer = GameObject.getComponentInChildren(model, AnimComposer.class);
        String clipName = animComposer.getAnimClipsNames().toArray(new String[0])[0];
        animComposer.setCurrentAction(clipName);
        
        return model;
    }

    private void setCharacterShader(Spatial spatial) {
        spatial.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                Material oldMat = geom.getMaterial();
                Material newMat = new Material(assetManager, "MatDefs/PBRCharacters.j3md");

                List<MatParam> matParams = new ArrayList<>(oldMat.getParams());
                for (MatParam matParam : matParams) {
                    if (matParam.getValue() != null) {
                        newMat.setParam(matParam.getName(), matParam.getVarType(), matParam.getValue());
                    }
                }
                geom.setMaterial(newMat);
                MikktspaceTangentGenerator.generate(geom);
            }
        });
    }
    
    /**
     */
    private void initShaderEffects() {
        BlendLayerEffect ice = new BlendLayerEffect("Freeze", 0, yBotSpatial);
        ice.addMaterialsFromSpatial(erikaSpatial);
        ice.setBaseColorMap(assetManager.loadTexture("Models/Cracked_Ice/DefaultMaterial_baseColor.png"));
        ice.setNormalMap(assetManager.loadTexture("Models/Cracked_Ice/DefaultMaterial_normal.png"));
        ice.setMetallicRoughnessAoMap(
                assetManager.loadTexture("Models/Cracked_Ice/DefaultMaterial_occlusionRoughnessMetallic.png"));

        BlendLayerEffect stone = new BlendLayerEffect("Petrify", 1, erikaSpatial);
        stone.addMaterialsFromSpatial(yBotSpatial);
        stone.setBaseColorMap(assetManager.loadTexture("Models/Cracked_Stone/DefaultMaterial_baseColor.png"));
        stone.setNormalMap(assetManager.loadTexture("Models/Cracked_Stone/DefaultMaterial_normal.png"));
        stone.setMetallicRoughnessAoMap(
                assetManager.loadTexture("Models/Cracked_Stone/DefaultMaterial_occlusionRoughnessMetallic.png"));

        BlendLayerEffect shield = new BlendLayerEffect("Shield", 2, erikaSpatial);
        shield.addMaterialsFromSpatial(yBotSpatial);
        shield.setBaseColorMap(assetManager.loadTexture("Models/Shield_Armor/DefaultMaterial_baseColor.png"));
        shield.setNormalMap(assetManager.loadTexture("Models/Shield_Armor/DefaultMaterial_normal.png"));
        shield.setMetallicRoughnessAoMap(
                assetManager.loadTexture("Models/Shield_Armor/DefaultMaterial_occlusionRoughnessMetallic.png"));
        shield.setEmissiveMap(assetManager.loadTexture("Models/Shield_Armor/DefaultMaterial_emissive.png"));
        shield.setBlendAlpha(true);

        registerBlendEffects(ice, stone, shield);
    }
    
    private void registerBlendEffects(BlendLayerEffect... effects) {
        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        
        for (BlendLayerEffect effect : effects) {
            Label label = new Label(effect.getName());
            Slider slider = new Slider();
            DefaultRangedValueModel sliderModel = new DefaultRangedValueModel(0, 1.0, 0);
            slider.setModel(sliderModel);

            lstEffects.add(effect);
            blendValSliders.add(slider);
            sliderRefs.add(sliderModel.createReference());

            container.addChild(label);
            container.addChild(slider);
        }
        
        container.setLocalTranslation(10f, settings.getHeight() - 10f, 1);
        guiNode.attachChild(container);
    }

    @Override
    public void simpleUpdate(float tpf) {
        for (int i = 0; i < sliderRefs.size(); i++) {
            VersionedReference<Double> sliderRef = sliderRefs.get(i);
            Slider slider = blendValSliders.get(i);
            if (sliderRef.update()) {
                float val = (float) slider.getModel().getValue();
                val = FastMath.clamp(val, 0.0f, 1.0f);

                BlendLayerEffect effect = lstEffects.get(i);
                effect.setBlendValue(val);
            }
        }
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
        
        AmbientLight al = new AmbientLight(new ColorRGBA(1.2f, 1.2f, 1.3f, 0.0f));
        rootNode.addLight(al);

        // add a PBR probe.
        Spatial probeModel = assetManager.loadModel("LightProbes/defaultProbe.j3o");
        LightProbe lightProbe = (LightProbe) probeModel.getLocalLightList().get(0);
        lightProbe.getArea().setRadius(100);
        rootNode.addLight(lightProbe);

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 4_096, 3);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        dlsr.setEdgesThickness(5);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.65f);
        viewPort.addProcessor(dlsr);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);

        /**
         * for PBR, you need to use GlowMode.Scene in the BloomFilter
         */
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Scene);
        bloom.setBloomIntensity(5.0f);
        fpp.addFilter(bloom);

        FXAAFilter fxaa = new FXAAFilter();
        fpp.addFilter(fxaa);
    }

}
