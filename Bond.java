/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lewis_structures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.io.Serializable;

/**
 *
 * @author james
 */
public class Bond implements Serializable {
    private static final long serialVerisonUID = 16L;
    private Atom atom1;
    private Atom atom2;
    private double angle;
    private double length = 50.0;
    private double order = 1.0;
    public Bond(Atom atom1, Atom atom2) {
        this.atom1=atom1;
        this.atom2=atom2;
        atom1.addBond(this);
        atom2.addBond(this);
    }
    public double getOrder() {
        return order;
    }
    public void setOrder(double order) {
        this.order=order;
    }
    public Atom other(Atom in) {
        return in==atom1 ? atom2 : atom1;
    }

    /**
     *
     * @param other
     * @return
     */
    @Override
    public boolean equals(Object other) {
        if (other==null) return false;
        if (!Bond.class.isInstance(other)) return false;
        Bond bond2 = (Bond) other;
        return order==bond2.order&&atom1.getIndex()==bond2.atom1.getIndex()&&atom2.getIndex()==bond2.atom2.getIndex();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (int) (Double.doubleToLongBits(this.order) ^ (Double.doubleToLongBits(this.order) >>> 32))+atom1.getIndex()+atom2.getIndex();
        return hash;
    }
    public Atom atom1() {
        return atom1;
    }
    public Atom atom2() {
        return atom2;
    }
    @Override
    public String toString() {
        return atom1.getSymbol()+","+atom1.getIndex()+" : "+atom2.getSymbol()+","+atom2.getIndex()+" Order: "+order+" Angle: "+(Math.round(360.0*angle/(2*Math.PI)));
    }
    public void setAngle(double angle, Atom atom) {
        this.angle = Math.IEEEremainder(atom==atom1 ? angle : angle-Math.PI, 2*Math.PI);
    }
    public double getAngle(Atom atom) {
        return Math.IEEEremainder(atom==atom1 ? angle : angle-Math.PI, 2*Math.PI);
    }
    public void setLength(double length) {
        this.length=length;
    }
    public double getLength() {
        return length;
    }

    public void paint(Graphics2D g, Atom atom, double atomicRadius1, Color symbolColor, Color bondColor, Color loneColor, Font font) {
        double atomicRadius2 = other(atom).paint(this,g,symbolColor,bondColor,loneColor,font);
        double x1 = atom.getX()+atomicRadius1*Math.cos(getAngle(atom));
        double y1 = atom.getY()+atomicRadius1*Math.sin(getAngle(atom));
        double x2 = other(atom).getX()+atomicRadius2*Math.cos(getAngle(other(atom)));
        double y2 = other(atom).getY()+atomicRadius2*Math.sin(getAngle(other(atom)));
        g.setColor(bondColor);
        if (order>=1.0) drawLineSegment(g,x1,y1,x2,y2);
        double dx = x2-x1;
        double dy = y2-y1;
        double xshift = 10.0*dy/length;
        double yshift = -10.0*dx/length;
        if (order>=2.0) drawLineSegment(g,x1+xshift,y1+yshift,x2+xshift,y2+yshift);
        if (order>=3.0) drawLineSegment(g,x1-xshift,y1-yshift,x2-xshift,y2-yshift);
        if (order-Math.floor(order)>0.0) drawHalfBond(g,x1-xshift,y1-yshift,x2-xshift,y2-yshift);
    }
    private void drawLineSegment(Graphics2D g, double x1, double y1, double x2, double y2) {
        //System.out.println(x1+" "+y1+" "+x2+" "+y2);
        g.draw(new Line2D.Double(x1,y1,x2,y2));
        //g.draw(new Line2D.Double(x1-1,y1,x2-1,y2));
        //g.draw(new Line2D.Double(x1,y1-1,x2,y2-1));
    }

    private void drawHalfBond(Graphics2D g, double x1, double y1, double x2, double y2) {
        double dx = (x2-x1)/7.0;
        double dy = (y2-y1)/7.0;
        boolean draw = true;
        for (int i = 0; i < 7; i++) {
            if (draw) drawLineSegment(g,x1,y1,x1+dx,y1+dy);
            x1+=dx;
            y1+=dy;
            draw=!draw;
        }
    }
}
