package asia.virtualmc.vLib.core.skills.data.components;

import java.util.Arrays;

public final class Components {
    private final int[] values = new int[7];

    public Components() {}

    public Components(int common, int uncommon, int rare, int unique, int epic, int mythical, int exotic) {
        values[0]=common; values[1]=uncommon; values[2]=rare; values[3]=unique;
        values[4]=epic; values[5]=mythical; values[6]=exotic;
    }

    public int get(ComponentType type) { return values[type.index()]; }
    public void add(ComponentType type, int delta) { values[type.index()] += delta; }
    public void addAll(int delta) { for (int i=0;i<values.length;i++) values[i]+=delta; }
    public void addArray(int[] deltas) { for (int i=0;i<values.length;i++) values[i]+=deltas[i]; }

    public int[] toArray() { return Arrays.copyOf(values, values.length); }

    // defensive set (optional)
    public void set(ComponentType type, int value) { values[type.index()] = value; }
}
