package com.github.shader.demo.states;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.InputManager;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

/**
 * 
 * @author capdevon
 */
public abstract class MySimpleState extends BaseAppState {
    
    public AssetManager assetManager;
    public InputManager inputManager;
    public Camera cam;
    public ViewPort viewPort;
    public AppSettings settings;
    public Node rootNode;
    public Node guiNode;

    @Override
    protected void initialize(Application app) {
        refreshCacheFields((SimpleApplication) app);
    }

    /**
     * @param app
     */
    private void refreshCacheFields(SimpleApplication app) {
        this.settings     = app.getContext().getSettings();
        this.assetManager = app.getAssetManager();
        this.inputManager = app.getInputManager();
        this.cam          = app.getCamera();
        this.viewPort     = app.getViewPort();
        this.rootNode     = app.getRootNode();
        this.guiNode      = app.getGuiNode();
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
    
    public BitmapText makeBitmapText(String text, ColorRGBA color) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText bmp = new BitmapText(font);
        bmp.setName("MyLabel");
        bmp.setText(text);
        bmp.setColor(color);
        bmp.setSize(font.getCharSet().getRenderedSize());
        return bmp;
    }
    
}
