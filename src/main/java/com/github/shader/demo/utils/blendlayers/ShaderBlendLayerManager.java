/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.shader.demo.utils.blendlayers;

import com.jme3.app.Application;
import com.jme3.app.state.BaseAppState;
import com.jme3.material.Material;
import com.jme3.scene.Spatial;
import java.util.ArrayList;

/**
 *
 * @author ryan
 */
public class ShaderBlendLayerManager extends BaseAppState{

    
    private final ArrayList<BlendGroup> registeredBlendGroups;
    
    
    
    
        
    public ShaderBlendLayerManager(){
        this.registeredBlendGroups = new ArrayList<>();
    
    }
    
    public void unregisterLayer(BlendGroup blendGroup, ShaderBlendLayer blendLayer){
        blendGroup.removeBlendLayer(blendLayer);
    }    

    public void registerTemporaryLayerToModel(ShaderBlendLayer blendLayer, BlendGroup blendGroup){
        if(!registeredBlendGroups.contains(blendGroup)){
            registeredBlendGroups.add(blendGroup);
        }
        
        //to-do: this currently just adds the most recently added layer ontop of all others. but eventually check priority & duration here to determine where each new
        //        layer should go and dynamically place it in the correct index
        int lastIndex = blendGroup.getCurrentLayerCount();
        blendGroup.setBlendLayer(lastIndex, blendLayer);
    }
    
    public void registerReservedLayerToModel(ShaderBlendLayer blendLayer, BlendGroup blendGroup, int layerIndex){
        if(!registeredBlendGroups.contains(blendGroup)){
            registeredBlendGroups.add(blendGroup);
        }
        blendGroup.setBlendLayer(layerIndex, blendLayer);
    }
    
    

    @Override
    public void update(float tpf) {
        super.update(tpf);
        for(BlendGroup blendGroup : registeredBlendGroups){
            if(blendGroup != null){
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
