/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.github.shader.demo.utils.blendlayers;

import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;

/**
 *
 * @author ryan
 */
public class ShaderBlendLayerGenerator {
   
    
    public static ShaderBlendLayer makeDefaultIceLayer(AssetManager assetManager){
        String dirName = "Models/Cracked_Ice/DefaultMaterial_";
        ShaderBlendLayer ice = new ShaderBlendLayer("Freeze");
        ice.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        ice.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        ice.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));
        ice.setEdgeFadeColorGradientStart(new ColorRGBA(0.92f,0.905f, 1.0f, 1.0f));
        ice.setEdgeFadeColorGradientEnd(new ColorRGBA(0.9f,0.92f, 0.995f, 0.0f));
        ice.setEdgeFadeThickness(0.09f);
        
        ice.setDissolveNoiseVal0(new Vector2f(0.12f, 1.0f));
         ice.setDissolveNoiseVal0(new Vector2f(0.24f, 0.2f));

        return ice;
    }
    public static ShaderBlendLayer makeDefaultStoneLayer(AssetManager assetManager){
        String dirName = "Models/Cracked_Stone/DefaultMaterial_";
        ShaderBlendLayer stone = new ShaderBlendLayer("Petrify");
        stone.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        stone.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        stone.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));
        stone.setEdgeFadeThickness(0.04f);
        
        stone.setEdgeFadeColorGradientStart(new ColorRGBA(0.23f,0.494f, 0.21f, 1.0f));
        stone.setEdgeFadeColorGradientEnd(new ColorRGBA(0.02f,0.05f, 0.03f, 0.0f));
        
        stone.setDissolveNoiseVal0(new Vector2f(0.05f, 1.0f));

        return stone;
    }
    public static ShaderBlendLayer makeDefaultShieldArmorLayer(AssetManager assetManager){
        String dirName = "Models/Shield_Armor/DefaultMaterial_";
        ShaderBlendLayer shield = new ShaderBlendLayer("Shield");
        shield.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        shield.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        shield.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));
        shield.setEmissiveMap(assetManager.loadTexture(dirName + "emissive.png"));
        shield.setEmissiveIntensity(0.5f);
        shield.setBlendAlpha(true);
        
        shield.setEdgeFadeColorGradientStart(new ColorRGBA(0.93f,0.64f, 0.73f, 1.0f));
        shield.setEdgeFadeColorGradientEnd(new ColorRGBA(0.2f,0.15f, 0.13f, 0.0f));
        shield.setEdgeFadeThickness(0.2f);
        
        shield.setDissolveNoiseVal0(new Vector2f(0.03f, 1.0f));
        
        return shield;
    }
    
    public static ShaderBlendLayer makeDefaultRustLayer(AssetManager assetManager){
        String dirName = "Models/Rust/DefaultMaterial_";
        ShaderBlendLayer rust = new ShaderBlendLayer("Rust");
        rust.setBaseColorMap(assetManager.loadTexture(dirName + "baseColor.png"));
        rust.setNormalMap(assetManager.loadTexture(dirName + "normal.png"));
        rust.setMetallicRoughnessAoMap(assetManager.loadTexture(dirName + "occlusionRoughnessMetallic.png"));
        rust.setBlendAlpha(true);
        rust.setScale(1.2f);
        
        rust.setEdgeFadeColorGradientStart(new ColorRGBA(0.9f,0.64f, 0.5f, 1.0f));
        rust.setEdgeFadeColorGradientEnd(new ColorRGBA(0.5f,0.48f, 0.03f, 0.0f));
        rust.setEdgeFadeThickness(0.14f);
        
        rust.setDissolveNoiseVal0(new Vector2f(0.1f, 1.0f));
        rust.setDissolveNoiseVal1(new Vector2f(0.02f, 0.4f));
        
        return rust;
    }
    
    public static ShaderBlendLayer makeBlankLayer(String name){
        ShaderBlendLayer blankWhiteLayer = new ShaderBlendLayer(name);
        blankWhiteLayer.setBaseColor(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));        
        return blankWhiteLayer;
    }
    
}
