package com.github.shader.demo.blendlayers;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;

/**
 *
 * @author ryan
 */
public class ShaderBlendLayerManager extends BaseAppState {

    private final List<BlendGroup> registeredBlendGroups;

    public ShaderBlendLayerManager() {
        this.registeredBlendGroups = new ArrayList<>();
    }

    public void unregisterLayer(BlendGroup blendGroup, ShaderBlendLayer blendLayer) {
        blendGroup.removeBlendLayer(blendLayer);
    }

    public void registerTemporaryLayerToModel(ShaderBlendLayer blendLayer, BlendGroup blendGroup) {
        if (!registeredBlendGroups.contains(blendGroup)) {
            registeredBlendGroups.add(blendGroup);
        }

        // to-do: this currently just adds the most recently added layer ontop of all
        // others. but eventually check priority & duration here to determine where each
        // new
        // layer should go and dynamically place it in the correct index
        int lastIndex = blendGroup.getCurrentLayerCount();
        blendGroup.setBlendLayer(lastIndex, blendLayer);
    }

    public void registerReservedLayerToModel(ShaderBlendLayer blendLayer, BlendGroup blendGroup, int layerIndex) {
        if (!registeredBlendGroups.contains(blendGroup)) {
            registeredBlendGroups.add(blendGroup);
        }
        blendGroup.setBlendLayer(layerIndex, blendLayer);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);
        for (BlendGroup blendGroup : registeredBlendGroups) {
            if (blendGroup != null) {
                blendGroup.update(tpf);
            }
        }
    }

    @Override
    protected void initialize(Application aplctn) {
    }

    @Override
    protected void cleanup(Application aplctn) {
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
    }

}
