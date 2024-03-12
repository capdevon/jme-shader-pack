package com.github.shader.demo.blendlayers;

import java.util.List;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author ryan
 */
public class BlendGroup {

    public Spatial spatial = null;
    
    /**
     * Number of layers to reserve at the start of the blend-stack. All dynamic
     * layers will be added ontop of these layers based on priority (and then
     * duration if priority is tied)
     */
    private int reservedLayerCount = 0; 
    private int currentDynamicLayerCount;
    private List<Material> materials;
    private ShaderBlendLayer[] currentLayers = new ShaderBlendLayer[100];

    public BlendGroup(Spatial spatial, List<Material> materials) {
        this.spatial = spatial;
        this.materials = materials;

        for (Material material : materials) {
            this.setBlendGroupMatParams(material);
        }
    }

    public ShaderBlendLayer[] getRegisteredLayers() {
        return currentLayers;
    }

    public int getReservedLayerCount() {
        return reservedLayerCount;
    }

    public void setReservedLayerCount(int reservedLayerCount) {
        this.reservedLayerCount = reservedLayerCount;
    }

    public int getCurrentDynamicLayerCount() {
        return currentDynamicLayerCount;
    }

    public int getCurrentLayerCount() {
        return currentDynamicLayerCount + reservedLayerCount;
    }

    public void setReservedLayer() {

    }

    public void setBlendLayer(int layerIndex, ShaderBlendLayer blendLayer) {

        if (layerIndex >= reservedLayerCount) {
            if (currentLayers[layerIndex] == null) {
                currentDynamicLayerCount++;
            }
        }
        blendLayer.clearMaterials();
        blendLayer.setLayerIndex(layerIndex);
        blendLayer.setMaterials(materials);

        currentLayers[layerIndex] = blendLayer;
    }

    public void swapBlendLayers(ShaderBlendLayer layerA, ShaderBlendLayer layerB) {
        int layerIndexA = layerA.getLayerIndex();
        int layerIndexB = layerB.getLayerIndex();

        this.setBlendLayer(layerIndexB, layerA);
        this.setBlendLayer(layerIndexA, layerB);
    }

    // set a new layer at the desired index, and shift all other above it up to make
    // room
    public void setBlendLayerAndShiftUp(ShaderBlendLayer layer, int layerIndex) {
        for (int i = currentLayers.length - 1; i >= layerIndex; i--) {

            int shiftedIndex = i + 1;
            ShaderBlendLayer layerToShift = currentLayers[i];
            if (layerToShift != null) {
                setBlendLayer(shiftedIndex, layerToShift);
            }

        }
        setBlendLayer(layerIndex, layer);
    }

    public void removeBlendLayer(ShaderBlendLayer blendLayer) {
        blendLayer.clearMaterials();

        int layerIndex = blendLayer.getLayerIndex();

        if (layerIndex > -1 && layerIndex < currentLayers.length) {
            if (currentLayers[layerIndex] == blendLayer) { // make sure the layer was indeed at the set index in this
                                                           // BlendGroup before shifting other layers, to avoid breaking
                                                           // everything else if that is done by mistake
                // shift down all layers that were above the removed layer
                for (int i = layerIndex; i < currentLayers.length - 1; i++) {
                    int layerToShiftIndex = i + 1;
                    ShaderBlendLayer layerToShiftDown = currentLayers[layerToShiftIndex];
                    if (layerToShiftDown != null) {
                        this.setBlendLayer(i, layerToShiftDown);
                    }
                }

                if (layerIndex >= reservedLayerCount) {
                    currentDynamicLayerCount--;
                    System.out.println(" CLC 11111   ::   " + currentDynamicLayerCount);
                }

                currentLayers[layerIndex] = null;

            }
        }
    }

    private final Vector3f spatialDimensions = new Vector3f();
    private final Vector3f spatialWorldTranslation = new Vector3f();

    public void update(float tpf) {
        for (ShaderBlendLayer blendLayer : currentLayers) {

            if (blendLayer != null) {
                blendLayer.update(tpf);
            }

            if (spatial != null) {
                BoundingVolume bounds = spatial.getWorldBound();
                float yDim, xDim, zDim;
                if (bounds instanceof BoundingBox) {
                    yDim = ((BoundingBox) bounds).getYExtent() * 2;
                    xDim = ((BoundingBox) bounds).getXExtent() * 2;
                    zDim = ((BoundingBox) bounds).getZExtent() * 2;
                    spatialDimensions.set(xDim, yDim, zDim);

                } else if (bounds instanceof BoundingSphere) {
                    float diameter = ((BoundingSphere) bounds).getRadius() * 2;
                    spatialDimensions.set(diameter, diameter, diameter);
                }

                if (spatialWorldTranslation != spatial.getWorldTranslation()) {
                    spatialWorldTranslation.set(spatial.getWorldTranslation());
                }
            }
        }
    }

    public void setBlendGroupMatParams(Material material) {
        material.setVector3("BlendGroup_SpatialDimensions", spatialDimensions);
        material.setVector3("BlendGroup_SpatialOriginOffset", spatialWorldTranslation);
    }

}