package com.github.tools;

import java.awt.Color;

import com.jme3.math.ColorRGBA;

public class ColorUtils {
    
    private ColorUtils() {}

    /**
     * Convert <code>com.jme3.math.ColorRGBA</code> 
     * to <code>java.awt.Color</code>
     * 
     * @param c
     * @return
     */
    public static Color toColorAWT(ColorRGBA c) {
        int r = (int) (c.r * 255);
        int g = (int) (c.g * 255);
        int b = (int) (c.b * 255);
        int a = (int) (c.a * 255);
        return new Color(r, g, b, a);
    }

    /**
     * Convert <code>java.awt.Color</code> 
     * to <code>com.jme3.math.ColorRGBA</code>
     * 
     * @param c
     * @return
     */
    public static ColorRGBA toColorRGBA(Color c) {
        return ColorRGBA.fromRGBA255(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

}
