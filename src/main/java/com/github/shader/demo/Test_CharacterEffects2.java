package com.github.shader.demo;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.LinkedList;
import java.util.Queue;

import com.github.shader.demo.states.BlendEffectState;
import com.github.shader.demo.states.DefaultSceneState;
import com.github.shader.demo.utils.GameObject;
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
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.custom.ArmatureDebugAppState;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.style.BaseStyles;

/**
 * 
 * @author capdevon
 */
public class Test_CharacterEffects2 extends SimpleApplication {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Test_CharacterEffects2 app = new Test_CharacterEffects2();
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

    @Override
    public void simpleInitApp() {
        armatureDebugState = new ArmatureDebugAppState();
        stateManager.attach(armatureDebugState);

        animUI = makeTextUI("CurrentAction:", ColorRGBA.Blue);
        animUI.setLocalTranslation(10f, settings.getHeight() - 10f, 0);

        initLemur();
        configureCamera();
        setupCamera();
        loadModel();
        initKeys();

        stateManager.attach(new DefaultSceneState());
        stateManager.attach(new BlendEffectState(model));
        // stateManager.attach(new DebugGridState());
        // stateManager.attach(new DetailedProfilerState());
    }
    
    private void initLemur() {
        // initialize lemur
        GuiGlobals.initialize(this);
        BaseStyles.loadGlassStyle();
        GuiGlobals.getInstance().getStyles().setDefaultStyle("glass");
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

        animComposer = GameObject.getComponentInChildren(model, AnimComposer.class);
        animsQueue.addAll(animComposer.getAnimClipsNames());
        animsQueue.forEach(System.out::println);

        skinningControl = GameObject.getComponentInChildren(model, SkinningControl.class);
        armatureDebugState.addArmatureFrom(skinningControl);

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

}