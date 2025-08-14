package asia.virtualmc.vLib.core.skills.data.components;

public enum ComponentType {
    COMMON, UNCOMMON, RARE, UNIQUE, EPIC, MYTHICAL, EXOTIC;

    public int index() { return ordinal(); }
}