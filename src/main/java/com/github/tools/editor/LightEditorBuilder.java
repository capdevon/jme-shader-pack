package com.github.tools.editor;

import com.github.tools.util.ConfigurationBuilder;
import com.jme3.light.Light;
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
public class LightEditorBuilder extends AbstractEditor<Light> {
    
    private static final String[] ignoredProperties = {
            "frustumCheckNeeded",
            "intersectsFrustum",
            "invRadius",
            "type",
            "name"
    };

    @Override
    public Container buildPanel(Light light) {

        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        String title = light.getType() + "Light [" + light.getName() + "]";

        try {
            System.out.println("------------------------------------");
            System.out.println(title);

            ConfigurationBuilder config = new ConfigurationBuilder();
            config.setIgnoredProperties(ignoredProperties);
            
            ReflectedEditorBuilder builder = new ReflectedEditorBuilder(config);
            Panel panel = builder.buildPanel(light);

            RollupPanel rollup = new RollupPanel(title, panel, "glass");
            rollup.setAlpha(0, false);
            rollup.setOpen(false);
            container.addChild(rollup);

        } catch (Exception e) {
            e.printStackTrace();
        }
      
        return container;
    }

}
