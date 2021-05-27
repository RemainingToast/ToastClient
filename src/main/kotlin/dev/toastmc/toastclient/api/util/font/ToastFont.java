package dev.toastmc.toastclient.api.util.font;

import me.remainingtoast.toastclient.api.util.Vector2f;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;

/**
 * A class that allows for text of any font to be drawn in OpenGL
 * The usage for this class can vary depending on your needs. If you are creating a simple game, without shaders, and need no consideration for performance, or are new to opengl and need something easy, then using {@link #draw(float, float, Color)} works fine.
 * On the other hand, if you are a power-user who needs to have access to every part of the render pipeline, using methods like {@link #sizeOfString()}, to get the width and height of the string in pixels when drawn, and {@link #getTexture()}, to retrieve the texture information about the text may be well suited for your needs.
 * This allows you to render text with any sort of shader or custom rendering system you like
 *
 * @author Charlie "Rock" Quigley
 * @see Texture
 * @see BufferedImage
 * @see Vector2f
 */

public class ToastFont {

    /** FontMetrics for information about the font */
    private FontMetrics metrics;

    /** font to use */
    private Font font;

    /** String */
    private String str;

    /** String Texture */
    private Texture tex;

    /** graphics for stuff */
    private	Graphics2D graphics;

    /**
     * Create some OpenGL compatible text
     * @param font The AWTFont to use
     * @param str The string to use (can be updated later)
     */
    public ToastFont(Font font, String str) {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        graphics = gc.createCompatibleImage(1, 1, Transparency.TRANSLUCENT).createGraphics();
        graphics.setFont(font);
        metrics = graphics.getFontMetrics(font);
        this.font = font;
        this.str = str;
        this.tex = new Texture(getImage(), false);
    }

    /**
     * Get the pixel size of the string when drawn
     * @return
     */
    public Vector2f sizeOfString() {
        return new Vector2f(
                metrics.stringWidth(str),
                metrics.getHeight()
        );
    }

    /**
     * Sets the font and updates the Graphics, FontMetrics, and finally the Texture
     * @param font
     */
    public void setFont(Font font) {
        this.font = font;
        graphics.setFont(font);
        metrics = graphics.getFontMetrics();
        updateTexture();
    }

    /**
     * Get the buffered image of the text
     * @return text image
     */
    public BufferedImage getImage() {
        Vector2f size = sizeOfString();
        BufferedImage image = graphics.getDeviceConfiguration().createCompatibleImage((int) size.getX(),(int) size.getY(), Transparency.TRANSLUCENT);

        Graphics2D imgGraphics = (Graphics2D) image.getGraphics();
        imgGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        imgGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        imgGraphics.setFont(font);
        imgGraphics.drawString(str, 0, font.getSize());

        return image;
    }

    /**
     * Sets the string and updates the texture
     * @param str
     */
    public void setString(String str) {
        this.str = str;
        updateTexture();
    }

    /**
     * Updates the texture of the string, you shouldn't need to call this
     */
    public void updateTexture() {
        tex.update(getImage(), false);
    }

    /**
     * Gets the fully converted texture for drawing
     * @return
     */
    public Texture getTexture() {
        return tex;
    }
    /**
     * Draws the text in immediate mode (for lazy people)
     * @param x
     * @param y
     */
    public void draw(float x, float y, Color color) {
        glColor3f(color.getRed(),color.getGreen(),color.getBlue());
        tex.bind();
        Vector2f size = sizeOfString();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glBegin(GL_QUADS);
        glTexCoord2f(0,0);
        glVertex2f(x,y);
        glTexCoord2f(1,0);
        glVertex2f(x+size.getX(),y);
        glTexCoord2f(1,1);
        glVertex2f(x+size.getX(),y+size.getY());
        glTexCoord2f(0,1);
        glVertex2f(x,y+size.getY());
        glEnd();
        glDisable(GL_BLEND);
    }

    /**
     * Draws the text in immediate mode at the given position (for lazy people)
     * @param position
     */
    public void draw(Vector2f position, Color color) {
        draw(position.getX(), position.getY(), color);
    }

    /**
     * Allows for the font size to be set without setting a whole new font
     * @param size
     */
    public void setFontSize(int size) {
        setFont(new Font(font.getFamily(), font.getStyle(), size));
    }
}
