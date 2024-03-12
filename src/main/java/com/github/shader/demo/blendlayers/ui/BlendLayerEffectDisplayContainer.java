package com.github.shader.demo.utils.blendlayers.interfaceelements;

import com.github.shader.demo.utils.blendlayers.effects.BlendLayerEffect;
import com.github.tools.SpinnerFloatModel;
import com.github.tools.SpinnerIntegerModel;
import com.github.tools.editor.ReflectedEditorBuilder;
import com.github.tools.util.ConfigurationBuilder;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Button;
import com.simsilica.lemur.Command;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RollupPanel;
import com.simsilica.lemur.component.SpringGridLayout;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author ryan
 */
    
    public class BlendLayerEffectDisplayContainer extends Container{
        
        
        public Button xRemoveButton;
        
        private BlendLayerEffect blendLayerEffect;
        public BlendLayerEffect getBlendLayerEffect() { return blendLayerEffect; }
        
        public RollupPanel propertyPanel ;
        public RollupPanel getPropertyPanel() {        return propertyPanel;    }
        
        public BlendLayerEffectDisplayContainer(BlendLayerEffect blendLayerEffect) {
            super();
            this.blendLayerEffect = blendLayerEffect;
            
            setLayout(new SpringGridLayout(Axis.Y, Axis.X, FillMode.Even, FillMode.First));
            
            propertyPanel = makePropertiesPanel(blendLayerEffect.getName(), blendLayerEffect);            
            addChild(propertyPanel);

            xRemoveButton = new Button(" X ");

            addChild(xRemoveButton, 0 , 1);
        }

        public RollupPanel makePropertiesPanel(String name, Object obj){
         
            ConfigurationBuilder config = new ConfigurationBuilder(); //probably pass this in as a parm so it can differ for effect vs blendLayer
            config.addConstraint("duration", new SpinnerFloatModel(0.0f, 60.0f, 0.1f));
            config.addConstraint("fadeInTime", new SpinnerFloatModel(0.0f, 60.0f, 0.1f));
            config.addConstraint("fadeOutTime", new SpinnerFloatModel(0.0f, 60.0f, 0.1f));
    
            

            ReflectedEditorBuilder builder = new ReflectedEditorBuilder(config);
            Panel panel = builder.buildPanel(obj);

            RollupPanel rollup = new RollupPanel(name, panel, "glass");
            rollup.setAlpha(0, false);
            rollup.setOpen(false);
            return rollup;
        }

 
    }