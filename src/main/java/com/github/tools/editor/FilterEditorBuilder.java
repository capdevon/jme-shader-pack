package com.github.tools.editor;

import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.ViewPort;
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
public class FilterEditorBuilder extends AbstractEditor<ViewPort> {
    
    @Override
    public Container buildPanel(ViewPort viewPort) {

        Container container = new Container(new SpringGridLayout(Axis.Y, Axis.X, FillMode.None, FillMode.Even));
        //TabbedPanel tabbedPanel = container.addChild(new TabbedPanel());
        
        for (SceneProcessor processor : viewPort.getProcessors()) {
            if (processor instanceof FilterPostProcessor) {
                FilterPostProcessor fpp = (FilterPostProcessor) processor;
                for (Filter filter : fpp.getFilterList()) {
                    
                    System.out.println("buildPanel Filter: " + filter.getName());
                    buildPanel(container, filter);
                }
            }
        }

        return container;
    }

    /**
     * @param container
     * @param filter
     */
    private void buildPanel(Container container, Filter filter) {
        try {
            ReflectedEditorBuilder builder = new ReflectedEditorBuilder();
            Panel panel = builder.buildPanel(filter);

            String title = filter.getClass().getSimpleName();
            RollupPanel rollup = new RollupPanel(title, panel, "glass");
            rollup.setAlpha(0, false);
            rollup.setOpen(false);
            container.addChild(rollup);

            //tabbedPanel.addTab(title, panel);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
