package com.github.shader.demo.utils;

import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.jme3.shader.VarType;
import com.jme3.texture.Texture;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ryan
 */
public class BlendLayerEffect {

    private String name;

    public String getName() {
        return name;
    }

    private int layerIndex = -1;
    private String layerParamsPrefix;

    private final Vector4f blendVec = new Vector4f();

    public float getBlendVar() {
        return blendVec.x;
    }

    private final List<Material> materials = new ArrayList<>();

    // remove these and instead loop through all params of the first in the list of
    // Materials for copy/pasting to newly registered mats
    private Texture baseColorMap, normalMap, metallicRoughnessAoMap, emissiveMap;

    public BlendLayerEffect(int layerIndex, List<Material> materials) {
        this(null, layerIndex, materials);
    }

    public BlendLayerEffect(int layerIndex, Spatial spatial) {
        this(null, layerIndex, spatial);
    }

    public BlendLayerEffect(String name, int layerIndex, List<Material> materials) {
        setName(name);
        setLayerIndex(layerIndex);
        this.materials.addAll(materials);
    }

    public BlendLayerEffect(String name, int layerIndex, Spatial spatial) {
        setName(name);
        setLayerIndex(layerIndex);
        addMaterialsFromSpatial(spatial);
    }

    public void setBlendValue(float blendVal) {
        blendVec.setX(blendVal);
    }

    public void clearLayer() {
        for (Material mat : materials) {
            ArrayList<MatParam> matParams = new ArrayList<>(mat.getParams());
            for (MatParam matParam : matParams) {
                if (matParam.getName().startsWith(layerParamsPrefix)) {
                    mat.clearParam(matParam.getName());
                }
            }
        }
    }

    public void setLayerIndex(int layerIndex) {
        if (layerIndex != -1) {
            clearLayer();
        }

        this.layerIndex = layerIndex;
        layerParamsPrefix = "BlendLayer_" + layerIndex;

        for (Material mat : materials) {
            mat.setVector4(layerParamsPrefix + "_BlendVec", blendVec);
        }
    }

    public void registerMaterial(Material material) {
        String blendVecMatParamString = "BlendLayer_" + layerIndex + "_BlendVec";
        // detect if the material's matDef is valid and has support for the blend layer
        if (material != null && material.getMaterialDef().getMaterialParam(blendVecMatParamString) != null) {
            if (!materials.contains(material)) {
                materials.add(material);

                // if this isn't the first material added, copy all params from the first
                // material to the newly registered one
                if (materials.size() > 1) {
                    // Material
                }

                material.setVector4(blendVecMatParamString, blendVec);
            }
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

    private boolean isTriplanar = true;

    public void setTriplanar(boolean isTriplanar) {
        this.isTriplanar = isTriplanar;
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

    public void setParam(String name, VarType varType, Object val) {
        for (Material mat : materials) {
            if (val == null || (val instanceof Boolean && ((Boolean) val == false))) {
                mat.clearParam(name);
            } else {
                if (val instanceof Texture && isTriplanar) {
                    ((Texture) val).setWrap(Texture.WrapMode.Repeat);
                }
                mat.setParam(name, varType, val);
            }
        }
    }

    public void setBaseColorMap(Texture texture) {
        baseColorMap = texture;
        setParam(layerParamsPrefix + "_BaseColorMap", VarType.Texture2D, texture);
    }

    public void setNormalMap(Texture texture) {
        normalMap = texture;
        setParam(layerParamsPrefix + "_NormalMap", VarType.Texture2D, texture);
    }

    public void setMetallicRoughnessAoMap(Texture texture) {
        metallicRoughnessAoMap = texture;
        setParam(layerParamsPrefix + "_MetallicRoughnessAoMap", VarType.Texture2D, texture);
    }

    public void setEmissiveMap(Texture texture) {
        emissiveMap = texture;
        setParam(layerParamsPrefix + "_EmissiveMap", VarType.Texture2D, texture);
    }

    public void setName(String name) {
        if (name == null) {
            name = "";
        }
        this.name = name;
    }

    public void setBlendAlpha(boolean boo) {
        setParam(layerParamsPrefix + "_BlendAlpha", VarType.Boolean, boo);
    }
}
