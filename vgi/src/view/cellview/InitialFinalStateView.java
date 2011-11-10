package view.cellview;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.Color;

import javax.swing.BorderFactory;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

/**
 * @author Gaudenz Alder
 *
 */

/*
 * TODO
 *  Line sawtooth
 */
public class InitialFinalStateView extends VertexView {

    /**
     */
    public static transient StateRenderer renderer = new StateRenderer();

    /**
     */
    public InitialFinalStateView() {
         super();
         System.out.println("initialState");
    }

    /**
     */
    public InitialFinalStateView(Object cell) {
        super(cell);
    }

    /**
     * Returns the intersection of the bounding rectangle and the
     * straight line between the source and the specified point p.
     * The specified point is expected not to intersect the bounds.
     * Perimeter【數】周
     */
    public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {

        Rectangle2D r = getBounds();

        double x = r.getX();
        double y = r.getY();
        double a = (r.getWidth() + 1) / 2;
        double b = (r.getHeight() + 1) / 2;

        // x0,y0 - center of ellipse
        double x0 = x + a;
        double y0 = y + b;

        // x1, y1 - point
        double x1 = p.getX();
        double y1 = p.getY();

        // calculate straight line equation through point and ellipse center
        // y = d * x + h
        double dx = x1 - x0;
        double dy = y1 - y0;

        if (dx == 0) {
            return new Point((int) x0, (int) (y0 + b * dy / Math.abs(dy)));
        }
        double d = dy / dx;
        double h = y0 - d * x0;

        // calculate intersection
        double e = a * a * d * d + b * b;
        double f = -2 * x0 * e;
        double g = a * a * d * d * x0 * x0 + b * b * x0 * x0 - a * a * b * b;

        double det = Math.sqrt(f * f - 4 * e * g);

        // two solutions (perimeter points)
        double xout1 = (-f + det) / (2 * e);
        double xout2 = (-f - det) / (2 * e);
        double yout1 = d * xout1 + h;
        double yout2 = d * xout2 + h;

        double dist1Squared = Math.pow((xout1 - x1), 2) + Math.pow((yout1 - y1), 2);
        double dist2Squared = Math.pow((xout2 - x1), 2) + Math.pow((yout2 - y1), 2);

        // correct solution
        double xout, yout;

        if (dist1Squared < dist2Squared) {
            xout = xout1;
            yout = yout1;
        } else {
            xout = xout2;
            yout = yout2;
        }

        return getAttributes().createPoint(xout, yout);
    }

    /**
     */
    public CellViewRenderer getRenderer() {
        return renderer;
    }

    /**
     */
    public static class StateRenderer extends VertexRenderer {

        /**
         * Return a slightly larger preferred size than for a rectangle.
         */
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.width += d.width / 8;
            d.height += d.height / 2;
            return d;
        }

        public void paint(Graphics g) {

            int b = borderWidth;

            Graphics2D g2 = (Graphics2D) g;
            Dimension d = getSize();
            boolean tmp = selected;
            int width =  d.width*3/5;
            int height = d.height;
            {
                g.setColor(super.getBackground());
                if (gradientColor != null && !preview) {
                    setOpaque(false);
                    g2.setPaint(new GradientPaint(0, 0, getBackground(),
                            getWidth(), getHeight(), gradientColor, true));
                }
               g.fillOval(0+ (width/3),0, width, height);
           
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
            //  g.drawOval(b - 1, b - 1, d.width - b, d.height - b);
            }



            int xpoints[] = {width/6, width/3, width/6};
            int ypoints[] = {height /4, height / 2, 3*height /4};
            int npoints = 3;
            g.fillPolygon(xpoints,ypoints,npoints);
            g.drawLine(0, height / 2, (width/3), height / 2);


            int xpoints_2[] = {width/3+width+width/6, width/3+width+width/3, width/3+width+width/6};
            int ypoints_2[] = {height /4, height / 2, 3*height /4};
            int npoints_2 = 3;
            g.fillPolygon(xpoints_2,ypoints_2,npoints_2);
            g.drawLine(width/3+width, height / 2, 2*width/3+width, height / 2);
            

            if (bordercolor != null) {
                g.setColor(bordercolor);
                g2.setStroke(new BasicStroke(b));
                 g.drawOval(width/3,0, width-b ,height-b);
                
                if (selected) {
                    g2.setStroke(GraphConstants.SELECTION_STROKE);   //虛線
                    g.setColor(highlightColor);
                 g.drawOval(width/3,0, width-b ,height-b);

                    // g.drawOval(b - 1 +(d.width/3), b - 1, d.width - b, d.height - b);
    }
            }
        }
    }
}