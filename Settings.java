/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lewis_structures;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 *
 * @author james
 */
public class Settings extends JPanel {
    private static final String[] colors = {"black","blue","gray","green","red","white","yellow"};
    private static final String[] fontStyles = {"bold","regular","italic"};
    private static final Integer[] fontSizes = {10,11,12,15,20,32};
    private final JButton last;
    private final JButton next;
    private final JButton play;
    private final ImageIcon playIcon;
    private final ImageIcon stopIcon;
    private final JComboBox<String> bondColor;
    private final JComboBox<String> textColor;
    private final JComboBox<String> electronColor;
    private final JComboBox<String> font;
    private final JComboBox<Integer> fontSize;
    private final JComboBox<String> fontStyle;
    private final Timer resonanceSwitcher;
    private final Lewis_Structures frame;
    public Settings(Lewis_Structures frame) {
        this.frame=frame;
        playIcon = new ImageIcon("play.png");
        stopIcon = new ImageIcon("stop.png");
        last = new JButton(new ImageIcon("Back.png"));
        next = new JButton(new ImageIcon("Next.png"));
        play = new JButton(playIcon);
        bondColor = new JComboBox<>(colors);
        textColor = new JComboBox<>(colors);
        electronColor = new JComboBox<>(colors);
        bondColor.setSelectedItem("black");
        textColor.setSelectedItem("black");
        electronColor.setSelectedItem("black");
        font = new JComboBox(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        font.setSelectedItem(new JLabel().getFont().getFamily());
        fontSize = new JComboBox(fontSizes);
        fontSize.setSelectedItem(new JLabel().getFont().getSize());
        fontStyle = new JComboBox(fontStyles);
        fontStyle.setSelectedItem("regular");
        resonanceSwitcher = new Timer(3000,(ActionEvent e) -> frame.nextResonance());
        last.addActionListener((ActionEvent e)-> frame.lastMolecule());
        next.addActionListener((ActionEvent e)-> frame.nextMolecule());
        play.addActionListener((ActionEvent ae) -> {
            if (resonanceSwitcher.isRunning()) {
                play.setIcon(playIcon);
                resonanceSwitcher.stop();
            } else {
                play.setIcon(stopIcon);
                resonanceSwitcher.start();
            }
        });
        bondColor.addActionListener((ActionEvent e) -> frame.setBondColor(getColor(bondColor.getItemAt(bondColor.getSelectedIndex()))));
        textColor.addActionListener((ActionEvent e) -> frame.setTextColor(getColor(textColor.getItemAt(textColor.getSelectedIndex()))));
        electronColor.addActionListener((ActionEvent e) -> frame.setElectronColor(getColor(electronColor.getItemAt(electronColor.getSelectedIndex()))));
        font.addActionListener((ActionEvent e) -> frame.setTextFont(font.getItemAt(font.getSelectedIndex())));
        fontSize.addActionListener((ActionEvent e) -> frame.setFontSize(fontSize.getItemAt(fontSize.getSelectedIndex())));
        fontStyle.addActionListener((ActionEvent e) -> frame.setFontStyle(fontStyle(fontStyle.getItemAt(fontStyle.getSelectedIndex()))));
        setLayout(new GridLayout(2,9));
        add(new JLabel());
        add(new JLabel());
        add(new JLabel());
        add(new JLabel("Bond Color"));
        add(new JLabel("Text Color"));
        add(new JLabel("Electron Color"));
        add(new JLabel("Font"));
        add(new JLabel("Font Size"));
        add(new JLabel("Font Style"));
        add(last);
        add(next);
        add(play);
        add(bondColor);
        add(textColor);
        add(electronColor);
        add(font);
        add(fontSize);
        add(fontStyle);
    }
    
    private Color getColor(String in) {
        Color color;
        switch (in) {
            case "white":
                color=Color.white;
                break;
            case "red":
                color=Color.red;
                break;
            case "blue":
                color=Color.blue;
                break;
            case "green":
                color=Color.green;
                break;
            case "yellow":
                color=Color.yellow;
                break;
            case "black":
                color=Color.black;
                break;
            default:
                color=Color.gray;
                break;
        }
        return color;
    }
    private int fontStyle(String style) {
        switch (style) {
            case "bold":
                return Font.BOLD;
            case "italic":
                return Font.ITALIC;
            default:
                return Font.PLAIN;
        }
    }
}
