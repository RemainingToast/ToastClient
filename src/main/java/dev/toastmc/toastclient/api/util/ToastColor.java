package dev.toastmc.toastclient.api.util;

import com.mojang.blaze3d.platform.GlStateManager;

import java.awt.*;

public class ToastColor extends java.awt.Color {

    public static ToastColor rainbow() {
        float hue = (System.currentTimeMillis() % (320 * 32)) / (320f * 32);
        return new ToastColor(ToastColor.fromHSB(hue, 1, 1, 255));
    }

    private static final long serialVersionUID = 1L;

    public ToastColor(int rgb) {
        super(rgb);
    }

    public ToastColor(int rgba, boolean alpha) {
        super(rgba,alpha);
    }

    public ToastColor(int r, int g, int b) {
        super(r,g,b);
    }

    public ToastColor(int r, int g, int b, int a) {
        super(r,g,b,a);
    }

    public ToastColor(java.awt.Color color) {
        super(color.getRed(),color.getGreen(),color.getBlue(),color.getAlpha());
    }

    public ToastColor(ToastColor color, int a) {
        super(color.getRed(),color.getGreen(),color.getBlue(),a);
    }

    public static ToastColor fromHSB(float hue, float saturation, float brightness, int alpha) {
        Color hsbColor = java.awt.Color.getHSBColor(hue,saturation,brightness);
        return new ToastColor(hsbColor.getRed(), hsbColor.getGreen(), hsbColor.getBlue(), alpha);
    }

    public float getHue() {
        return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[0];
    }

    public float getSaturation() {
        return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[1];
    }

    public float getBrightness() {
        return RGBtoHSB(getRed(),getGreen(),getBlue(),null)[2];
    }

    public int getABGRPackedInt() {
        int i = -1;
        try {
            i = Integer.parseInt("0x" + Integer.toHexString(getRGB()));
        } catch (NumberFormatException ignored) { }
        return i;
    }

    public void glColor() {
        GlStateManager.color4f(getRed()/255.0f,getGreen()/255.0f,getBlue()/255.0f,getAlpha()/255.0f);
    }
}
