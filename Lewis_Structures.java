/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lewis_structures;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
/**
 *
 * @author james
 */
public class Lewis_Structures extends JFrame {
    private LewisStructure[][] structures;
    private int type = 0;
    private int frame = 0;
    private Color textColor = Color.black;
    private Color bondColor = Color.black;
    private Color electronColor = Color.black;
    private Font font = new JLabel().getFont();
    public Lewis_Structures() {
        setLayout(new BorderLayout());
        add(new StructureReader(this),BorderLayout.NORTH);
        //String[] symbols = {"S","S","F","F","F","F","F","F","F","F","F","F"};
        //System.out.println("Hello 1");
        //structures = LewisStructure.finalizedStructures(symbols, 0, false);
        //System.out.println("Hello 2");
        JPanel panel = new JPanel() {
            @Override
            public void paint(Graphics in) {
                Graphics2D g = (Graphics2D) in;
                AffineTransform pastTransform = g.getTransform();
                g.translate(getWidth()/2,getHeight()/2);
                if (structures!=null) {
                    structures[type][frame].paint(g,textColor,bondColor,electronColor,font);
                }
                g.setTransform(pastTransform);
            }
        };
        add(new Settings(this),BorderLayout.SOUTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        add(panel,BorderLayout.CENTER);
        setSize(900,600);
        setTitle("Lewis Structures");
        setVisible(true);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //System.out.println("C"+'l');
        SwingUtilities.invokeLater(()->new Lewis_Structures());
        //Scanner keyboard = new Scanner(System.in);
        //String[] symbols = {"C","C","C","C","O","H","H","F","Cl","H","H"};//new String[20];
        /*ArrayList<String> symbols = new ArrayList<>();
        
        //for (int i = 0; i < 14; i++) symbols[i]="H";
        //for (int i = 14; i < 20; i++) symbols[i]="C";
        for (int i = 0; i < 6; i++) symbols.add("C");
        symbols.add("H");
        symbols.add("H");
        */
        /*String[] symbols = {"S","S","F","F","F","F","F","F","F","F","F","O","H"};
        LewisStructure[][] structures = LewisStructure.finalizedStructures(symbols, 0, true);
        for (int i = 0; i < structures.length; i++) {
            System.out.println("Structure "+i);
            //structures[i][0].print();
            structures[i][0].printResonance();
            //for (int i2 = 0; i2 < structures[i].length; i2++) {structures[i][i2].printResonance();System.out.println();}
            System.out.println();
        }*/
        /*LewisStructure[] resonances = structures[keyboard.nextInt()].resonances(0, true);
        System.out.println();
        for (int i = 0; i < resonances.length; i++) resonances[i].printResonance();*/
    }

    public void setStructure(LewisStructure[][] structure) {
        structures=structure;
        type=0;
        frame=0;
        repaint();
    }

    public void nextResonance() {
        if (structures!=null) {
            frame++;
            if (frame>=structures[type].length) frame=0;
            repaint();
        }
    }

    public void lastMolecule() {
        if (structures!=null) {
            type--;
            if (type<0) type=structures.length-1;
            repaint();
        }
    }

    public void nextMolecule() {
        if (structures!=null) {
            type++;
            if (type>=structures.length) type=0;
            repaint();
        }
    }

    public void setBondColor(Color color) {
        bondColor=color;
        repaint();
    }

    public void setTextColor(Color color) {
        textColor=color;
        repaint();
    }

    public void setElectronColor(Color color) {
        electronColor=color;
        repaint();
    }

    public void setTextFont(String name) {
        font = new Font(name,font.getStyle(),font.getSize());
        repaint();
    }

    public void setFontSize(int size) {
        font = font.deriveFont((float) size);
        repaint();
    }

    public void setFontStyle(int style) {
        font = font.deriveFont(style);
        repaint();
    }
    
}
