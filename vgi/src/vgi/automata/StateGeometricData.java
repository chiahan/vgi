/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package vgi.automata;

import java.awt.geom.Point2D;

/**
 *
 * @author reng
 */
public class StateGeometricData {
    private Point2D location;
    private Point2D size;
//    private double x;
//    private double y;
//    private double width;
//    private double height;
    private String shape;

    public StateGeometricData() {
            this.location = null;
            this.size=null;
            
            this.shape = null;
    }
//    public StateGeometricData(Point2D location_,Point2D size_,String shape_){
//            this.location = location_;
//            this.size= size_;
//            this.shape = shape_;
//    }
    public StateGeometricData(double x_, double y_, double width_, double height_, String shape_){
        //x=x_; y=y_; width=width_; height=height_; 
        shape=shape_;
        location=new Point2D.Double(x_,y_);
        size=new Point2D.Double(width_,height_);
    }
    public void setLocation(Point2D location_){
        location=location_;
//        x=location_.getX();
//        y=location_.getY();
    }
    public void setSize(Point2D size_){
        size=size_;
//        width=size_.getX();
//        height=size_.getY();
    }
    public void setX(double x_){
//        x=x_;
        if(location==null) location=new Point2D.Double();
        
        location.setLocation(x_,location.getY());
    }
    public void setY(double y_){
//        y=y_;
        if(location==null) location=new Point2D.Double();
        location.setLocation(location.getX(),y_);
    }
    public void setWidth(double w_){
//        width=w_;
        if(size==null) size=new Point2D.Double();
        size.setLocation(w_, size.getY());
    }
    public void setHeight(double h_){
//        height=h_;
        
        if(size==null) size=new Point2D.Double();
        size.setLocation(size.getX(),h_);
    }
    public void setShape(String shape_){
        shape=shape_;
    }
    public Point2D getLocation(){
//        if(x>0 && y>0)
//            return new Point2D.Double(x,y);
//        else return null;
        return location;
    }
    public Point2D getSize(){
//        if(width>0 && height>0)
//            return new Point2D.Double(width,height);
//        else return null;
        return size;
    }
    public double getX(){
//        return x;
        if(location==null) location=new Point2D.Double();
        return location.getX();
    }
    public double getY(){
//        return y;
        
        if(location==null) location=new Point2D.Double();
        return location.getY();
    }
    public double getWidth(){
//        return width;
        if(size==null) size=new Point2D.Double();
        return size.getX();
    }
    public double getHeight(){
//        return height;
        if(size==null) size=new Point2D.Double();
        return size.getY();
    }
    public String getShape(){
        return shape;
    }
    
    
}
