package com.github.shader.pack;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Grid;

/**
 * @author capdevon
 */
public class DebugGridState extends BaseAppState implements ActionListener {
    
    private static final String TOGGLE_DEBUG_GRID = "TOGGLE_DEBUG_GRID";
    
    private InputManager inputManager;
    private ViewPort viewPort;
    private Node debugNode = new Node("DebugNode");
    private boolean showGrid = false;
    
    @Override
    protected void initialize(Application app) {
        this.inputManager = app.getInputManager();
        this.viewPort = app.getViewPort();
        
        createGrid(app.getAssetManager());
    }

    @Override
    protected void cleanup(Application app) {
    }

    @Override
    protected void onEnable() {
        setShowGrid(true);
        inputManager.addMapping(TOGGLE_DEBUG_GRID, new KeyTrigger(KeyInput.KEY_P));
        inputManager.addListener(this, TOGGLE_DEBUG_GRID);
    }

    @Override
    protected void onDisable() {
        setShowGrid(false);
        inputManager.deleteMapping(TOGGLE_DEBUG_GRID);
        inputManager.removeListener(this);
    }

    private void createGrid(AssetManager assetManager) {
        Geometry geo = new Geometry("DebugGrid", new Grid(21, 21, 1));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        geo.setMaterial(mat);
        geo.center().move(0, 0, 0);
        debugNode.attachChild(geo);
    }

    private void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }
    
    @Override
    public void render(RenderManager rm) {
        if (showGrid) {
            debugNode.updateLogicalState(0);
            debugNode.updateGeometricState();
            rm.renderScene(debugNode, viewPort);
        }
    }
    
    @Override
    public void onAction(String action, boolean keyPressed, float tpf) {
        if (action.equals(TOGGLE_DEBUG_GRID) && keyPressed) {
            setShowGrid(!showGrid);
        }
    }

}
