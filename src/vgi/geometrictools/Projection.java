/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.geometrictools;

import java.awt.geom.Point2D;

/**
 *
 * @author reng
 */
public class Projection {
    Point2D center; //center of the automata in pixels
    Vector2D ix; // image of (1,0) in pixels
    
    public Projection(){
        center=new Point2D.Double(400, 300);
        ix=new Vector2D(1,0);
        
//        System.out.println("new projection:"+center+"  "+ix);
    }
    public Projection(Point2D c,Vector2D ixx){
        center=c;
        ix=ixx;
        
//        System.out.println("new projection:"+center+"  "+ix);
    }
    public void setCenter(Point2D c){
//        center=c;
        center.setLocation(c.getX(),c.getY());
    }
    public void setIx(Vector2D v){
        ix=v;
    }
    
    
    // Location: pixels in the screen
    // Geometry: coordinates defined in automata
    public Point2D getLocFromGeo(Point2D pt){
        
        if(pt==null) return pt;
        
        
        double x=center.getX()+pt.getX()*ix.getX()+pt.getY()*ix.getY();
        double y=center.getY()+pt.getX()*ix.getY()-pt.getY()*ix.getX();
//        System.out.println("geo: "+pt+" -> "+"loc: "+x+" , "+(y));
        
        return new Point2D.Double(x,y);
    }
    public Point2D getGeoFromLoc(Point2D pt){
        
        if(pt==null) return pt;
       
        double norm=ix.lengthSquared();
        double x=ix.getX()*(pt.getX()-center.getX())+ix.getY()*(pt.getY()-center.getY());
        double y=ix.getY()*(pt.getX()-center.getX())-ix.getX()*(pt.getY()-center.getY());
//        System.out.println("loc: "+pt+" -> "+"geo: "+x/norm+" , "+y/norm);
        
        return new Point2D.Double(x/norm,y/norm);
    }
    public Vector2D getLocVecFromGeoVec(Vector2D vec){
        
        if(vec==null) return vec;
        
        double x=vec.getX()*ix.getX()+vec.getY()*ix.getY();
        double y=vec.getX()*ix.getY()-vec.getY()*ix.getX();
        //System.out.println("geo: "+vec+" -> "+"vec: "+x+" , "+y);
        
        return new Vector2D(x,y);
        
    }
    public Vector2D getGeoVecFromLocVec(Vector2D vec) {
         if(vec==null) return vec;
        
        
        double norm=ix.lengthSquared();
        double x=ix.getX()*(vec.getX())+ix.getY()*(vec.getY());
        double y=ix.getY()*(vec.getX())-ix.getX()*(vec.getY());
        //System.out.println("vec: "+vec+" -> "+"geo: "+x+" , "+y);
        
        return new Vector2D(x/norm,y/norm);
    }
    public Vector2D getLocScalFromGeoScal(Vector2D vec){
        
        
        if(vec==null) return vec;
        
        double x=Math.abs(vec.getX()*ix.getX()+vec.getY()*ix.getY());
        double y=Math.abs(vec.getX()*ix.getY()-vec.getY()*ix.getX());
        
        return new Vector2D(x,y);
        
    }
    public Vector2D getGeoScalFromLocScal(Vector2D vec){
        
        if(vec==null) return vec;
        
        
        double norm=ix.lengthSquared();
        double x=Math.abs(ix.getX()*(vec.getX())+ix.getY()*(vec.getY()));
        double y=Math.abs(ix.getY()*(vec.getX())-ix.getX()*(vec.getY()));
        //System.out.println("vec: "+vec+" -> "+"geo: "+x+" , "+y);
        
        return new Vector2D(x/norm,y/norm);
    }

    public Point2D getCenter() {
        return center;
    }
    public Vector2D getIx(){
        return ix;
    }

    public Vector2D getScalFromLoc(Vector2D vec) {
         if(vec==null) return vec;
        
        double x=Math.abs(vec.getX()/ix.length());
        double y=Math.abs(vec.getY()/ix.length());
        //System.out.println("geo: "+vec+" -> "+"vec: "+x+" , "+y);
        
        return new Vector2D(x,y);
    }

    
}
