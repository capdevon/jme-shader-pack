package com.github.tools.material;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.tools.SpinnerFloatModel;
import com.github.tools.SpinnerModel;

/**
 * 
 * @author capdevon
 */
public abstract class MatConstraints {

    private static final Map<String, SpinnerModel> pbr = new HashMap<>();
    static {
        pbr.put("NormalType", new SpinnerFloatModel(-1f, 1f, 0.01f));
        pbr.put("EmissivePower", new SpinnerFloatModel(0f, 3f, 0.01f));
        pbr.put("EmissiveIntensity", new SpinnerFloatModel(0f, 2f, 0.01f));
        pbr.put("Roughness", new SpinnerFloatModel(0f, 1f, 0.01f));
        pbr.put("Metallic", new SpinnerFloatModel(0f, 1f, 0.01f));
        pbr.put("Glossiness", new SpinnerFloatModel(0f, 1f, 0.01f));
        pbr.put("ParallaxHeight", new SpinnerFloatModel(0f, 1f, 0.01f));
        pbr.put("AlphaDiscardThreshold", new SpinnerFloatModel(0f, 1f, 0.01f));
    }
    
    public static final Map<String, SpinnerModel> getPBRConstraints() {
        return Collections.unmodifiableMap(pbr);
    }

}
