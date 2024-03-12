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
public class HeightScanEffect extends BlendLayerEffect{
    
    float scanSpeed = 1.0f;
    float scanWidth = 0.1f;
    
    

    public float getScanWidth() {        return scanWidth;    }
    public float getScanSpeed() {        return scanSpeed;    }
    public float getScanInterval() {        return scanInterval;    }

    public void setScanWidth(float scanWidth) {
        this.scanWidth = scanWidth;
    }

    public void setScanSpeed(float scanSpeed) {
        this.scanSpeed = scanSpeed;
    }

    public void setScanInterval(float scanInterval) {
        this.scanInterval = scanInterval;
    }
    
    float scanInterval = 2.0f;
    
    public HeightScanEffect(String name) {
        super(name);
    }
    
    public HeightScanEffect() {
        super("Height_Scan");
        setDuration(8.0f);
    }
    
    public boolean isCycleMode = false;
    public boolean isIsCycleMode() {        return isCycleMode;    }
    public void setIsCycleMode(boolean isCycleMode) {        this.isCycleMode = isCycleMode;    }
    
    private float currentUpperHeightVal;
    private boolean currentlyInReverse = false;
    
    @Override
    public void update(float tpf) {
        super.update(tpf); 
        
        float frameScanAmount = tpf * scanSpeed;
        
        if(currentlyInReverse){
            frameScanAmount *= -1.0f;
        }
        
        currentUpperHeightVal += frameScanAmount;
        
        float currentLowerHeightVal = currentUpperHeightVal - scanWidth;
        
        if(isCycleMode && currentUpperHeightVal < 0){
            currentlyInReverse = !currentlyInReverse;
        }
        else if(currentLowerHeightVal >= 1.0f){
            
            if(isCycleMode){
                currentlyInReverse = !currentlyInReverse;
            }else{
                currentUpperHeightVal = 0.0f;
            }
        }
        
        
        if(affectedLayer != null){
            affectedLayer.setHeightScanLowerVal(currentLowerHeightVal);
            affectedLayer.setHeighScanUpperVal(currentUpperHeightVal);
        }
        




    }
    
    @Override
    public void setAffectedLayer(ShaderBlendLayer affectedLayer) {
        super.setAffectedLayer(affectedLayer);
        affectedLayer.setLinearBlendVal(1.0f);
        affectedLayer.setDissolveBlendVal(1.0f);    
    }
    
    @Override
    public void onFinish() {
        super.onFinish();
        
        affectedLayer.setHeighScanUpperVal(0.0f);
        affectedLayer.setHeightScanLowerVal(0.0f);
        
    }
    
}
