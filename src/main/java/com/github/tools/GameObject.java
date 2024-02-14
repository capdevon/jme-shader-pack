package com.github.tools;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 *
 * @author capdevon
 */
public class GameObject {

    /**
     * A private constructor to inhibit instantiation of this class.
     */
    private GameObject() {}

    /**
     * Returns the component of Type type in the GameObject or any of its children
     * using depth first search.
     */
    public static <T extends Control> T getComponentInChildren(Spatial sp, final Class<T> clazz) {
        T control = sp.getControl(clazz);
        if (control != null) {
            return control;
        }

        if (sp instanceof Node) {
            for (Spatial child : ((Node) sp).getChildren()) {
                control = getComponentInChildren(child, clazz);
                if (control != null) {
                    return control;
                }
            }
        }

        return null;
    }

    /**
     * Retrieves the component of Type type in the GameObject or any of its parents.
     */
    public static <T extends Control> T getComponentInParent(Spatial sp, Class<T> clazz) {
        Node parent = sp.getParent();
        while (parent != null) {
            T control = parent.getControl(clazz);
            if (control != null) {
                return control;
            }
            parent = parent.getParent();
        }
        return null;
    }
}