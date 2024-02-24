package com.github.shader.demo.states;

import com.jme3.app.Application;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.LightProbe;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;

import jme3utilities.sky.SkyControl;
import jme3utilities.sky.StarsOption;
import jme3utilities.sky.Updater;

/**
 * 
 * @author capdevon
 */
public class DefaultSceneState extends MySimpleState {
    
    private boolean dynamicSky = true;
    
    @Override
    protected void initialize(Application app) {
        super.initialize(app);
        
        initFloor();
        initFilters();
        
        if (dynamicSky) {
            initDynamicSky();
        } else {
            setupSky();
        }
    }

    /**
     * a sky as background
     */
    private void setupSky() {
        String textureName = "Scenes/Beach/FullskiesSunset0068.dds";
        Spatial sky = SkyFactory.createSky(assetManager, textureName, SkyFactory.EnvMapType.CubeMap);
        sky.setShadowMode(RenderQueue.ShadowMode.Off);
        rootNode.attachChild(sky);
    }

    private void initFilters() {
        
        viewPort.setBackgroundColor(ColorRGBA.Gray);
        rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
        
        DirectionalLight sunLight = new DirectionalLight();
        sunLight.setName("main");
        sunLight.setDirection(new Vector3f(-1f, -1f, 0).normalizeLocal());
        rootNode.addLight(sunLight);

        AmbientLight ambient = new AmbientLight();
        ambient.setName("ambient");
        ambient.setColor(new ColorRGBA(0.25f, 0.25f, 0.25f, 1));
//       rootNode.addLight(ambient);

        // add a PBR probe.
        Spatial probeModel = assetManager.loadModel("Scenes/defaultProbe.j3o");
        LightProbe lightProbe = (LightProbe) probeModel.getLocalLightList().get(0);
        lightProbe.setName("LightProbe");
        lightProbe.getArea().setRadius(100);
        rootNode.addLight(lightProbe);

        DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 4096, 3);
        dlsf.setLight(sunLight);
        dlsf.setShadowIntensity(0.4f);
        dlsf.setShadowZExtend(256);
        dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);

        FXAAFilter fxaa = new FXAAFilter();
        
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        fpp.addFilter(dlsf);
        fpp.addFilter(fxaa);
        viewPort.addProcessor(fpp);
    }
    
    private void initFloor() {
        Material pbr = new Material(assetManager, "Common/MatDefs/Light/PBRLighting.j3md");
        Texture texture = assetManager.loadTexture("Textures/default_grid.png");
        texture.setWrap(WrapMode.Repeat);
        pbr.setTexture("BaseColorMap", texture);
//        pbr.setColor("BaseColor", ColorRGBA.Gray);
        pbr.setFloat("Metallic", 0.4f);
        pbr.setFloat("Roughness", 0.6f);

        Box mesh = new Box(10, 0.5f, 10);
        mesh.scaleTextureCoordinates(new Vector2f(5, 5));
        Geometry geo = new Geometry("Floor", mesh);
        geo.setMaterial(pbr);
        geo.setLocalTranslation(0, -0.5f, 0);
        rootNode.attachChild(geo);
    }
    
    /**
     * Create and attach the sky.
     */
    private void initDynamicSky() {
        float cloudFlattening = 0.8f;
        boolean bottomDome = true;
        SkyControl skyControl = new SkyControl(assetManager, cam,
                cloudFlattening, StarsOption.Cube, bottomDome);
        rootNode.addControl(skyControl);
        skyControl.setCloudiness(0.2f);
        skyControl.setCloudsYOffset(0.4f);
        skyControl.getSunAndStars().setHour(12f);
        skyControl.setEnabled(true);

        Updater updater = skyControl.getUpdater();
        for (Light light : rootNode.getLocalLightList()) {
            String lightName = light.getName();
            switch (lightName) {
                case "ambient":
                    updater.setAmbientLight((AmbientLight) light);
                    break;
                case "main":
                    updater.setMainLight((DirectionalLight) light);
                    break;
                default:
            }
        }
    }

}
