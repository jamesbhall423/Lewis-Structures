/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lewis_structures;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 *
 * @author james
 */
public class LewisStructure implements Serializable {
    private static final long serialVerisonUID = 14L;
    public static boolean paintOnce = false;

    private static double chargeGap(LewisStructure[] structures) {
        //System.out.println("Hello "+structures.length);
        //structures[0].print();
        double[] correctedFormalCharge = new double[structures[0].atoms.size()];
        for (LewisStructure structure: structures) for (int i = 0; i < correctedFormalCharge.length; i++) correctedFormalCharge[i]+=structure.atoms.get(i).formalCharge();
        double maxChargeGap = 0.0;
        for (int i1 = 0; i1 < correctedFormalCharge.length; i1++) for (int i2 = 0; i2 < correctedFormalCharge.length; i2++) {
            if (structures[0].atoms.get(i1).electronegitivity()>=structures[0].atoms.get(i2).electronegitivity()&&correctedFormalCharge[i1]-correctedFormalCharge[i2]>maxChargeGap) {
                maxChargeGap=correctedFormalCharge[i1]-correctedFormalCharge[i2];
            }
        }
        //System.out.println(maxChargeGap);
        return maxChargeGap/structures.length;
    }
    private Atom centralAtom;

    private final ArrayList<Atom> atoms = new ArrayList<>();
    public boolean add(Atom atom, int index, int trialNum) {
        boolean out = atoms.get(index).bondTo(atom, trialNum);
        atom.setIndex(atoms.size());
        atoms.add(atom);
        return out;
    }
    public static LewisStructure[] firstAssembly(String[] symbols) {
        HashSet<LewisStructure> out = new HashSet<>();
        LewisStructure start = new LewisStructure(symbols[0]);
        boolean[] covered = new boolean[symbols.length];
        covered[0]=true;
        for (int trialNum=0; out.isEmpty()&&trialNum<=2;trialNum++) addAssemble(out,start,symbols,covered, trialNum);
        return out.toArray(new LewisStructure[0]);
    }
    public static void addAssemble(HashSet<LewisStructure> out, LewisStructure next, String[] symbols, boolean[] covered, int trialNum) {
        HashSet<String> usedSymbols = new HashSet<>();
        for (int i = 0; i < symbols.length; i++) if (!covered[i]&&!usedSymbols.contains(symbols[i])) {
            usedSymbols.add(symbols[i]);
            for (int i2 = 0; i2 < next.numAtoms(); i2++) {
                LewisStructure next2 = next.copy();
                covered[i]=true;
                if (next2.add(new Atom(symbols[i]),i2,trialNum)) {
                    boolean allCovered = true;
                    for (int i3 = 0; i3 < covered.length; i3++) if (!covered[i3]) allCovered=false;
                    if (allCovered) out.add(next2);
                    else addAssemble(out,next2,symbols,covered, trialNum);
                    Atom atom1 = next.atoms.get(i2);
                    atom1.skipBond(symbols[i]);
                    for (int i3=i2+1; i3<next.numAtoms();i3++) {
                        Atom atom2 = next.atoms.get(i3);
                        if (branchEquals(atom1,atom2)) atom2.skipBond(symbols[i]);
                    }
                }
                covered[i]=false;
            }
        }
    }
    public LewisStructure(String symbol) {
        atoms.add(new Atom(symbol));
    }
    public int numAtoms() {
        return atoms.size();
    }
    
    public LewisStructure copy() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(buffer)) {
            out.writeObject(this);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        Object ret;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buffer.toByteArray()))) {
            ret = in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
        return (LewisStructure) ret;
    }
    public void print() {
        for (Atom atom: atoms) System.out.println(atom.toString());
        Atom[] centralAtoms = centralAtoms();
        String out = "Central Atoms:";
        for (int i = 0; i < centralAtoms.length; i++) out+=" "+centralAtoms[i];
        System.out.println(out);
    }
    public Atom[] centralAtoms() {
        ArrayList<Atom> out = new ArrayList<>();
        int[] maxDistances = new int[atoms.size()];
        for (int i = 0; i < maxDistances.length; i++) {
            for (Atom atom: atoms) atom.setPulse(false);
            ArrayList<Atom> next = new ArrayList<>();
            ArrayList<Atom> next2 = new ArrayList<>();
            next.add(atoms.get(i));
            atoms.get(i).setPulse(true);
            int i2;
            for (i2 = 0; next.size()>0; i2++) {
                for (Atom atom: next) for (Bond bond: atom.getBonds()) {
                    Atom other = bond.other(atom);
                    if (!other.getPulse()) {
                        other.setPulse(true);
                        next2.add(other);
                    }
                }
                next=next2;
                next2=new ArrayList<>();
            }
            maxDistances[i]=i2;
        }
        //for (int i = 0; i < maxDistances.length; i++) System.out.println(maxDistances[i]);
        int minMax = maxDistances[0];
        for (int i = 0; i < maxDistances.length; i++) if (maxDistances[i]<minMax) minMax=maxDistances[i];
        for (int i = 0; i < maxDistances.length; i++) if (maxDistances[i]==minMax) out.add(atoms.get(i));
        return out.toArray(new Atom[0]);
    }
    public static boolean branchEquals(Atom branch1, Atom branch2) {
        boolean out = true;
        if (!branch1.getSymbol().equals(branch2.getSymbol())) return false;
        Bond[] bonds1 = branch1.getBonds();
        Bond[] bonds2 = branch2.getBonds();
        if (bonds1.length!=bonds2.length) return false;
        int[] hashCodes1 = new int[bonds1.length];
        int[] hashCodes2 = new int[bonds2.length];
        branch1.setPulse(true);
        branch2.setPulse(true);
        for (int i = 0; i < bonds1.length; i++) {
            if (!bonds1[i].other(branch1).getPulse()) hashCodes1[i]=branchHashCode(bonds1[i].other(branch1));
            if (!bonds2[i].other(branch2).getPulse()) hashCodes2[i]=branchHashCode(bonds2[i].other(branch2));
        }
        boolean[] taken = new boolean[bonds2.length];
        for (int i = 0; i < bonds1.length&&out; i++) {
            boolean equivalent = false;
            boolean pulse1 = bonds1[i].other(branch1).getPulse();
            for (int i2 = 0; i2 < bonds2.length&&!equivalent; i2++)  {
                boolean pulse2 = bonds2[i2].other(branch2).getPulse();
                if (!taken[i2]&&(pulse1&&pulse2||!pulse1&&!pulse2&&hashCodes1[i]==hashCodes2[i2]&&branchEquals(bonds1[i].other(branch1),bonds2[i2].other(branch2)))) {
                    taken[i2]=true;
                    equivalent=true;
                }
            }
            if (!equivalent) out=false;
        }
        branch1.setPulse(false);
        branch2.setPulse(false);
        return out;
    }
    public static int branchHashCode(Atom branch) {
        int out = branch.getSymbol().hashCode();
        Bond[] bonds = branch.getBonds();
        branch.setPulse(true);
        for (Bond bond: bonds) {
            Atom other = bond.other(branch);
            if (!other.getPulse()) out+=3571*branchHashCode(other);
        }
        branch.setPulse(false);
        return out^1304957242;
    }
    public static int branchLength(Atom branch) {
        int out = 1;
        Bond[] bonds = branch.getBonds();
        branch.setPulse(true);
        for (Bond bond: bonds) {
            Atom other = bond.other(branch);
            if (!other.getPulse()) out+= branchLength(other);
        }
        branch.setPulse(false);
        return out;
    }
    @Override
    public int hashCode() {
        Atom[] central = centralAtoms();
        int out = 0;
        for (Atom atom: atoms) atom.setPulse(false);
        for (int i = 0; i < central.length; i++) out+=branchHashCode(central[i]);
        return out;
    }

    /**
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (o==null) return false;
        if (!LewisStructure.class.isInstance(o)) return false;
        LewisStructure struct = (LewisStructure) o;
        Atom[] central1 = centralAtoms();
        Atom[] central2 = struct.centralAtoms();
        flushPulse();
        struct.flushPulse();
        for (int i = 0; i < central1.length; i++) for (int i2 = 0; i2 < central2.length; i2++) if (branchEquals(central1[i],central2[i2])) return true;
        return false;
    }
    public void flushPulse() {
        for (Atom atom: atoms) atom.setPulse(false);
    }
    public boolean identicalBonds(LewisStructure other) {
        for (int i = 0; i < atoms.size(); i++) {
            Atom atom1 = atoms.get(i);
            Atom atom2 = other.atoms.get(i);
            Bond[] bonds1 = atom1.getBonds();
            Bond[] bonds2 = atom2.getBonds();
            for (int i2 = 0; i2 < bonds1.length; i2++) if (!bonds1[i2].equals(bonds2[i2])) return false;
            if (atom1.loneElectrons()!=atom2.loneElectrons()) return false;
        }
        return true;
    }
    public int resonanceHashCode() {
        int out = 0;
        int multiple = 1;
        Bond[] bonds = allBonds();
        for (int i = 0; i < bonds.length; i++) {
            out+=((int)(2*bonds[i].getOrder()))*multiple;
            multiple*=5;
        }
        for (Atom atom: atoms) {
            out+=atom.loneElectrons()*multiple;
            multiple*=5;
        }
        return out;
    }
    public LewisStructure[] resonances(int charge, boolean dblock) {
        if (!dblock) for (Atom atom: atoms) if (atom.hyperValent()) atom.dropShell();
        for (Atom atom: atoms) atom.completeOctet();
        int electronLack = sumFormalCharges()-charge;
        HashSet<ResonanceStructure> current = new HashSet<>();
        HashSet<ResonanceStructure> next = new HashSet<>();
        current.add(new ResonanceStructure(this));
        while (electronLack<-1) {
            changeBondOrder(current,next,true,false);
            current=next;
            next = new HashSet<>();
            electronLack+=2;
        }
        while (electronLack>1) {
            changeBondOrder(current,next,false,false);
            current=next;
            next = new HashSet<>();
            electronLack-=2;
        }
        //System.out.println("resonances: "+current.size());
        if (electronLack==-1) {changeBondOrder(current,next,true,true);current=next;}
        //System.out.println("Hello resonances: "+current.size()+" "+next.size());
        if (electronLack==1) {changeBondOrder(current,next,false,true);current=next;}
        //System.out.println("resonances: "+current.size()+" "+next.size());
        if (dblock) while (minimizeFormalCharges(current,next)) {
            current=next;
            next = new HashSet<>();
        }
        //System.out.println("resonances: "+current.size());
        return ResonanceStructure.structures(current);
    }
    public int sumFormalCharges() {
        double out = 0;
        for (Atom atom: atoms) {out += atom.formalCharge();}
        return (int)out;
    }
    public static void changeBondOrder(HashSet<ResonanceStructure> current, HashSet<ResonanceStructure> next, boolean add, boolean half) {
        //System.out.println("Change Bond Order: ");
        Loop:
        for (ResonanceStructure structureRes: current) {
            LewisStructure structure = structureRes.getStructure();
            //structure.printResonance();
            Bond[] bonds = structure.allBonds();
            boolean[] canBond = new boolean[bonds.length];
            double[] chargePreference = new double[bonds.length];
            double[] electronegitivityPreference = new double[bonds.length];
            if (add) structure.canAdd(bonds, canBond,half ? 1 : 2);
            else structure.canRemove(bonds, canBond,half ? 1 : 2);
            boolean insufficientElectrons = true;
            for (int i = 0; i < canBond.length; i++) if (canBond[i]) insufficientElectrons=false;
            if (insufficientElectrons) structure.canRemove(bonds, canBond, half ? 1 : 2);
            if (insufficientElectrons) {
                //System.out.println("Hello 1");
                HashSet<ResonanceStructure> temp = new HashSet<>();
                if (structure.removeLoneElectron(temp)) {
                    if (!half) {
                        for (ResonanceStructure tempStruct: temp) tempStruct.getStructure().removeLoneElectron(next);
                    } else next.addAll(temp);
                    continue Loop;
                }
            }
            double sign = add ? -1.0 : 1.0;
            for (int i = 0; i < bonds.length; i++) chargePreference[i]=sign*(bonds[i].atom1().formalCharge()+bonds[i].atom2().formalCharge());
            for (int i = 0; i < bonds.length; i++) electronegitivityPreference[i]=sign*(bonds[i].atom1().electronegitivity()+bonds[i].atom2().electronegitivity());
            double maxChargePreference = -10.0;
            for (int i = 0; i < bonds.length; i++) if (canBond[i]&&chargePreference[i]>maxChargePreference) {maxChargePreference=chargePreference[i];}
            //double maxElectronegitivityPreference = -10.0;
            //for (int i = 0; i < bonds.length; i++) if (canBond[i]&&chargePreference[i]==maxChargePreference&&electronegitivityPreference[i]>maxElectronegitivityPreference) maxElectronegitivityPreference=electronegitivityPreference[i];
            double toAdd = half ? 0.5 : 1.0;
            if (!add||insufficientElectrons) toAdd = -toAdd;
            for (int i = 0; i < bonds.length; i++) if (canBond[i]&&chargePreference[i]==maxChargePreference/*&&electronegitivityPreference[i]==maxElectronegitivityPreference*/) {
                LewisStructure out = structure.copy();
                Bond bondCopy = out.allBonds()[i];
                bondCopy.setOrder(bondCopy.getOrder()+toAdd);
                if (!insufficientElectrons) {
                    bondCopy.atom1().completeOctet();
                    bondCopy.atom2().completeOctet();
                }
                next.add(new ResonanceStructure(out));
            }
        }
        //System.out.println("change bond order" + next.size());
    }
    public Bond[] allBonds() {
        ArrayList<Bond> out = new ArrayList<>();
        for (Atom atom: atoms) {
            Bond[] bonds = atom.getBonds();
            for (Bond bond: bonds) if (atom==bond.atom1()) out.add(bond);
        }
        return out.toArray(new Bond[0]);
    }

    private void canAdd(Bond[] bonds, boolean[] canBond,int numElectrons) {
        for (int i = 0; i < bonds.length; i++) canBond[i] = bonds[i].getOrder()<=2.0&&bonds[i].atom1().loneElectrons()>=numElectrons&&bonds[i].atom2().loneElectrons()>=numElectrons;               
    }

    private void canRemove(Bond[] bonds, boolean[] canBond,int numElectrons) {
        for (int i = 0; i < bonds.length; i++) canBond[i] = bonds[i].getOrder()>=1.0;               
    }

    public void printResonance() {
        Bond[] bonds = allBonds();
        for (Bond bond: bonds) System.out.println(bond);
        for (Atom atom: atoms) System.out.println(atom+" Lone Electrons = "+atom.loneElectrons());
    }
    public boolean removeLoneElectron(HashSet<ResonanceStructure> next) {
        boolean ret = false;
            boolean[] hasElectrons = new boolean[atoms.size()];
            for (int i = 0; i < hasElectrons.length; i++) hasElectrons[i]=(atoms.get(i).loneElectrons()>=1);
            double minFormalCharge = 10.0;
            for (int i = 0; i < atoms.size(); i++) if (hasElectrons[i]&&atoms.get(i).formalCharge()<minFormalCharge) minFormalCharge=atoms.get(i).formalCharge();
            double minElectronegitivity = 10.0;
            for (int i = 0; i < atoms.size(); i++) if (hasElectrons[i]&&atoms.get(i).formalCharge()==minFormalCharge&&atoms.get(i).electronegitivity()<minElectronegitivity) minElectronegitivity=atoms.get(i).electronegitivity();
            for (int i = 0; i < atoms.size(); i++) if (hasElectrons[i]&&atoms.get(i).formalCharge()==minFormalCharge&&atoms.get(i).electronegitivity()==minElectronegitivity) {
                ret = true;
                LewisStructure toAdd = copy();
                Atom atomCopy = toAdd.atoms.get(i);
                atomCopy.setLoneElectrons(atomCopy.loneElectrons()-1);
                next.add(new ResonanceStructure(toAdd));
            }
        //System.out.println("remove lone electron: " + next.size()+" "+ret);
        return ret;
    }

    private static boolean minimizeFormalCharges(HashSet<ResonanceStructure> current, HashSet<ResonanceStructure> next) {
        boolean adding = false;
        for (ResonanceStructure structureRes: current) {
            LewisStructure structure = structureRes.getStructure();
            double maxChargeDifference = 0.0;
            Bond[] bonds = structure.allBonds();
            double[] dif1 = new double[bonds.length];
            double[] dif2 = new double[bonds.length];
            for (int i = 0; i < bonds.length; i++) {
                dif1[i] = chargeDifference(bonds[i],bonds[i].atom1(),bonds[i].atom2());
                dif2[i] = chargeDifference(bonds[i],bonds[i].atom2(),bonds[i].atom1());
                if (dif1[i]>maxChargeDifference) maxChargeDifference=dif1[i];
                if (dif2[i]>maxChargeDifference) maxChargeDifference=dif2[i];
            }
            if (maxChargeDifference>=2.0) {
                adding=true;
                for (int i = 0; i < bonds.length; i++) {
                    LewisStructure copy = structure.copy();
                    if (dif1[i]==maxChargeDifference) {
                        Bond[] bondsCopy = copy.allBonds();
                        bondsCopy[i].setOrder(bondsCopy[i].getOrder()+1.0);
                        bondsCopy[i].atom2().setLoneElectrons(bondsCopy[i].atom2().loneElectrons()-2);
                        next.add(new ResonanceStructure(copy));
                    } else if (dif2[i]==maxChargeDifference) {
                        Bond[] bondsCopy = copy.allBonds();
                        bondsCopy[i].setOrder(bondsCopy[i].getOrder()+1.0);
                        bondsCopy[i].atom1().setLoneElectrons(bondsCopy[i].atom1().loneElectrons()-2);
                        next.add(new ResonanceStructure(copy));
                    }
                }
            }
        }
        return adding;
    }
    private static double chargeDifference(Bond bond, Atom expander, Atom donator) {
        if (bond.getOrder()<=2.0&&donator.loneElectrons()>=2&&expander.dblockCompatible()) return expander.formalCharge()-donator.formalCharge();
        else return 0.0;
    }
    public static boolean[] bestStructures(LewisStructure[] in, double[] chargeGaps) {
        boolean[] out = new boolean[in.length];
        Arrays.fill(out, true);
        boolean octet=false;
        for (int i = 0; i < in.length&&!octet; i++) if (in[i].allOctet()) octet=true;
        if (!octet) return out;
        for (int i = 0; i < in.length; i++) out[i]=in[i].allOctet();
        double minChargeGap = 10.0;
        if (chargeGaps==null) {
            chargeGaps = new double[in.length];
            for (int i = 0; i < in.length;i++) if (out[i]) chargeGaps[i]=in[i].chargeGap();
        }
        for (int i = 0; i < in.length; i++) if (out[i]&&chargeGaps[i]<minChargeGap) minChargeGap=chargeGaps[i];
        for (int i = 0; i < in.length; i++) if (out[i]&&chargeGaps[i]>minChargeGap) out[i]=false;
        return out;
    }
    public static LewisStructure[][] finalizedStructures(String[] symbols, int charge, boolean dblock) {
        //System.out.println("Hello 3");
        LewisStructure[] firstAssemble = firstAssembly(symbols);
        //System.out.println("Hello 4");
        LewisStructure[][] out = new LewisStructure[firstAssemble.length][1];
        for (int i = 0; i < out.length; i++) out[i][0]=firstAssemble[i];
        for (int i = 0; i < out.length; i++) out[i]=out[i][0].resonances(charge, dblock);
        LewisStructure[] convert = new LewisStructure[out.length];
        double[] chargeGaps = new double[out.length];
        for (int i = 0; i < out.length; i++) {
            boolean[] valid = bestStructures(out[i],null);
            ArrayList<LewisStructure> innerConvert=new ArrayList<>();
            for (int i2 = 0; i2 < out[i].length; i2++) if (valid[i2]) innerConvert.add(out[i][i2]);
            out[i] = innerConvert.toArray(new LewisStructure[0]);
            convert[i]=out[i][0];
            chargeGaps[i]=chargeGap(out[i]);
        }
        ArrayList<LewisStructure[]> listOut = new ArrayList<>();
        boolean[] valid = bestStructures(convert,chargeGaps);
        for (int i = 0; i < out.length; i++) if (valid[i]) listOut.add(out[i]);
        LewisStructure[][] structures = listOut.toArray(new LewisStructure[0][]);
        //System.out.println("Hello 5");
        for (LewisStructure[] next: structures) for (LewisStructure structure: next) {
            //System.out.println("Hello 6");
            structure.centralAtom = structure.centralAtoms()[0];
            structure.flushPulse();
            //System.out.println("Hello 7");
            structure.centralAtom.assignBondAngles(null);
            //System.out.println("Hello 8");
            structure.centralAtom.assignXY(null, 0, 0);
            //System.out.println("Hello 9");
            //structure.print();
            structure.centralAtom.resolveIntersection(null);
            //System.out.println("Hello 10");
        }
        return structures;
    }

    private boolean allOctet() {
        for (Atom atom: atoms) if (!atom.hasOctet()) return false;
        return true;
    }

    private double chargeGap() {
        double maxChargeGap = 0.0;
        for (Atom atom1: atoms) for (Atom atom2: atoms) if (atom1.electronegitivity()>=atom2.electronegitivity()&&atom1.formalCharge()-atom2.formalCharge()>maxChargeGap)  maxChargeGap=atom1.formalCharge()-atom2.formalCharge();
        return maxChargeGap;
    }
    public void paint(Graphics2D g, Color symbolColor, Color bondColor, Color loneColor, Font font) {
        //if (paintOnce) System.out.println("paint");
        flushPulse();
        centralAtom.paint(null,g,symbolColor,bondColor,loneColor,font);
        paintOnce=false;
    }
}
