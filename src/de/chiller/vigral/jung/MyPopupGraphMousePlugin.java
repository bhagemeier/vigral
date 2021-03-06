package de.chiller.vigral.jung;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JPopupMenu;

import de.chiller.vigral.graph.Edge;
import de.chiller.vigral.graph.Vertex;
import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.AbstractPopupGraphMousePlugin;

/**
 * a plugin that uses popup menus to show properties and delete or modify verties and edges
 * 
 * @author Simon Schiller
 *
 */
public class MyPopupGraphMousePlugin extends AbstractPopupGraphMousePlugin {
    
    protected JPopupMenu popup = new JPopupMenu();

    /**
     * constructor
     */
    public MyPopupGraphMousePlugin() {
    	this(MouseEvent.BUTTON3_MASK);
    }
    
    /**
     * constructor
     * @param modifiers int modifiers
     */
    public MyPopupGraphMousePlugin(int modifiers) {
    	super(modifiers);
    }
    
    /**
     * creates the popup menu
     */
    protected void handlePopup(MouseEvent e) {
        @SuppressWarnings("unchecked")
		final VisualizationViewer<Vertex, Edge> vv = (VisualizationViewer<Vertex, Edge>)e.getSource();
        Point2D p = e.getPoint();
        
        // TODO inform the editing plugin about a popupmenu to be present so that no new vertex will be created when clicking outside the popupmenu
        
        GraphElementAccessor<Vertex, Edge> pickSupport = vv.getPickSupport();
        if(pickSupport != null) {
            final Vertex vertex = pickSupport.getVertex(vv.getGraphLayout(), p.getX(), p.getY());
            if(vertex != null) {
            	ElementPopupMenu.setMode(ElementPopupMenu.VERTEXMENU, vertex, null, vv);
            	new ElementPopupMenu.PopupMenu().show(vv, e.getX(), e.getY());
            } 
            else {
                final Edge edge = pickSupport.getEdge(vv.getGraphLayout(), p.getX(), p.getY());
                if(edge != null) {
                	ElementPopupMenu.setMode(ElementPopupMenu.EDGEMENU, null, edge, vv);
                	new ElementPopupMenu.PopupMenu().show(vv, e.getX(), e.getY());
                }
            }
        }
    }

}