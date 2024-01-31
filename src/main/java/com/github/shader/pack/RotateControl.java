package com.github.shader.pack;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

public class RotateControl extends AbstractControl {

    private float speed = 1.0f;
    
    public RotateControl() {}

    public RotateControl(float speed) {
        this.speed = speed;
    }

    @Override
    protected void controlUpdate(float tpf) {
        spatial.rotate(0, speed * tpf, 0);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(speed, "speed", 1.0f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        InputCapsule ic = im.getCapsule(this);
        speed = ic.readFloat("speed", 1.0f);
    }

}