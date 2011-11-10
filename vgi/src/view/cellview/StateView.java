/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package view.cellview;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;
import view.jgraph.JGraphController;

/*
 *@author cody_liu
 */
public class StateView extends VertexView {

    protected static MyRenderer renderer = new MyRenderer();

    public StateView() {
        super();
    }

    public StateView(Object arg0) {
        super(arg0);
    }

    @Override
    public CellViewRenderer getRenderer() {
        return renderer;
    }

    public static class MyRenderer extends VertexRenderer {

        @Override
        public Point2D getPerimeterPoint(VertexView vv, Point2D source, Point2D p) {

            Rectangle2D r = vv.getBounds();
            double x = r.getX();
            double y = r.getY();
            double a = (r.getWidth() + 1) / 2;
            double b = (r.getHeight() + 1) / 2;

            // x0,y0 - center of ellipse
            double x0 = x + a;
            double y0 = y + b;

            double x1 = p.getX();
            double y1 = p.getY();

            double distance = Math.sqrt((x0 - x1) * (x0 - x1) + (y0 - y1) * (y0 - y1));

            double size = JGraphController.stateSize;
            DefaultGraphCell cell = (DefaultGraphCell) vv.getCell();
            if(cell != null)
            {
               size = Double.parseDouble(cell.getAttributes().get("size").toString());
            }
            
           
            double ratio =( size/2 ) / distance;
            double xout = x0 + ratio *(x1- x0 );
            double yout = y0 + ratio *(y1- y0 );

            Point2D point = new Point2D.Double(xout, yout);
            return point;
        }

        @Override
        public void paint(Graphics g) {

            int b = borderWidth;

            Graphics2D g2 = (Graphics2D) g;
            Dimension d = getSize();
            boolean tmp = selected;
            {
                g.setColor(super.getBackground());
                if (gradientColor != null && !preview) {
                    setOpaque(false);
                    g2.setPaint(new GradientPaint(0, 0, getBackground(),
                            getWidth(), getHeight(), gradientColor, true));
                }
                //g.fillOval(b - 1, b - 1, d.width - b, d.height - b);
            }

            try {
                setBorder(null);
                setOpaque(false);
                selected = false;
                super.paint(g);
            } finally {
                selected = tmp;
            }

            if (bordercolor != null) {
                g.setColor(bordercolor);
                g2.setStroke(new BasicStroke(b));
                g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
            }

            if (selected) {
                g2.setStroke(GraphConstants.SELECTION_STROKE);   //虛線

                g.setColor(highlightColor);
                g.drawOval(b - 1, b - 1, d.width - b, d.height - b);

            }
        }
    }
}