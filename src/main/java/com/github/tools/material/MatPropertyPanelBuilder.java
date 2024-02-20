package com.github.tools.material;

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
                MaterialEditorBuilder builder = new MaterialEditorBuilder();
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
