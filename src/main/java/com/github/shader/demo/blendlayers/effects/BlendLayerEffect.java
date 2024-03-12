package com.github.shader.demo.blendlayers.effects;

import com.github.shader.demo.blendlayers.ShaderBlendLayer;

/**
 *
 * @author ryan
 */
public abstract class BlendLayerEffect {

    public ShaderBlendLayer affectedLayer = null;
    private String name;
    private boolean removeLayerOnFinish = false;
    private float duration;

    public float timeElapsed;
    private boolean isInfinite = false;
    private boolean finished = false;

    public BlendLayerEffect(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public void setAffectedLayer(ShaderBlendLayer affectedLayer) {
        this.affectedLayer = affectedLayer;
    }

    public void setRemoveLayerOnFinish(boolean removeLayerOnFinish) {
        this.removeLayerOnFinish = removeLayerOnFinish;
    }

    public boolean isRemoveLayerOnFinish() {
        return removeLayerOnFinish;
    }

    public void setIsInfinite(boolean isInfinite) {
        this.isInfinite = isInfinite;
    }

    public boolean isInfinite() {
        return isInfinite;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public float getDuration() {
        return duration;
    }

    public void update(float tpf) {
        timeElapsed += tpf;

        if (!isInfinite) {
            if (timeElapsed > duration) {
                finished = true;
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void cancel() {
        finished = true;
    }

    public void onFinish() {

    }

}
