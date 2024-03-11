package com.github.shader.demo.utils.blendlayers;

import com.github.shader.demo.utils.blendlayers.effects.BlendLayerEffect;
import java.util.ArrayList;
import java.util.List;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
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
public class ShaderBlendLayer {

    private String name;
    private int debugMode = -1;
    private int layerIndex = -1;
    private String blendLayerPrefixedName;
    private final Vector4f blendVec = new Vector4f(0.0f, 0.0f, 1.0f, 1.0f);
    private List<Material> materials = new ArrayList<>();
    private Texture baseColorMap;
    private Texture normalMap;
    private Texture metallicRoughnessAoMap;
    private Texture emissiveMap;
    private float scale = 1.0f;
    private boolean triplanar = true;
    
    private float heighScanUpperVal = 1f;
    private float heightScanLowerVal = 0f;
    private float dissolveBlendVal = 1f;
    private float linearBlendVal = 0f;
    private float emissiveIntensity = 0f;
    private final ColorRGBA emissiveColor = new ColorRGBA();
    
    private boolean blendAlpha = false;
    private final ColorRGBA baseColor = new ColorRGBA(1.0f,1.0f,1.0f,1.0f);
    
    private final Vector4f hsvScalar = new Vector4f();    
    private float hueScalar = 0.0f; //h
    private float saturationScalar = 0.0f;  //s
    private float brightnessScalar = 0.0f;  //v
    
    private float edgeFadeThickness;
    
    
    private Vector2f dissolveNoiseVal0 = new Vector2f(0.0f, 0.0f), dissolveNoiseVal1 = new Vector2f(0.0f, 0.0f), dissolveNoiseVal2 = new Vector2f(0.0f, 0.0f);    

    


    private ColorRGBA edgeFadeColorGradientStart = new ColorRGBA(0.0f,0.0f,0.0f,0.0f), edgeFadeColorGradientEnd = new ColorRGBA(0.0f,0.0f,0.0f,0.0f);
    
    private final ArrayList<BlendLayerEffect> activeEffects = new ArrayList<>();

    /**
     * Creates a BlendLayerEffect.
     * 
     * @param name
     * @param layerIndex
     * @param spatial
     */
    
    @Deprecated
    public ShaderBlendLayer(String name, int layerIndex, Spatial spatial) { //should use the ShaderBlendLayerManager to register BlendLayers and let that handle the layer indexing
        this.name = name;
        setLayerIndex(layerIndex);
        addMaterialsFromSpatial(spatial);
    }
    
    public ShaderBlendLayer(String name) {
        this.name = name;
    }    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLayerIndex() {
        return layerIndex;
    }
    
    public void registerEffect(BlendLayerEffect effect) {
        if(!activeEffects.contains(effect)){
            activeEffects.add(effect);
            effect.setAffectedLayer(this);
        }
    }

    public void removeEffect(BlendLayerEffect effect) {
        
    }
    
    public void update(float tpf){
        for(int e = 0; e < activeEffects.size(); e++){
            BlendLayerEffect effect = activeEffects.get(e);
            if(effect != null){
                effect.update(tpf);
           
            
                if(effect.isFinished()){
                    activeEffects.remove(effect);
                    e--;
                    
                    effect.onFinish();

                    if(effect.isRemoveLayerOnFinish()){
                        clearLayer();
                    }
                }
            }
        }
    }

    public void clearLayer() {
        
        ArrayList<String> paramNamesToClear = new ArrayList<>();
        for (Material mat : materials) {
            for (MatParam matParam : mat.getParams()) {
                if (matParam.getName().startsWith(blendLayerPrefixedName)) {
                    paramNamesToClear.add(matParam.getName());
                }
            }
            for (String paramNameToClear : paramNamesToClear) {
                mat.clearParam(paramNameToClear);
            }
        }
    }

    public void setLayerIndex(int layerIndex) {
        if (layerIndex != -1 && layerIndex != this.layerIndex) {
            clearLayer();
        }
        this.layerIndex = layerIndex;
        blendLayerPrefixedName = "BlendLayer_" + layerIndex;

        for (Material mat : materials) {
            this.applyParamsToMat(mat);
        }
    }

    public void addMaterialsFromSpatial(Spatial spatial) {
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
                this.applyParamsToMat(mat);
            }
        }
    }
    
    private void applyParamsToMat(Material material){
        
        //note that the non-primitive types only need set as a param to the material once here, and the param is intentionally not set again for some setters in this class
        
        material.setVector4(blendLayerPrefixedName + "_BlendVec", blendVec);
        material.setFloat(blendLayerPrefixedName + "_Scale", scale);
        
        material.setTexture(blendLayerPrefixedName + "_BaseColorMap", baseColorMap);
        material.setTexture(blendLayerPrefixedName + "_NormalMap", normalMap);
        material.setTexture(blendLayerPrefixedName + "_MetallicRoughnessAoMap", metallicRoughnessAoMap);
        material.setTexture(blendLayerPrefixedName + "_EmissiveMap", emissiveMap);

        material.setParam(blendLayerPrefixedName + "_BaseColor", VarType.Vector4, baseColor);
        material.setParam(blendLayerPrefixedName + "_EmissiveColor", VarType.Vector4, emissiveColor);
        material.setParam(blendLayerPrefixedName + "_BlendAlpha", VarType.Boolean, blendAlpha);
        material.setParam(blendLayerPrefixedName + "_EmissiveIntensity", VarType.Float, emissiveIntensity);
        
        material.setParam(blendLayerPrefixedName + "_HSVScalar", VarType.Vector4, hsvScalar);
        
        material.setParam(blendLayerPrefixedName + "_EdgeFadeThickness", VarType.Float, edgeFadeThickness);
        material.setParam(blendLayerPrefixedName + "_EdgeFadeColorA", VarType.Vector4, edgeFadeColorGradientStart);
        material.setParam(blendLayerPrefixedName + "_EdgeFadeColorB", VarType.Vector4, edgeFadeColorGradientEnd);
        
        material.setParam(blendLayerPrefixedName + "_TriPlanar", VarType.Boolean, triplanar);
        
        material.setParam(blendLayerPrefixedName + "_IsMultiplicative", VarType.Boolean, multiplicative); 
        
        //to-do: set all other possible matParams here, so this method can be called when a new material is added to an existent layer, and for when layers are swapped and need matParams reapplied to a differnet layerIndex
        
         setTexturesWrappable(triplanar);
         setDissolveNoiseVal0(dissolveNoiseVal0);
         setDissolveNoiseVal1(dissolveNoiseVal1);
         setDissolveNoiseVal2(dissolveNoiseVal2);
        
    }
    
    public boolean isTriplanar() {
        return triplanar;
    }

    public void setTriplanar(boolean triplanar) {
        this.triplanar = triplanar;
        setParam(blendLayerPrefixedName + "_TriPlanar", VarType.Boolean, triplanar);
        
        setTexturesWrappable(triplanar);

    }
    
    private void setTexturesWrappable(boolean wrap){
        if (baseColorMap != null) {
            if(wrap){
                baseColorMap.setWrap(Texture.WrapMode.Repeat);
            }
            else{
                baseColorMap.setWrap(Texture.WrapMode.EdgeClamp); //is this the correct way to clear wrap.repeat mode?
            }            
        }
        if (normalMap != null) {
            if(wrap){
                normalMap.setWrap(Texture.WrapMode.Repeat);
            }
            else{
                normalMap.setWrap(Texture.WrapMode.EdgeClamp);
            }
        }
        if (metallicRoughnessAoMap != null) {
            if(wrap){
                metallicRoughnessAoMap.setWrap(Texture.WrapMode.Repeat);
            }
            else{
                metallicRoughnessAoMap.setWrap(Texture.WrapMode.EdgeClamp);
            }
        }     
        if (emissiveMap != null) {
            if(wrap){
                emissiveMap.setWrap(Texture.WrapMode.Repeat);
            }
            else{
                emissiveMap.setWrap(Texture.WrapMode.EdgeClamp);
            }
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
    
    

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
        setParam(blendLayerPrefixedName + "_Scale", VarType.Float, scale);
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
    }
    
    public ColorRGBA getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(ColorRGBA baseColor) {
        this.baseColor.set(baseColor);
    }    

    public float getSaturationScalar() {
        return saturationScalar;
    }

    public float getBrightnessScalar() {
        return brightnessScalar;
    }

    public void setBrightnessScalar(float brightnessScalar) {
        this.brightnessScalar = brightnessScalar;
        hsvScalar.setZ(brightnessScalar);
    }

    public void setSaturationScalar(float saturationScalar) {
        this.saturationScalar = saturationScalar;
        hsvScalar.setY(saturationScalar);
    }

    public void setHueScalar(float hueScalar) {
        this.hueScalar = hueScalar;
        hsvScalar.setX(hueScalar);
    }

    public float getHueScalar() {
        return hueScalar;
    }
    public Vector4f getHsvScalar() {
        return hsvScalar;
    }   
    public ColorRGBA getEdgeFadeColorGradientStart() {
        return edgeFadeColorGradientStart;
    }
    public ColorRGBA getEdgeFadeColorGradientEnd() {
        return edgeFadeColorGradientEnd;
    }
    
    public void setDissolveNoiseVal2(Vector2f dissolveNoiseVal2) {
        this.dissolveNoiseVal2 = dissolveNoiseVal2;
        if(dissolveNoiseVal2.x == 0.0f || dissolveNoiseVal2.y == 0.0f){
            this.setParam(this.blendLayerPrefixedName + "_DissolveNoiseVec_2", VarType.Vector2, null);
        }else{
            this.setParam(this.blendLayerPrefixedName + "_DissolveNoiseVec_2", VarType.Vector2, dissolveNoiseVal2);
        }  
        
    }

    public void setDissolveNoiseVal1(Vector2f dissolveNoiseVal1) {
        this.dissolveNoiseVal1 = dissolveNoiseVal1;
        if(dissolveNoiseVal1.x == 0.0f || dissolveNoiseVal1.y == 0.0f){
            this.setParam(this.blendLayerPrefixedName + "_DissolveNoiseVec_1", VarType.Vector2, null);
        }else{
            this.setParam(this.blendLayerPrefixedName + "_DissolveNoiseVec_1", VarType.Vector2, dissolveNoiseVal1);
        }  
    }

    public void setDissolveNoiseVal0(Vector2f dissolveNoiseVal0) {
        this.dissolveNoiseVal0 = dissolveNoiseVal0;
        if(dissolveNoiseVal0.x == 0.0f || dissolveNoiseVal0.y == 0.0f){
            this.setParam(this.blendLayerPrefixedName + "_DissolveNoiseVec_0", VarType.Vector2, null);
        }else{
            this.setParam(this.blendLayerPrefixedName + "_DissolveNoiseVec_0", VarType.Vector2, dissolveNoiseVal0);
        }        
    }

    public Vector2f getDissolveNoiseVal2() {
        return dissolveNoiseVal2;
    }

    public Vector2f getDissolveNoiseVal1() {
        return dissolveNoiseVal1;
    }

    public Vector2f getDissolveNoiseVal0() {
        return dissolveNoiseVal0;
    }
    
    public void setEdgeFadeThickness(float edgeFadeThickness) {
        this.edgeFadeThickness = edgeFadeThickness;
        this.setParam(blendLayerPrefixedName + "_EdgeFadeThickness", VarType.Float, edgeFadeThickness);
        
        this.setDissolveBlendVal(dissolveBlendVal); //important to call these so that they are put into proper range accounting for new edgeFadeThickness
        this.setHeighScanUpperVal(heighScanUpperVal);
        this.setHeightScanLowerVal(heightScanLowerVal);
    }

    public float getEdgeFadeThickness() {
        return edgeFadeThickness;
    }
    
    public void setEdgeFadeColorGradientStart(ColorRGBA edgeFadeColorGradientStart) {
        this.edgeFadeColorGradientStart.set(edgeFadeColorGradientStart);
    }

    public void setEdgeFadeColorGradientEnd(ColorRGBA edgeFadeColorGradientEnd) {
        this.edgeFadeColorGradientEnd.set(edgeFadeColorGradientEnd);
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
        heightScanLowerVal = FastMath.interpolateLinear(heightScanLowerVal, 0.0f, 1.0f + edgeFadeThickness);
        this.heightScanLowerVal = heightScanLowerVal;
        blendVec.y = heightScanLowerVal;
    }

    public float getHeighScanUpperVal() {
        return heighScanUpperVal;
    }

    public void setHeighScanUpperVal(float heighScanUpperVal) {
        heighScanUpperVal = FastMath.interpolateLinear(heighScanUpperVal, 0.0f - edgeFadeThickness, 1.0f);
        this.heighScanUpperVal = heighScanUpperVal;
        blendVec.z = heighScanUpperVal;
    }

    public float getDissolveBlendVal() {
        return dissolveBlendVal;
    }

    public void setDissolveBlendVal(float dissolveBlendVal) {
        dissolveBlendVal = FastMath.interpolateLinear(dissolveBlendVal, 0.0f, 1.0f + edgeFadeThickness); //account for edgeFadeThickness so that edgeFade doesn't bleed over at 0.0 and 1.0 blend values
        this.dissolveBlendVal = dissolveBlendVal;
        blendVec.w = dissolveBlendVal;
    }

    public void setMaterials(ArrayList<Material> materials) {
        for(Material mat : materials){
            this.registerMaterial(mat);
        }
    
    }

    public void clearMaterials() {    
        clearLayer();
        materials.clear();
    }

    public void setHsvScalar(Vector4f hsvScalar) {
        this.hsvScalar.set(hsvScalar);
    }
    
    
    private boolean multiplicative = false;
    public boolean isMultiplicative() {
        return multiplicative;
    }
    public void setMultiplicative(boolean multiplicative) {
        this.multiplicative = multiplicative;
        this.setParam(blendLayerPrefixedName + "_IsMultiplicative", VarType.Boolean, multiplicative);   
    }


}
