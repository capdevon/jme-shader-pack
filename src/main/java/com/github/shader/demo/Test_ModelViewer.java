package com.github.shader.demo;

import java.util.LinkedList;
import java.util.Queue;

import com.github.shader.demo.utils.DebugGridState;
import com.github.shader.demo.utils.GameObject;
import com.github.tools.material.MatPropertyPanelBuilder;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.jme3.app.Application;
import com.jme3.app.ChaseCameraAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.Trigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.LightProbe;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.custom.ArmatureDebugAppState;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.EdgeFilteringMode;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

/**
 * 
 * @author capdevon
 */
public class Test_ModelViewer extends SimpleApplication {

    /**
    *
    * @param args
    */
   public static void main(String[] args) {
       Test_ModelViewer app = new Test_ModelViewer();
       AppSettings settings = new AppSettings(true);
       settings.setResolution(1280, 720);

       app.setSettings(settings);
       app.setShowSettings(false);
       app.setPauseOnLostFocus(false);
       app.start();
   }
   
   private static final String MODEL_ASSET_PATH = "Models/Erika/Erika.j3o"; //"Models/YBot/YBot.j3o";
   
   private BitmapText hud;
   private Spatial model;
   private AnimComposer animComposer;
   private SkinningControl skinningControl;
   private ArmatureDebugAppState armatureDebugState;
   private final Queue<String> animsQueue = new LinkedList<>();

   @Override
   public void simpleInitApp() {
       armatureDebugState = new ArmatureDebugAppState();
       stateManager.attach(armatureDebugState);
       stateManager.attach(new DebugGridState());
       
       hud = makeLabelUI("", ColorRGBA.Blue, guiNode);
       hud.setLocalTranslation(10f, settings.getHeight() - 10f, 0);
       
       rootNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
       
       configureCamera();
       setupCamera();
       setupSky();
       initFilters();
       loadModel();
       initLemur(this);
       initKeys();
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
   
   /**
    * a sky as background
    */
   private void setupSky() {
       Spatial sky = SkyFactory.createSky(assetManager, "Scenes/Beach/FullskiesSunset0068.dds", SkyFactory.EnvMapType.CubeMap);
       sky.setShadowMode(RenderQueue.ShadowMode.Off);
       rootNode.attachChild(sky);
   }

   private void initFilters() {
       DirectionalLight sun = new DirectionalLight();
       sun.setDirection(new Vector3f(-0.2f, -1, -0.3f).normalizeLocal());
       rootNode.addLight(sun);

       AmbientLight ambient = new AmbientLight();
       ambient.setColor(new ColorRGBA(0.25f, 0.25f, 0.25f, 1));
//       rootNode.addLight(ambient);

       // add a PBR probe.
       Spatial probeModel = assetManager.loadModel("Scenes/defaultProbe.j3o");
       LightProbe lightProbe = (LightProbe) probeModel.getLocalLightList().get(0);
       lightProbe.getArea().setRadius(100);
       rootNode.addLight(lightProbe);

       FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
       viewPort.addProcessor(fpp);

       DirectionalLightShadowFilter dlsf = new DirectionalLightShadowFilter(assetManager, 4096, 3);
       dlsf.setLight(sun);
       dlsf.setShadowIntensity(0.4f);
       dlsf.setShadowZExtend(256);
       dlsf.setEdgeFilteringMode(EdgeFilteringMode.PCFPOISSON);
       fpp.addFilter(dlsf);

       FXAAFilter fxaa = new FXAAFilter();
       fpp.addFilter(fxaa);
   }
   
   private void loadModel() {
       model = assetManager.loadModel(MODEL_ASSET_PATH);
       rootNode.attachChild(model);

       animComposer = GameObject.getComponentInChildren(model, AnimComposer.class);
       animsQueue.addAll(animComposer.getAnimClipsNames());
       animsQueue.forEach(System.out::println);

       skinningControl = GameObject.getComponentInChildren(model, SkinningControl.class);
       armatureDebugState.addArmatureFrom(skinningControl);
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
   
   private void initLemur(Application app) {
       // initialize lemur
       GuiGlobals.initialize(app);
       BaseStyles.loadGlassStyle();
       GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
       
       initMaterialEditor();
   }

   private void initMaterialEditor() {
       MatPropertyPanelBuilder builder = new MatPropertyPanelBuilder();
       Container container = builder.buildPanel(model);
       container.setLocalTranslation(settings.getWidth() - settings.getWidth() / 4f, settings.getHeight() - 10f, 1);
       guiNode.attachChild(container);
   }
   
   /**
    * Mapping a named action to a key input.
    */
   private void initKeys() {
       addMapping("Speed+", new KeyTrigger(KeyInput.KEY_U));
       addMapping("Speed-", new KeyTrigger(KeyInput.KEY_I));
       addMapping("Next", new KeyTrigger(KeyInput.KEY_N));
       addMapping("ToggleArmature", new KeyTrigger(KeyInput.KEY_M));
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
               hud.setText(anim);
               
           } else if (name.equals("Speed+")) {
               float glSpeed = animComposer.getGlobalSpeed();
               animComposer.setGlobalSpeed(glSpeed + 0.1f);
               
           } else if (name.equals("Speed-")) {
               float glSpeed = animComposer.getGlobalSpeed();
               animComposer.setGlobalSpeed(glSpeed - 0.1f);
               
           } else if (name.equals("ToggleArmature")) {
               armatureDebugState.setEnabled(!armatureDebugState.isEnabled());
           }
       }
   };

}
