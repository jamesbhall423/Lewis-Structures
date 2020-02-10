/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lewis_structures;

import java.awt.Graphics2D;
import java.awt.Font;
import java.awt.Color;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.HashSet;
import javax.swing.JLabel;
/**
 *
 * @author james
 */
public class Atom implements Serializable {
    private class Point {
        public double xval;
        public double yval;
        public int indexVal;

        private Point(double x, double y, int index) {
            xval=x;
            yval=y;
            indexVal=index;
        }
    }
    private static final long serialVerisonUID = 15L;
    private ArrayList<Bond> bonds;
    private String symbol;
    private Font chargeFont = new JLabel().getFont().deriveFont(Font.ITALIC, 9.0F);
    private int baseElectrons;
    private int loneElectrons;
    private double x;
    private double y;
    private int index = 0;
    private boolean pulse = false;
    private HashSet<String> noBond = null;
    public Atom(String symbol) {
        bonds = new ArrayList<>();
        this.symbol=symbol;
        switch (symbol) {
            case "H":
                baseElectrons = 1;
                break;
            case "B":
                baseElectrons = 3;
                break;
            case "C":
            case "Si":
            case "Ge":
                baseElectrons = 4;
                break;
            case "N":
            case "P":
            case "As":
            case "Sb":
                baseElectrons = 5;
                break;
            case "O":
            case "S":
            case "Se":
            case "Te":
                baseElectrons = 6;
                break;
            case "F":
            case "Cl":
            case "Br":
            case "I":
            case "At":
                baseElectrons = 7;
                break;
            case "Kr":
            case "Xe":
            case "Rn":
                baseElectrons = 8;
        }
    }

    public void addBond(Bond bond) {
        bonds.add(bond);
    }
    public boolean bondTo(Atom other, int trialNum) {
        boolean out = bonds.size()<maxBonds(trialNum)&&!noBond(other.getSymbol());
        if (out) new Bond(this,other);
        return out;
    }
    public boolean noBond(String in) {
        if (noBond==null) noBond=new HashSet<>();
        return noBond.contains(in);
    }
    public void skipBond(String in) {
        if (noBond==null) noBond=new HashSet<>();
        noBond.add(in);
    }
    public String getSymbol() {
        return symbol;
    }
    public int maxBonds(int trialNum) {
        switch (symbol) {
            case "H":
            case "F":
                return trialNum < 2 ? 1 : 2;
            case "O":
                return trialNum < 1 ? 2 : 3;
            case "B":
            case "C":
            case "N":
            case "Si":
            case "Ge":
                return 4;
            default:
                return baseElectrons;
        }
    }
    public void completeOctet() {
        int inBonds = sumOrder();
        if (symbol.equals("H")) loneElectrons=2-inBonds;
        else if (symbol.equals("B")) loneElectrons = inBonds<=6 ? 6-inBonds : 0;
        else loneElectrons = inBonds<=8 ? 8-inBonds : 0;
        if (loneElectrons<0) loneElectrons=0;
    }
    public int sumOrder() {
        double out = 0.0;
        for (Bond bond: bonds) {
            out+=bond.getOrder();
        }
        return (int) (2*out);
    }
    public double formalCharge() {
        return baseElectrons-(loneElectrons+sumOrder()/2.0);
    }
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index=index;
    }
    @Override
    public String toString() {
        String out = "Atom "+symbol+", "+index+" ("+Math.round(x)+","+Math.round(y)+") Bonded to #s";
        for (Bond bond: bonds) out+=" "+bond.other(this).getIndex();
        out+=".";
        return out;
    }
    public void setPulse(boolean pulse) {
        this.pulse=pulse;
    }
    public boolean getPulse() {
        return pulse;
    }
    public Bond[] getBonds() {
        return bonds.toArray(new Bond[0]);
    }
    public double electronegitivity() {
        switch (symbol) {
            case "H":
                return 2.2;
            case "B":
                return 2.04;
            case "C":
                return 2.55;
            case "Si":
                return 1.9;
            case "Ge":
                return 2.01;
            case "N":
                return 3.04;
            case "P":
                return 2.19;
            case "As":
                return 2.18;
            case "Sb":
                return 2.05;
            case "O":
                return 3.44;
            case "S":
                return 2.58;
            case "Se":
                return 2.55;
            case "Te":
                return 2.1;
            case "F":
                return 3.98;
            case "Cl":
                return 3.16;
            case "Br":
                return 2.96;
            case "I":
                return 2.66;
            case "At":
                return 2.2;
            case "Kr":
                return 3.0;
            case "Xe":
                return 2.6;
            case "Rn":
                return 2.2;
            default:
                throw new Error("Undifined symbol: "+symbol);
        }
    }
    public void setLoneElectrons(int loneElectrons) {
        this.loneElectrons=loneElectrons;
    }
    public int loneElectrons() {
        return loneElectrons;
    }

    public boolean hyperValent() {
        return bonds.size()>4;
    }

    public void dropShell() {
        for (Bond bond: bonds) bond.setOrder(0.0);
    }

    boolean dblockCompatible() {
        switch (symbol) {
            case "H":
            case "B":
            case "C":
            case "N":
            case "O":
            case "F":
                return false;
            default:
                return true;
        }
    }

    public boolean hasOctet() {
        int bondingElectrons = sumOrder()+loneElectrons;
        if (symbol.equals("H")) return bondingElectrons>=2;
        if (symbol.endsWith("B")) return bondingElectrons>=6;
        return bondingElectrons>=8;
    }
    public void assignBondAngles(Bond last) {
        double inc = 2*(Math.PI-0.001)/bonds.size();
        if (last==null) {
            double nextDir = 0.0;
            for (Bond bond: bonds) {
                bond.setAngle(nextDir, this);
                bond.other(this).assignBondAngles(bond);
                nextDir+=inc;
            }
        } else {
            bonds.remove(last);
            bonds.add(last);
            setPulse(true);
            for (int i1 = 0; i1 < bonds.size()-1; i1++) for (int i2 = i1+1; i2 < bonds.size()-1; i2++) {
                Bond bond1 = bonds.get(i1);
                Bond bond2 = bonds.get(i2);
                if (LewisStructure.branchLength(bond1.other(this))<LewisStructure.branchLength(bond2.other(this))) {
                    bonds.set(i1, bond2);
                    bonds.set(i2, bond1);
                }
            }
            double[] incValues = new double[bonds.size()-1];
            double angle = inc;
            for (int i = 0; i < incValues.length; i++) {
                incValues[i]=angle;
                angle+=inc;
            }
            for (int i = 0; i < incValues.length; i++) for (int j = 0; j < incValues.length; j++) if (Math.abs(incValues[i]-Math.PI)<Math.abs(incValues[j]-Math.PI)) {
                double temp = incValues[i];
                incValues[i]=incValues[j];
                incValues[j]=temp;
            }
            double inAngle=last.getAngle(this);
            for (int i = 0; i < incValues.length; i++) {
                bonds.get(i).setAngle(incValues[i]+inAngle, this);
                bonds.get(i).other(this).assignBondAngles(bonds.get(i));
            }
            setPulse(false);
        }
    }
    public void assignXY(Bond last, double x, double y) {
        this.x=x;
        this.y=y;
        for (Bond bond: bonds) if (bond!=last) {
            double angle = bond.getAngle(this);
            bond.other(this).assignXY(bond, x+Math.cos(angle)*bond.getLength(), y+Math.sin(angle)*bond.getLength());
        }
    }
    public void resolveIntersection(Bond last) {
        //System.out.println("Hello b");
        boolean conflict;
        //System.out.println("Hello a "+index);
        for (Bond bond: bonds) if (bond!=last) bond.other(this).resolveIntersection(bond);
        do {
            conflict=false;
            //System.out.println("Hello b "+index);
            ArrayList<Point> points = new ArrayList<>();
            aquireValues(last,points);
            int index1 = 0;
            int index2 = 0;
            for (Point p1: points) for (Point p2: points) if (p1!=p2) {
                double x1 = p1.xval;
                double y1 = p1.yval;
                double x2 = p2.xval;
                double y2 = p2.yval;
                if (Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))<45) {
                    conflict=true;
                    index1=p1.indexVal;
                    index2=p2.indexVal;
                }
            }
            if (conflict) {
                //System.out.println("Hello a");
                //System.out.println(bonds.size());
                for (Bond bond: bonds) if (bond!=last) {
                    //System.out.println("Hello c");
                    Atom other = bond.other(this);
                    //System.out.println(other.getBonds().length+" "+other.intersect(bond, index1, index2));
                    if (other.getBonds().length>1&&(other.intersect(bond,index1,index2))) {
                        //System.out.println("Hello n");
                        bond.setLength(bond.getLength()+25);
                    }
                }
                assignXY(last,x,y);
            }
        } while (conflict);
    }

    private void aquireValues(Bond last, ArrayList<Point> points) {
        //System.out.println("Hello c "+index);
        for (Bond bond: bonds) if (bond!=last) bond.other(this).aquireValues(bond,points);
        points.add(new Point(x,y,index));
    }

    private boolean intersect(Bond last, int index1, int index2) {
        //System.out.println("Intersect "+symbol+" "+index+" "+index1+" "+index2);
        for (Bond bond: bonds) if (bond!=last&&bond.other(this).intersect(bond,index1,index2)) return true;
        //System.out.println("Intersect End "+symbol+" "+index+" "+index1+" "+index2);
        return (index==index1||index==index2);
    }
    public double paint(Bond last, Graphics2D g, Color symbolColor, Color bondColor, Color loneColor, Font font) {
        Rectangle2D textBounds = new TextLayout(symbol,font,g.getFontRenderContext()).getBounds();
        double xstart = x-textBounds.getCenterX();
        double ystart = y-textBounds.getCenterY();
        double atomicRadius = Math.sqrt(textBounds.getWidth()*textBounds.getWidth()+textBounds.getHeight()*textBounds.getHeight());
        for (Bond bond: bonds) if (bond!=last) {
            bond.paint(g, this, atomicRadius+4.0,symbolColor,bondColor,loneColor,font);
        }
        g.setColor(symbolColor);
        g.setFont(font);
        g.drawString(symbol, (float)xstart, (float)ystart);
        double charge = formalCharge();
        if (charge!=0.0) {
            g.setFont(chargeFont);
            String next;
            if (charge==1.0) next = "+";
            else if (charge==-1.0) next = "--";
            else if (charge>0&&charge==Math.floor(charge)) next = ((int) charge)+"+";
            else if (charge<0&&charge==Math.floor(charge)) next = ((int) charge)+"-";
            else next = ""+charge;
            g.drawString(next+" ",(float)(x+atomicRadius*0.7),(float)(y-atomicRadius*0.7-4.0));
        }
        double lonePairs = Math.ceil(loneElectrons/2.0);
        double domainsPerBond = Math.ceil(lonePairs/bonds.size())+1;
        double domainSize = 2.0*Math.PI/Math.ceil(domainsPerBond*bonds.size());
        //if (LewisStructure.paintOnce) System.out.println(index+" "+lonePairs+" "+domainsPerBond+" "+domainSize);
        int bondNum = 0;
        int domainNum = 1;
        g.setColor(loneColor);
        for (int remainingElectrons = loneElectrons; remainingElectrons>0; remainingElectrons-=2) {
            double angle = bonds.get(bondNum).getAngle(this)+domainNum*domainSize;
            if (remainingElectrons==1) drawElectron(g,atomicRadius,angle);
            else {
                drawElectron(g,atomicRadius, angle-3.0/atomicRadius);
                drawElectron(g,atomicRadius,angle+3.0/atomicRadius);
            }
            bondNum++;
            if (bondNum>=bonds.size()) {
                bondNum=0;
                domainNum++;
            }
        }
        return atomicRadius+4.0;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }

    private void drawElectron(Graphics2D g, double atomicRadius, double angle) {
        int newX = (int)(x+Math.cos(angle)*atomicRadius);
        int newY = (int)(y+Math.sin(angle)*atomicRadius);
        g.fillOval(newX-1,newY-1,2,2);
    }
}
