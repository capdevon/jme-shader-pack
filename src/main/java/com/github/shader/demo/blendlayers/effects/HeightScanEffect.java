package com.github.shader.demo.blendlayers.effects;

import com.github.shader.demo.blendlayers.ShaderBlendLayer;

/**
 *
 * @author ryan
 */
public class HeightScanEffect extends BlendLayerEffect {

    float scanSpeed = 1.0f;
    float scanWidth = 0.1f;
    
    float scanInterval = 2.0f;
    public boolean isCycleMode = false;
    private float currentUpperHeightVal;
    private boolean currentlyInReverse = false;
    
    public HeightScanEffect(String name) {
        super(name);
    }

    public HeightScanEffect() {
        super("Height_Scan");
        setDuration(8.0f);
    }

    public float getScanWidth() {
        return scanWidth;
    }
    
    public void setScanWidth(float scanWidth) {
        this.scanWidth = scanWidth;
    }

    public float getScanSpeed() {
        return scanSpeed;
    }
    
    public void setScanSpeed(float scanSpeed) {
        this.scanSpeed = scanSpeed;
    }

    public float getScanInterval() {
        return scanInterval;
    }
    
    public void setScanInterval(float scanInterval) {
        this.scanInterval = scanInterval;
    }

    public boolean isIsCycleMode() {
        return isCycleMode;
    }

    public void setIsCycleMode(boolean isCycleMode) {
        this.isCycleMode = isCycleMode;
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        float frameScanAmount = tpf * scanSpeed;

        if (currentlyInReverse) {
            frameScanAmount *= -1.0f;
        }

        currentUpperHeightVal += frameScanAmount;

        float currentLowerHeightVal = currentUpperHeightVal - scanWidth;

        if (isCycleMode && currentUpperHeightVal < 0) {
            currentlyInReverse = !currentlyInReverse;
        } else if (currentLowerHeightVal >= 1.0f) {

            if (isCycleMode) {
                currentlyInReverse = !currentlyInReverse;
            } else {
                currentUpperHeightVal = 0.0f;
            }
        }

        if (affectedLayer != null) {
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
