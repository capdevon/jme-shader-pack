package com.github.shader.demo;

import com.github.shader.demo.utils.AdapterControl;
import com.github.shader.demo.utils.RotateControl;
import com.github.tools.editor.FilterEditorBuilder;
import com.github.tools.material.MatPropertyPanelBuilder;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.post.filters.ColorOverlayFilter;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.CenterQuad;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.PQTorus;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

/**
 * 
 * @author capdevon
 */
public class Test_Hologram2 extends SimpleApplication implements ActionListener {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        Test_Hologram2 app = new Test_Hologram2();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();
    }
    
    private Geometry geom;
    private Material holoMat;
    private int meshIndex = 0;
    private final Mesh[] arrayMesh = new Mesh[] { 
            new Box(.5f, .5f, .5f), 
            new Sphere(32, 32, .6f),
            new Cylinder(32, 32, .8f, 1.0f, true), 
            new PQTorus(2f, 3f, 0.6f, 0.2f, 48, 16),
            new Torus(16, 16, 0.15f, 0.5f) 
            };

    @Override
    public void simpleInitApp() {
        
//        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        configureCamera();
        initScene();
        initFilters();
        initLemur(this);

        inputManager.addMapping("NextMesh", new KeyTrigger(KeyInput.KEY_N));
        inputManager.addListener(this, "NextMesh");
        
//        stateManager.attach(new DebugGridState());
//        stateManager.attach(new DetailedProfilerState());
    }

    private void configureCamera() {
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustumPerspective(45, aspect, 0.01f, 1000f);

        flyCam.setMoveSpeed(25f);
        flyCam.setDragToRotate(true);
    }
    
    private void initLemur(Application app) {
        // initialize lemur
        GuiGlobals.initialize(app);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
        
        initMaterialEditor();
        initFilterEditor();
    }

    private void initFilterEditor() {
        FilterEditorBuilder builder = new FilterEditorBuilder();
        Container container = builder.buildPanel(viewPort);
        container.setLocalTranslation(10f, settings.getHeight() - 10f, 1);
        guiNode.attachChild(container);
    }
    
    private void initMaterialEditor() {
        MatPropertyPanelBuilder builder = new MatPropertyPanelBuilder();
        Container container = builder.buildPanel(geom);
        container.setLocalTranslation(settings.getWidth() - settings.getWidth() / 4f, settings.getHeight() - 10f, 1);
        guiNode.attachChild(container);
    }
    
    private void initScene() {
        
        addQuad(new Vector3f(0, 0, -2), Quaternion.IDENTITY, rootNode);
        addQuad(new Vector3f(0, 0, 0), new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X), rootNode);
        
        Vector3f position = new Vector3f(0, 2, 0);
        ColorRGBA color = ColorRGBA.Blue;
        
        holoMat = createHologramMat();
        holoMat.setColor("Color", color);
        holoMat.setFloat("ScanOffset", 10f);

        geom = new Geometry("Hologram", arrayMesh[meshIndex]);
        geom.setMaterial(holoMat);
        geom.setQueueBucket(RenderQueue.Bucket.Transparent);
        geom.setLocalTranslation(position);
        geom.addControl(new RotateControl(1.2f));
        rootNode.attachChild(geom);

        float radius = 5f;
        PointLight light = new PointLight(position, color, radius);
        rootNode.addLight(light);
        
        rootNode.addControl(new AdapterControl() {
            @Override
            protected void controlUpdate(float tpf) {
                light.setColor(holoMat.getParamValue("Color"));
            }
        });
    }

    /**
     */
    private void addQuad(Vector3f position, Quaternion rotation, Node parent) {
        Material pbr = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        Texture texture = assetManager.loadTexture("Textures/default_grid.png");
        texture.setWrap(WrapMode.Repeat);
        pbr.setTexture("BaseColorMap", texture);
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

    private Material createHologramMat() {
        Material mat = new Material(assetManager, "MatDefs/hologram.j3md");
        mat.setName("MAT_Hologram");
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTransparent(true);
        return mat;
    }

    private void initFilters() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        rootNode.addLight(sun);

//        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 4_096, 3);
//        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
//        dlsr.setEdgesThickness(5);
//        dlsr.setShadowIntensity(0.65f);
//        dlsr.setLight(sun);
//        viewPort.addProcessor(dlsr);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 4096, 3);
        dlsf.setLight(sun);
        dlsf.setShadowIntensity(0.4f);
        dlsf.setShadowZExtend(256);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);

        /**
         * for PBR, you need to use GlowMode.Scene in the BloomFilter
         */
        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Scene);
        bloom.setBloomIntensity(5.0f);

        ToneMapFilter toneMap = new ToneMapFilter(Vector3f.UNIT_XYZ.mult(4.0f));
        FXAAFilter fxaa = new FXAAFilter();
        ColorOverlayFilter overlay = new ColorOverlayFilter();
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(toneMap);
        fpp.addFilter(bloom);
        fpp.addFilter(dlsf);
        fpp.addFilter(fxaa);
        fpp.addFilter(overlay);
        viewPort.addProcessor(fpp);
    }

    @Override
    public void onAction(String name, boolean isPressed, float tpf) {
        if (name.equals("NextMesh") && isPressed) {
            meshIndex = (meshIndex + 1) % arrayMesh.length;
            geom.setMesh(arrayMesh[meshIndex]);
            geom.updateModelBound();
        }
    }

}
