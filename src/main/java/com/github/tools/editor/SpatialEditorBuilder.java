package com.github.tools.editor;

import com.jme3.light.Light;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RollupPanel;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.props.PropertyPanel;

/**
 * 
 * @author capdevon
 */
public class SpatialEditorBuilder extends AbstractEditor<Spatial> {
    
    private static final String[] ignoredProperties = {
            "nbSimultaneousGPUMorph",
            "dirtyMorph",
            "morphState",
            "fallbackMorphTarget",
            "lastFrustumIntersection",
            "modelBound",
            "localTransform",
            "name",
            "material",
            "mesh",
            "key"
    };
    
    @Override
    public Container buildPanel(Spatial bean) {

        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
//        TabbedPanel tabbedPanel = container.addChild(new TabbedPanel());

        bean.depthFirstTraversal(new SceneGraphVisitor() {
            @Override
            public void visit(Spatial spatial) {
                try {
//                    ReflectedEditorBuilder builder = new ReflectedEditorBuilder(ignoredProperties);
//                    Panel panel = builder.buildPanel(spatial);
                    
                    Container panel = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
                    PropertyPanel propertyPanel = panel.addChild(new PropertyPanel("glass"));
                    
                    propertyPanel.addEnumProperty("Cull Hint", spatial, "cullHint");
                    propertyPanel.addEnumProperty("Queue Bucket", spatial, "queueBucket");
                    propertyPanel.addEnumProperty("Shadow Mode", spatial, "shadowMode");
                    propertyPanel.addEnumProperty("Batch Hint", spatial, "batchHint");
                    panel.addChild(addVector3Property("Position", spatial, "localTranslation"));
                    panel.addChild(addQuaternionProperty("Rotation", spatial, "localRotation"));
                    panel.addChild(addVector3Property("Scale", spatial, "localScale"));
                    
                    RollupPanel rollup = new RollupPanel(spatial.getName(), panel, "glass");
                    rollup.setAlpha(0, false);
                    rollup.setOpen(false);
                    container.addChild(rollup);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Lights
                for (Light light : spatial.getLocalLightList()) {
                    LightEditorBuilder builder = new LightEditorBuilder();
                    Panel panel = builder.buildPanel(light);
                    container.addChild(panel);
                }
                
                // Controls
                for (int i = 0; i < spatial.getNumControls(); i++) {

                    Control control = spatial.getControl(i);
                    String title = control.getClass().getSimpleName();

                    try {
                        System.out.println("------------------------------------");
                        System.out.println(title);

                        ReflectedEditorBuilder builder = new ReflectedEditorBuilder();
                        Panel panel = builder.buildPanel(control);

                        RollupPanel rollup = new RollupPanel(title, panel, "glass");
                        rollup.setAlpha(0, false);
                        rollup.setOpen(false);
                        container.addChild(rollup);

//                        tabbedPanel.addTab(title, panel);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return container;
    }

}
