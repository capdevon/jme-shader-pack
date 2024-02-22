package com.github.tools.material;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.tools.editor.MaterialEditorBuilder;
import com.github.tools.util.ConfigurationBuilder;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
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
    
    private static final Logger log = LoggerFactory.getLogger(MatPropertyPanelBuilder.class);
    
    private Predicate<MatParam> ignoreParamFilter;

    public void setIgnoreParamFilter(Predicate<MatParam> ignoreParamFilter) {
        this.ignoreParamFilter = ignoreParamFilter;
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
                Material material = geom.getMaterial();
                
                Set<String> ignoredProperties = new HashSet<>();
                if (ignoreParamFilter != null) {
                    for (MatParam param : material.getParams()) {
                        if (ignoreParamFilter.test(param)) {
                            log.debug("Ignore Properties: {}", param);
                            ignoredProperties.add(param.getName());
                        }
                    }
                }
                
                ConfigurationBuilder config = new ConfigurationBuilder();
                config.setIgnoredProperties(ignoredProperties.toArray(new String[0]));
                config.addConstraints(MatConstraints.getPBRConstraints());
                
                MaterialEditorBuilder builder = new MaterialEditorBuilder(config);
                Panel panel = builder.buildPanel(material);
                
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
