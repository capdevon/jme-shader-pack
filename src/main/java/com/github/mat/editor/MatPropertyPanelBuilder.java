package com.github.mat.editor;

import java.util.HashMap;
import java.util.Map;

import com.github.tools.editor.MaterialEditorBuilder;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitorAdapter;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.RollupPanel;
import com.simsilica.lemur.component.SpringGridLayout;

/**
 * 
 * @author capdevon
 */
public class MatPropertyPanelBuilder {
    
    private final String[] ignoredProperties;
    private final Map<String, SpinnerModel> constraints = new HashMap<>();
    
    /**
     * @param ignoredProperties
     */
    public MatPropertyPanelBuilder(String... ignoredProperties) {
        this.ignoredProperties = ignoredProperties;
    }
    
    /**
     * Add a new constraint definition.
     * @param paramName
     * @param model
     */
    public final void addConstraint(String paramName, SpinnerModel<?> model) {
        constraints.put(paramName, model);
    }

    /**
     * Copies all of the constraints from the specified map to this map.
     * @param map
     */
    public final void addConstraints(Map<String, SpinnerModel> map) {
        constraints.putAll(map);
    }

    public Container buildPanel(Spatial spatial) {

        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
//        TabbedPanel tabbedPanel = container.addChild(new TabbedPanel());

        spatial.breadthFirstTraversal(new SceneGraphVisitorAdapter() {
            @Override
            public void visit(Geometry geom) {
                System.out.println("------------------------------------");
                System.out.println("Name: " + geom);
                System.out.println("Triangle: " + geom.getTriangleCount());
                System.out.println("Vertex: " + geom.getVertexCount());
                System.out.println("LOD: " + geom.getLodLevel());
                System.out.println("MeshMode: " + geom.getMesh().getMode());

                String title = geom.toString();
                MaterialEditorBuilder builder = new MaterialEditorBuilder(ignoredProperties);
                builder.addConstraints(constraints);
                Panel panel = builder.buildPanel(geom.getMaterial());
                
                RollupPanel rollup = new RollupPanel(title, panel, "glass");
                rollup.setAlpha(0, false);
                rollup.setOpen(false);
                container.addChild(rollup);

//                tabbedPanel.addTab(geom.getName(), panel);
            }
        });

        return container;
    }

}
