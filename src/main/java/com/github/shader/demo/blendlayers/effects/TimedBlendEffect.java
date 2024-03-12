/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.shader.demo.utils.blendlayers.effects;

/**
 *
 * @author ryan
 */
public class TimedBlendEffect extends BlendLayerEffect{
    
    public float fadeInTime = 4.0f;
    public float fadeOutTime = 2.0f;    

    public float getFadeOutTime() {        return fadeOutTime;    }
    public float getFadeInTime() {        return fadeInTime;    }
    public void setFadeOutTime(float fadeOutTime) {        this.fadeOutTime = fadeOutTime;    }
    public void setFadeInTime(float fadeInTime) {        this.fadeInTime = fadeInTime;    }
    
    public boolean isDissolve;
    public boolean isLinear = true;
    public boolean isHeightScan = false;

    public boolean isIsHeightScan() {        return isHeightScan;    }
    public boolean isIsDissolve() {        return isDissolve;    }
    public boolean isIsLinear() {        return isLinear;    }

    public void setIsLinear(boolean isLinear) {        this.isLinear = isLinear;    }
    public void setIsHeightScan(boolean isHeightScan) {        this.isHeightScan = isHeightScan;    }
    public void setIsDissolve(boolean isDissolve) {        this.isDissolve = isDissolve;    }
    
    public TimedBlendEffect(String name) {
        super(name);
    }
    
    public TimedBlendEffect() {
        super("Timed_Blend");
        setDuration(8.0f);
    }
    
    @Override
    public void update(float tpf) {
        super.update(tpf); 
        float blendPct;
        if(timeElapsed < fadeInTime){
            blendPct = timeElapsed /fadeInTime;
        }else if(timeElapsed > getDuration() - fadeOutTime){
            float timeLeft = getDuration() - timeElapsed;
            blendPct = (timeLeft / fadeOutTime);
        }else{
            blendPct = 1.0f;
        }
        if(affectedLayer != null){
            if(isLinear){
                affectedLayer.setLinearBlendVal(blendPct);
            }
            else{
                affectedLayer.setLinearBlendVal(1.0f);
            }
            if(isDissolve){
                affectedLayer.setDissolveBlendVal(blendPct);
            }else{
                affectedLayer.setDissolveBlendVal(1.0f);
            }
            if(isHeightScan){
                affectedLayer.setHeighScanUpperVal(blendPct);
            }else{
                affectedLayer.setHeighScanUpperVal(1.0f);
            }
        }
        
    }
}
