/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.shader.demo.utils.blendlayers.effects;

import com.github.shader.demo.utils.blendlayers.ShaderBlendLayer;

/**
 *
 * @author ryan
 */
public abstract class BlendLayerEffect {
    
    public ShaderBlendLayer affectedLayer = null;
    public void setAffectedLayer(ShaderBlendLayer affectedLayer) {        this.affectedLayer = affectedLayer;    }
    
    private boolean removeLayerOnFinish = false;
    public void setRemoveLayerOnFinish(boolean removeLayerOnFinish) {        this.removeLayerOnFinish = removeLayerOnFinish;  }
    public boolean isRemoveLayerOnFinish() {        return removeLayerOnFinish;    }
    
    private String name;
    public String getName() {        return name;    }
    
    private float duration;
    private boolean isInfinite = false;

    public void setIsInfinite(boolean isInfinite) {        this.isInfinite = isInfinite;    }
    public boolean isInfinite() {        return isInfinite;    }

    public void setDuration(float duration) {        this.duration = duration;    }
    public float getDuration() {        return duration;    }

    public float timeElapsed;
    
    public BlendLayerEffect(String name) {
        this.name = name;
    }
    
    public void update(float tpf){
        timeElapsed += tpf;
        
        if(!isInfinite){
            if(timeElapsed > duration){
               finished = true;
            }
        }
    }

    private boolean finished = false;
    public boolean isFinished() { return finished; }
    public void setFinished(boolean finished) {        this.finished = finished;    }

    public void cancel() {
        finished = true;
    }

    public void onFinish() {
    
    }
    
}
