import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;

import org.apache.commons.collections15.Factory;

import edu.uci.ics.jung.algorithms.layout.GraphElementAccessor;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationServer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.util.ArrowFactory;


public class EditSupport<V,E> {
	
	protected V mStartVertex;
    protected Point2D mDown;
    
    protected CubicCurve2D mRawEdge = new CubicCurve2D.Float();
    protected Shape mEdgeShape;
    protected Shape mRawArrowShape;
    protected Shape mArrowShape;
    protected VisualizationServer.Paintable mEdgePaintable;
    protected VisualizationServer.Paintable mArrowPaintable;
    protected EdgeType mEdgeIsDirected;
    protected Factory<V> mVertexFactory;
    protected Factory<E> mEdgeFactory;

	
	/**
     * Used for the edge creation visual effect during mouse drag
     */
    class EdgePaintable implements VisualizationServer.Paintable {
        
        public void paint(Graphics g) {
            if(mEdgeShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D)g).draw(mEdgeShape);
                g.setColor(oldColor);
            }
        }
        
        public boolean useTransform() {
            return false;
        }
    }
    
    /**
     * Used for the directed edge creation visual effect during mouse drag
     */
    class ArrowPaintable implements VisualizationServer.Paintable {
        
        public void paint(Graphics g) {
            if(mArrowShape != null) {
                Color oldColor = g.getColor();
                g.setColor(Color.black);
                ((Graphics2D)g).fill(mArrowShape);
                g.setColor(oldColor);
            }
        }
        
        public boolean useTransform() {
            return false;
        }
    }
    
    
	
	public EditSupport(Factory<V> vertexFactory, Factory<E> edgeFactory) {
		mVertexFactory = vertexFactory;
        mEdgeFactory = edgeFactory;
        mRawEdge.setCurve(0.0f, 0.0f, 0.33f, 100, .66f, -50, 1.0f, 0.0f);
        mRawArrowShape = ArrowFactory.getNotchedArrow(20, 16, 8);
        mEdgePaintable = new EdgePaintable();
        mArrowPaintable = new ArrowPaintable();
        mEdgeIsDirected = EdgeType.UNDIRECTED;
	}
	
	
	public void startEdge(MouseEvent e, V vertex) {
		
		// get the clicked vv and the coordinates
    	final VisualizationViewer<V,E> vv = (VisualizationViewer<V,E>)e.getSource();
        final Point2D p = e.getPoint();
        
        // get an instance of the graphelementaccessor
        GraphElementAccessor<V,E> pickSupport = vv.getPickSupport();
        
        mStartVertex = vertex;
        mDown = e.getPoint();
        transformEdgeShape(mDown, mDown);
        vv.addPostRenderPaintable(mEdgePaintable);
	}
	

	public void drawEdge(MouseEvent e) {
		if(mStartVertex != null) {
            transformEdgeShape(mDown, e.getPoint());
            /*
            if(edgeIsDirected == EdgeType.DIRECTED) {
                transformArrowShape(down, e.getPoint());
            }
            */
        }
        VisualizationViewer<V,E> vv = (VisualizationViewer<V,E>)e.getSource();
        vv.repaint();
	}

	
	public void addVertex(MouseEvent e, VisualizationViewer<V, E> vv) {
		
		// get the graph
    	Graph<V,E> graph = vv.getModel().getGraphLayout().getGraph();
		
		V newVertex = mVertexFactory.create();
		
        Layout<V,E> layout = vv.getModel().getGraphLayout();
        graph.addVertex(newVertex);
        layout.setLocation(newVertex, vv.getRenderContext().getMultiLayerTransformer().inverseTransform(e.getPoint()));
		
	}
	
	
	
	public void addEdge(MouseEvent e, Point2D p, V vertex, VisualizationViewer<V, E> vv) {
		
		if((vertex != null) && (mStartVertex != null)) {
			if(!(mDown.getX() == p.getX() && mDown.getY() == p.getY())) {
	    		Graph<V,E> graph = vv.getGraphLayout().getGraph();
	    		graph.addEdge(mEdgeFactory.create(), mStartVertex, vertex, mEdgeIsDirected);
	    	}
		}
        vv.repaint();
        mStartVertex = null;
        mDown = null;
        mEdgeIsDirected = EdgeType.UNDIRECTED;
        vv.removePostRenderPaintable(mEdgePaintable);
        vv.removePostRenderPaintable(mArrowPaintable);
	}
	
	
	/**
     * code lifted from PluggableRenderer to move an edge shape into an
     * arbitrary position
     */
    private void transformEdgeShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x1, y1);
        
        float dx = x2-x1;
        float dy = y2-y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        float dist = (float) Math.sqrt(dx*dx + dy*dy);
        xform.scale(dist / mRawEdge.getBounds().getWidth(), 1.0);
        mEdgeShape = xform.createTransformedShape(mRawEdge);
    }
    
    private void transformArrowShape(Point2D down, Point2D out) {
        float x1 = (float) down.getX();
        float y1 = (float) down.getY();
        float x2 = (float) out.getX();
        float y2 = (float) out.getY();

        AffineTransform xform = AffineTransform.getTranslateInstance(x2, y2);
        
        float dx = x2-x1;
        float dy = y2-y1;
        float thetaRadians = (float) Math.atan2(dy, dx);
        xform.rotate(thetaRadians);
        mArrowShape = xform.createTransformedShape(mRawArrowShape);
    }
}
