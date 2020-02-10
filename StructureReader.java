/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lewis_structures;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
/**
 *
 * @author james
 */
public class StructureReader extends JPanel {
    private Lewis_Structures frame;
    private JTextField formula = new JTextField();
    private JComboBox<Integer> charge;
    private JCheckBox dshell = new JCheckBox();
    private JButton enter = new JButton("Calculate");
    public StructureReader(Lewis_Structures frame) {
        this.frame=frame;
        setLayout(new GridLayout(2,4));
        add(new JLabel("formula"));
        add(new JLabel("charge"));
        add(new JLabel("d-bonding"));
        add(new JLabel());
        Integer[] charges = new Integer[9];
        for (int i = 0; i < charges.length; i++) charges[i]=i-4;
        charge = new JComboBox<>(charges);
        charge.setSelectedItem(0);
        dshell.setSelected(true);
        add(formula);
        add(charge);
        add(dshell);
        enter.addActionListener((ActionEvent e)->new Updater(toAtoms(formula.getText()),((Integer)charge.getSelectedItem()),dshell.isSelected()).execute());
        add(enter);
        
    }
    private class Updater extends SwingWorker<LewisStructure[][],Object> {
        private String[] atoms;
        private int charge;
        private boolean dblock;
        public Updater(String[] atoms, int charge, boolean dblock) {
            this.atoms=atoms;
            this.charge=charge;
            this.dblock=dblock;
        }

        @Override
        protected LewisStructure[][] doInBackground() throws Exception {
            return LewisStructure.finalizedStructures(atoms, charge, dblock);
        }
        @Override
        protected void done() {
            try {
                frame.setStructure(get());
            } catch (InterruptedException ex) {
                Logger.getLogger(StructureReader.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ExecutionException ex) {
                Logger.getLogger(StructureReader.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }
    public static String[] toAtoms(String in) {
        ArrayList<String> out = new ArrayList<>();
        String atom = "";
        String numural = "";
        for (int i = 0; i < in.length(); i++) {
            char next = in.charAt(i);
            if (i!=0&&Character.isUpperCase(next)) {
                for (int number = numural.length()==0 ? 1 : Integer.parseInt(numural);number>0;number--) out.add(atom);
                atom="";
                numural="";
            }
            if (Character.isAlphabetic(next)) atom=atom+next;
            else if (Character.isDigit(next)) numural=numural+next;
        }
        for (int number = numural.length()==0 ? 1 : Integer.parseInt(numural);number>0;number--) out.add(atom);
        System.out.println(out.size());
        for (String next: out) System.out.print(next+" ");
        System.out.println();
        return out.toArray(new String[0]);
    }
}
