package me.remainingtoast.toastclient.api.gui;

import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.theme.GameSenseTheme;
import me.remainingtoast.toastclient.client.module.client.ClickGUIModule;
import me.remainingtoast.toastclient.client.module.client.Colors;

import java.awt.*;

public class ToastTheme extends GameSenseTheme {
    public ToastTheme (ColorScheme scheme, int height, int border, int scroll) {
        super(scheme, height, border, scroll);
        this.scheme=scheme;
        panelRenderer=new ToastRenderer(0,height,border,scroll);
        containerRenderer=new ToastRenderer(1,height,border,scroll);
        componentRenderer=new ToastRenderer(2,height,border,scroll);
    }

    protected class ToastRenderer extends GameSenseTheme.ComponentRenderer {

        public ToastRenderer(int level, int height, int border, int scroll) {
            super(level, height, border, scroll);
        }

        @Override
        public Color getMainColor (boolean focus, boolean active) {
            Color color = Colors.INSTANCE.getCategoryBgColor().getValue();
            if (level==0 && active) return new Color(color.getRed(),color.getGreen(),color.getBlue(),getColorScheme().getOpacity());
            return super.getMainColor(focus,active);
        }
    }
}
