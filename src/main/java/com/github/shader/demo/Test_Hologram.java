package com.github.shader.demo;

import java.io.File;

import com.github.shader.demo.states.DebugGridState;
import com.github.shader.demo.utils.RotateControl;
import com.jme3.app.DetailedProfilerState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.VideoRecorderAppState;
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
import com.jme3.post.filters.FXAAFilter;
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
import com.jme3.shadow.DirectionalLightShadowRenderer;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import jme3utilities.math.noise.Generator;

/**
 * 
 * @author capdevon
 */
public class Test_Hologram extends SimpleApplication {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {
        Test_Hologram app = new Test_Hologram();
        AppSettings settings = new AppSettings(true);
        settings.setResolution(1280, 720);
        
        app.setSettings(settings);
        app.setShowSettings(false);
        app.setPauseOnLostFocus(false);
        app.start();
    }
    
    private static final Generator gen = new Generator();
    private static final boolean captureVideo = false;

    @Override
    public void simpleInitApp() {

//        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        configureCamera();
        initScene();
        initFilters();

        stateManager.attach(new DebugGridState());
//        stateManager.attach(new DetailedProfilerState());

        if (captureVideo) {
            captureVideo();
        }
    }

    private void captureVideo() {
        String dirName = System.getProperty("user.home") + "/Screenshot";
        String fileName = "hologram" + System.currentTimeMillis() + ".avi";
        File output = new File(dirName, fileName);
        stateManager.attach(new VideoRecorderAppState(output));
    }

    private void configureCamera() {
        float aspect = (float) cam.getWidth() / cam.getHeight();
        cam.setFrustumPerspective(45, aspect, 0.01f, 1000f);

        flyCam.setMoveSpeed(25f);
        flyCam.setDragToRotate(true);
    }
    
    private void initScene() {
        
        addQuad(new Vector3f(0, 0, -2), Quaternion.IDENTITY, rootNode);
        addQuad(new Vector3f(0, 0, 0), new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X), rootNode);
        
        String[] arrayName = { "Cube", "Sphere", "Cylinder", "PQTorus", "Torus" };

        Mesh[] arrayMesh = new Mesh[] { 
                new Box(.5f, .5f, .5f), 
                new Sphere(32, 32, .6f),
                new Cylinder(32, 32, .8f, 1.0f, true), 
                new PQTorus(2f, 3f, 0.6f, 0.2f, 48, 16),
                new Torus(16, 16, 0.15f, 0.5f) 
                };

        Vector3f v = new Vector3f(-4, 2, 0);
        float distance = 2;
        
        for (int i = 0; i < arrayMesh.length; i++) {
            Vector3f position = new Vector3f(1, 0, 0).scaleAdd(i * distance, v);
            ColorRGBA color = ColorRGBA.randomColor();

            Geometry geom = new Geometry(arrayName[i], arrayMesh[i]);
            Material mat = createHologramMat();
            mat.setColor("Color", color);
            mat.setFloat("ScanOffset", gen.nextFloat(0, 100));
            geom.setMaterial(mat);
            geom.setQueueBucket(RenderQueue.Bucket.Transparent);
            geom.setLocalTranslation(position);
            geom.addControl(new RotateControl(1.2f));
            rootNode.attachChild(geom);

            PointLight light = new PointLight(position, color, 5f);
            light.setName("PointLight-" + arrayName[i]);
            rootNode.addLight(light);
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

    private Material createHologramMat() {
        Material mat = new Material(assetManager, "MatDefs/hologram.j3md");
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTransparent(true);
        return mat;
    }

    private void initFilters() {
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(-1, -1, -1).normalizeLocal());
        rootNode.addLight(sun);

        DirectionalLightShadowRenderer dlsr = new DirectionalLightShadowRenderer(assetManager, 4_096, 3);
        dlsr.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
        dlsr.setEdgesThickness(5);
        dlsr.setLight(sun);
        dlsr.setShadowIntensity(0.65f);
        viewPort.addProcessor(dlsr);

        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        viewPort.addProcessor(fpp);

        BloomFilter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);
        bloom.setBloomIntensity(5.0f);
        fpp.addFilter(bloom);

        FXAAFilter fxaa = new FXAAFilter();
        fpp.addFilter(fxaa);
    }

}
