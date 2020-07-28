package dev.toastmc.client.module;

public enum Category {
    COMBAT("Combat", false),
    RENDER("Render", false),
    MISC("Misc", false),
    PLAYER("Player", false),
    MOVEMENT("Movement", false),
    NONE("NONE", true);

    boolean hidden;
    String name;

    Category(String name, boolean hidden) {
        this.name = name;
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getName() {
        return name;
    }
}
