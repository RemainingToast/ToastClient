package toast.client.gui.clickgui;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class CategorySetting {
    @SerializedName("Pos X")
    private double posX;
    @SerializedName("Pos Y")
    private double posY;
    @SerializedName("Expanded")
    private boolean expanded;
    @SerializedName("Expanded Modules")
    private final ArrayList<String> expandedModules;

    public CategorySetting(double x, double y, boolean expanded, ArrayList<String> expandedModules) {
        this.posX = x;
        this.posY = y;
        this.expanded = expanded;
        this.expandedModules = expandedModules;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posx) {
        this.posX = posx;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posy) {
        this.posY = posy;
    }

    public ArrayList<String> getExpandedModules() {
        return expandedModules;
    }
}
