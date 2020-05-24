package toast.client.dontobfuscate;

import com.google.gson.annotations.SerializedName;

public class CategorySetting {
    @SerializedName("Pos X")
    private int posX;
    @SerializedName("Pos Y")
    private int posY;
    @SerializedName("Expanded")
    private boolean expanded;
    @SerializedName("Expanded Modules")
    private String[] expandedModules;

    public CategorySetting(int x, int y, boolean expanded, String[] expandedModules) {
        this.posX = x;
        this.posY = y;
        this.expanded = expanded;
        this.expandedModules = expandedModules;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public void setExpandedModules(String[] expandedModules) {
        this.expandedModules = expandedModules;
    }

    public void setPosX(int posx) {
        this.posX = posx;
    }

    public void setPosY(int posy) {
        this.posY = posy;
    }

    public String[] getExpandedModules() {
        return expandedModules;
    }
}
