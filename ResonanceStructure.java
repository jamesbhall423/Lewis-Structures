/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lewis_structures;

import java.util.HashSet;

/**
 *
 * @author james
 */
public class ResonanceStructure {

    private final LewisStructure structure;
    public ResonanceStructure(LewisStructure structure) {
        this.structure=structure;
    }
    @Override
    public boolean equals(Object other) {
        if (other==null) return false;
        if (!ResonanceStructure.class.isInstance(other)) return false;
        else return structure.identicalBonds(((ResonanceStructure)other).structure);
    }

    @Override
    public int hashCode() {
        return structure.resonanceHashCode();
    }
    public static LewisStructure[] structures(HashSet<ResonanceStructure> in) {
        ResonanceStructure[] resonances = in.toArray(new ResonanceStructure[0]);
        LewisStructure[] out = new LewisStructure[resonances.length];
        for (int i = 0; i < out.length; i++) out[i]=resonances[i].structure;
        return out;
    }

    public LewisStructure getStructure() {
        return structure;
    }
}
