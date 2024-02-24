package com.github.shader.demo;

import com.github.shader.demo.states.BlendEffectState;
import com.github.shader.demo.utils.GameObject;
import com.jme3.anim.AnimComposer;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
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
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.CenterQuad;
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

/**
 * 
 * @author ryan
 */
public class Test_CharacterEffects extends SimpleApplication {

    private Spatial erikaSpatial;
    private Spatial yBotSpatial;
    
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
        app.setDisplayFps(false);
        app.setDisplayStatView(false);
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

        stateManager.attach(new BlendEffectState(erikaSpatial));
//        stateManager.attach(new DebugGridState());
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
    
    private void initScene() {

        addQuad(new Vector3f(0, 0, -2), Quaternion.IDENTITY, rootNode);
        addQuad(new Vector3f(0, 0, 0), new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X), rootNode);

        yBotSpatial = loadModel("Models/YBot/YBot.j3o", new Vector3f(-1f, 0, 0), rootNode);
        erikaSpatial = loadModel("Models/Erika/Erika.j3o", new Vector3f(1f, 0, 0), rootNode);
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
        DirectionalLight sunLight = new DirectionalLight();
        sunLight.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        rootNode.addLight(sunLight);
        
        AmbientLight al = new AmbientLight();
        al.setColor(new ColorRGBA(1.2f, 1.2f, 1.3f, 0.0f));
        //rootNode.addLight(al);

        // add a PBR probe.
        Spatial probeModel = assetManager.loadModel("LightProbes/defaultProbe.j3o");
        LightProbe lightProbe = (LightProbe) probeModel.getLocalLightList().get(0);
        lightProbe.getArea().setRadius(100);
        rootNode.addLight(lightProbe);

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 4_096, 3);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        dlsr.setEdgesThickness(5);
        dlsr.setLight(sunLight);
        dlsr.setShadowIntensity(0.65f);
        viewPort.addProcessor(dlsr);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);

        FXAAFilter fxaa = new FXAAFilter();
        fpp.addFilter(fxaa);
    }

}
