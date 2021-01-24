package me.remainingtoast.toastclient.api.util.font;

import me.remainingtoast.toastclient.api.util.Vector2f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * A basic texture class, converts a BufferedImage into a Texture.
 * Also loads textures from a file.
 * Stores texture ID and allows for updating of texture
 * @author Charlie "Rock" Quigley
 * @see BufferedImage
 */

public class Texture {

    /**
     * Generate a magenta and black texture to avoid crashes when a file isn't found. Can also be used elsewhere or for debugging.
     * @return Not found texture
     */
    public static BufferedImage genNotFoundBufIMG() {
        BufferedImage nf = new BufferedImage(64, 64, BufferedImage.TYPE_4BYTE_ABGR);

        Graphics2D graphics = (Graphics2D) nf.getGraphics();

        graphics.setBackground(Color.DARK_GRAY);
        graphics.clearRect(0, 0, 64, 64);

        graphics.setColor(Color.MAGENTA);
        graphics.fillRect(0, 0, 32, 32);
        graphics.fillRect(32, 32, 32, 32);

        BufferedImage nf2 = new BufferedImage(128, 128, BufferedImage.TYPE_4BYTE_ABGR);

        graphics = (Graphics2D) nf2.getGraphics();

        graphics.drawImage(nf, 0, 0, null);
        graphics.drawImage(nf, 64, 0, null);
        graphics.drawImage(nf, 64, 64, null);
        graphics.drawImage(nf, 0, 64, null);

        nf.flush();

        return nf2;
    }

    /** OpenGL Texture ID */
    private int id;

    /** The current texture image */
    private BufferedImage buf;

    private boolean isFiltered;

    /**
     * Create a new texture using a bufferedImage
     * @param bufferedImage
     */
    public Texture(BufferedImage bufferedImage, boolean filter) {
        id = glGenTextures();
        update(bufferedImage, filter);
    }

    /**
     * Update the contents of the texture
     * @param bufferedImage the image to update the texture with
     */
    public void update(BufferedImage bufferedImage, boolean filter) {
        buf = bufferedImage;
        int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), pixels, 0, bufferedImage.getWidth());

        ByteBuffer data = ByteBuffer.allocateDirect((bufferedImage.getWidth() * bufferedImage.getHeight() * 4));

        for (int y = 0; y < bufferedImage.getHeight(); y++)
        {
            for (int x = 0; x < bufferedImage.getWidth(); x++)
            {
                int pixel = pixels[y * bufferedImage.getWidth() + x];
                data.put((byte) ((pixel >> 16) & 0xFF));
                data.put((byte) ((pixel >> 8) & 0xFF));
                data.put((byte) (pixel & 0xFF));
                data.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        data.flip();
        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        if(filter) {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        } else {
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        }

        isFiltered = filter;

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, bufferedImage.getWidth(), bufferedImage.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void setFiltered(boolean filtered) {
        if(filtered != isFiltered) {
            glBindTexture(GL_TEXTURE_2D, id);
            if(filtered) {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            } else {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            }
            glBindTexture(GL_TEXTURE_2D, 0);
            isFiltered = filtered;
        }
    }

    public void enableFiltering() {
        setFiltered(true);
    }

    public void disableFiltering() {
        setFiltered(false);
    }

    /**
     * Creates a texture given file inside jar
     * @param name
     */
    public Texture(String name, boolean filter) {
        id = glGenTextures();
        update(name, filter);
    }

    /**
     * Creates a texture using given file
     * @param file
     */
    public Texture(File file, boolean filter) {
        id = glGenTextures();
        update(file, filter);
    }

    /**
     * Get the image for manipulation
     * @return image
     */
    public BufferedImage getImage() {
        return buf;
    }

    /**
     * Update the texture from a file inside the jar
     * @param name of file
     */
    public void update(String name, boolean filter) {
        try {
            BufferedImage in = ImageIO.read(Texture.class.getClassLoader().getResourceAsStream(name));
            buf = in;
            update(in, filter);
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("File " + name + " not found, or an error occurred");
            update(genNotFoundBufIMG(), false);
        }
    }

    /**
     * Update the texture from a file not inside the jar
     * @param file image file
     */
    public void update(File file, boolean filter) {
        if(file.exists()) {
            try {
                BufferedImage in = ImageIO.read(file);
                buf = in;
                update(in, filter);
            } catch (IOException | IllegalArgumentException e) {
                System.out.println("An error occurred while getting file " + file.getName() + ".");
                update(genNotFoundBufIMG(), filter);
            }
        } else {
            update(genNotFoundBufIMG(), false);
            System.out.println("File " + file.getName() + " not found.");
        }
    }

    /**
     * Get the opengl ID of this texture
     * @return id
     */
    public int getID() {
        return id;
    }

    /**
     * Bind the texture
     * This is a shorthand method for {@code glBindTexture(GL_TEXTURE_2D, instance.getID());}
     * @see #getID()
     * @see {@code glBindTexture(int target, int texture)}
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /**
     * Unbind the texture
     * This is a convinience method for {@code glBindTexture(GL_TEXTURE_2D, 0);}
//     * @see glBindTexture(int target, int texture)
     */
    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    /**
     * Gets the width of this {@code Texture}
     * @return width
     */
    public float getWidth() {
        return buf.getWidth();
    }

    /**
     * Gets the height of this {@code Texture}
     * @return height
     */
    public float getHeight() {
        return buf.getHeight();
    }

    /**
     * Gets the width and height as a Vector2f
     * @return size
     */
    public Vector2f getSize() {
        return new Vector2f(getWidth(), getHeight());
    }

    @Override
    public void finalize() {
        release();
        System.err.println("TEXTURE WITH ID " + id + " NOT RELEASED!!!!!");
    }

    /**
     * call this when done with texture
     */
    public void release() {
        GL11.glDeleteTextures(id);
    }
}
