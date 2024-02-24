package com.github.shader.demo.utils;

import java.util.ArrayList;
import java.util.List;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;

/**
 *
 * @author ryan
 */
public class BlendLayerEffect {

    private String name;
    private int debugMode = -1;
    private int layerIndex = -1;
    private String blendLayerPrefixedName;
    private final Vector4f blendVec = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);
    private final List<Material> materials = new ArrayList<>();
    private Texture baseColorMap;
    private Texture normalMap;
    private Texture metallicRoughnessAoMap;
    private Texture emissiveMap;
    private boolean triplanar = true;
    
    private float heighScanUpperVal = 1f;
    private float heightScanLowerVal = 0f;
    private float dissolveBlendVal = 1f;
    private float linearBlendVal = 0f;
    private float emissiveIntensity = 0f;
    private final ColorRGBA emissiveColor = new ColorRGBA();
    
    private boolean blendAlpha = false;
    private final ColorRGBA baseColor = new ColorRGBA();

    /**
     * Creates a BlendLayerEffect.
     * 
     * @param name
     * @param layerIndex
     * @param spatial
     */
    public BlendLayerEffect(String name, int layerIndex, Spatial spatial) {
        this.name = name;
        setLayerIndex(layerIndex);
        addMaterialsFromSpatial(spatial);
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private void clearLayer() {
        for (Material mat : materials) {
            for (MatParam matParam : mat.getParams()) {
                if (matParam.getName().startsWith(blendLayerPrefixedName)) {
                    mat.clearParam(matParam.getName());
                }
            }
        }
    }

    private void setLayerIndex(int layerIndex) {
        if (layerIndex != -1) {
            clearLayer();
        }

        this.layerIndex = layerIndex;
        this.blendLayerPrefixedName = "BlendLayer_" + layerIndex;

        for (Material mat : materials) {
            mat.setVector4(blendLayerPrefixedName + "_BlendVec", blendVec);
        }
    }

    private void addMaterialsFromSpatial(Spatial spatial) {
        spatial.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                registerMaterial(geom.getMaterial());
            }
        });
    }
    
    private void registerMaterial(Material mat) {
        String paramName = blendLayerPrefixedName + "_BlendVec";
        // detect if the material's matDef is valid and has support for the blend layer
        if (mat.getMaterialDef().getMaterialParam(paramName) != null) {
            if (!materials.contains(mat)) {
                materials.add(mat);
                mat.setVector4(paramName, blendVec);
            }
        }
    }
    
    public boolean isTriplanar() {
        return triplanar;
    }

    public void setTriplanar(boolean triplanar) {
        this.triplanar = triplanar;
        setParam(blendLayerPrefixedName + "_TriPlanar", VarType.Boolean, triplanar);
        
        if (baseColorMap != null) {
            baseColorMap.setWrap(Texture.WrapMode.Repeat);
        }
        if (normalMap != null) {
            normalMap.setWrap(Texture.WrapMode.Repeat);
        }
        if (metallicRoughnessAoMap != null) {
            metallicRoughnessAoMap.setWrap(Texture.WrapMode.Repeat);
        }
        if (emissiveMap != null) {
            emissiveMap.setWrap(Texture.WrapMode.Repeat);
        }
    }

    public void setParam(String name, VarType varType, Object value) {
        for (Material mat : materials) {
            
            if (value == null) {
                mat.clearParam(name);
                continue;
            }
            
            switch (varType) {
                case Boolean:
                    Boolean bool = (Boolean) value;
                    if (!bool) {
                        mat.clearParam(name);
                    } else {
                        mat.setParam(name, varType, value);
                    }
                    break;
                    
                case Texture2D:
                    if (triplanar) {
                        Texture texture = (Texture) value;
                        texture.setWrap(Texture.WrapMode.Repeat);
                    }
                    mat.setParam(name, varType, value);
                    break;
                    
                default:
                    mat.setParam(name, varType, value);
            }
        }
    }
    
    public void setBaseColorMap(Texture texture) {
        baseColorMap = texture;
        setParam(blendLayerPrefixedName + "_BaseColorMap", VarType.Texture2D, texture);
    }

    public void setNormalMap(Texture texture) {
        normalMap = texture;
        setParam(blendLayerPrefixedName + "_NormalMap", VarType.Texture2D, texture);
    }

    public void setMetallicRoughnessAoMap(Texture texture) {
        metallicRoughnessAoMap = texture;
        setParam(blendLayerPrefixedName + "_MetallicRoughnessAoMap", VarType.Texture2D, texture);
    }

    public void setEmissiveMap(Texture texture) {
        emissiveMap = texture;
        setParam(blendLayerPrefixedName + "_EmissiveMap", VarType.Texture2D, texture);
    }

    public boolean isBlendAlpha() {
        return blendAlpha;
    }

    public void setBlendAlpha(boolean blendAlpha) {
        this.blendAlpha = blendAlpha;
        this.setParam(blendLayerPrefixedName + "_BlendAlpha", VarType.Boolean, blendAlpha);
    }
    
    public float getEmissiveIntensity() {
        return emissiveIntensity;
    }

    public void setEmissiveIntensity(float emissiveIntensity) {
        this.emissiveIntensity = emissiveIntensity;
        this.setParam(blendLayerPrefixedName + "_EmissiveIntensity", VarType.Float, emissiveIntensity);
    }

    public ColorRGBA getEmissiveColor() {
        return emissiveColor;
    }
    
    public void setEmissiveColor(ColorRGBA emissiveColor) {
        this.emissiveColor.set(emissiveColor);
        this.setParam(blendLayerPrefixedName + "_EmissiveColor", VarType.Vector4, emissiveColor);
    }
    
    public ColorRGBA getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(ColorRGBA baseColor) {
        this.baseColor.set(baseColor);
        this.setParam(blendLayerPrefixedName + "_BaseColor", VarType.Vector4, baseColor);
    }
    
    public int getDebugMode() {
        return debugMode;
    }
    
    /**
     * The specified value must be between -1 and 5
     * @param debugMode
     */
    public void setDebugMode(int debugMode) {
        this.debugMode = debugMode;
        setParam("DebugValuesMode", VarType.Int, debugMode);
    }

    public float getLinearBlendVal() {
        return linearBlendVal;
    }

    public void setLinearBlendVal(float linearBlendVal) {
        this.linearBlendVal = linearBlendVal;
        blendVec.x = linearBlendVal;
    }
    
    public float getHeightScanLowerVal() {
        return heightScanLowerVal;
    }
    
    public void setHeightScanLowerVal(float heightScanLowerVal) {
        this.heightScanLowerVal = heightScanLowerVal;
        blendVec.y = heightScanLowerVal;
    }

    public float getHeighScanUpperVal() {
        return heighScanUpperVal;
    }

    public void setHeighScanUpperVal(float heighScanUpperVal) {
        this.heighScanUpperVal = heighScanUpperVal;
        blendVec.z = heighScanUpperVal;
    }

    public float getDissolveBlendVal() {
        return dissolveBlendVal;
    }

    public void setDissolveBlendVal(float dissolveBlendVal) {
        this.dissolveBlendVal = dissolveBlendVal;
        blendVec.w = dissolveBlendVal;
    }
}
