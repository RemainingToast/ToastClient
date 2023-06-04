package dev.toastmc.toastclient.api.util.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.GlAllocationUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class GlyphCache
{
    private static final int TEXTURE_WIDTH = 256;

    private static final int TEXTURE_HEIGHT = 256;

    private static final int STRING_WIDTH = 256;

    private static final int STRING_HEIGHT = 64;

    private static final int GLYPH_BORDER = 1;

    private static Color BACK_COLOR = new Color(255, 255, 255, 0);

    private int fontSize = 18;

    private boolean antiAliasEnabled = false;

    private BufferedImage stringImage;

    private Graphics2D stringGraphics;

    public static BufferedImage glyphCacheImage = new BufferedImage(TEXTURE_WIDTH, TEXTURE_HEIGHT, BufferedImage.TYPE_INT_ARGB);

    public static Graphics2D glyphCacheGraphics = glyphCacheImage.createGraphics();

    public static FontRenderContext fontRenderContext = glyphCacheGraphics.getFontRenderContext();

    private int imageData[] = new int[TEXTURE_WIDTH * TEXTURE_HEIGHT];

    private IntBuffer imageBuffer = ByteBuffer.allocateDirect(4 * TEXTURE_WIDTH * TEXTURE_HEIGHT).order(ByteOrder.BIG_ENDIAN).asIntBuffer();

//    private IntBuffer singleIntBuffer = GLAllocation.createDirectIntBuffer(1);
//    private IntBuffer singleIntBuffer = ByteBuffer.allocateDirect(1).asIntBuffer();
    private final IntBuffer singleIntBuffer = GlAllocationUtils.allocateByteBuffer(4).asIntBuffer();

    private final List<Font> allFonts = Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts());

    public static List<Font> usedFonts = Lists.newArrayList();

    private int textureName;

    private LinkedHashMap<Font, Integer> fontCache = Maps.newLinkedHashMap();

    private LinkedHashMap<Long, Entry> glyphCache = Maps.newLinkedHashMap();

    private int cachePosX = GLYPH_BORDER;

    private int cachePosY = GLYPH_BORDER;

    private int cacheLineHeight = 0;

    static class Entry
    {
        int textureName;

        int width;

        int height;

        float u1;

        float v1;

        float u2;

        float v2;
    }

    private String path;

    GlyphCache(String path)
    {
        this.path = path;

        GLFW.glfwMakeContextCurrent(MinecraftClient.getInstance().getWindow().getHandle());
        GL.createCapabilities();

        /* Set background color for use with clearRect() */
        glyphCacheGraphics.setBackground(BACK_COLOR);

        /* The drawImage() to this buffer will copy all source pixels instead of alpha blending them into the current image */
        glyphCacheGraphics.setComposite(AlphaComposite.Src);

        allocateGlyphCacheTexture();
        allocateStringImage(STRING_WIDTH, STRING_HEIGHT);

        /* Use Java's logical font as the default initial font if user does not override it in some configuration file */
        GraphicsEnvironment.getLocalGraphicsEnvironment().preferLocaleFonts();
        usedFonts.add(new Font(Font.SANS_SERIF, Font.PLAIN, 72)); //size 1 > 72
    }

    void setDefaultFont(int size, boolean antiAlias)
    {
        usedFonts.clear();
        try {
            usedFonts.add(Font.createFont(Font.PLAIN, this.getClass().getResourceAsStream(path))); //size 1 > 72
        } catch (FontFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        fontSize = size;
        antiAliasEnabled = antiAlias;
        setRenderingHints();
    }

    GlyphVector layoutGlyphVector(Font font, char text[], int start, int limit, int layoutFlags)
    {
        /* Ensure this font is already in fontCache so it can be referenced by cacheGlyphs() later on */
        if (!fontCache.containsKey(font))
        {
            fontCache.put(font, fontCache.size());
        }
        return font.layoutGlyphVector(fontRenderContext, text, start, limit, layoutFlags);
    }

    Font lookupFont(char text[], int start, int limit, int style)
    {
        /* Try using an already known base font; the first font in usedFonts list is the one set with setDefaultFont() */
        Iterator<Font> iterator = usedFonts.iterator();
        while (iterator.hasNext())
        {
            /* Only use the font if it can layout at least the first character of the requested string range */
            Font font = iterator.next();
            if (font.canDisplayUpTo(text, start, limit) != start)
            {
                /* Return a font instance of the proper point size and style; usedFonts has only 1pt sized plain style fonts */
                return font.deriveFont(style, fontSize);
            }
        }

        /* If still not found, try searching through all fonts installed on the system for the first that can layout this string */
        iterator = allFonts.iterator();
        while (iterator.hasNext())
        {
            /* Only use the font if it can layout at least the first character of the requested string range */
            Font font = iterator.next();
            if (font.canDisplayUpTo(text, start, limit) != start)
            {
                /* If found, add this font to the usedFonts list so it can be looked up faster next time */
                usedFonts.add(font);

                /* Return a font instance of the proper point size and style; allFonts has only 1pt sized plain style fonts */
                return font.deriveFont(style, fontSize);
            }
        }

        /* If no supported fonts found, use the default one (first in usedFonts) so it can draw its unknown character glyphs */
        Font font = usedFonts.get(0);

        /* Return a font instance of the proper point size and style; usedFonts only 1pt sized plain style fonts */
        return font.deriveFont(style, fontSize);
    }

    Entry lookupGlyph(Font font, int glyphCode)
    {
        long fontKey = (long) fontCache.get(font) << 32;
        return glyphCache.get(fontKey | glyphCode);
    }

    void cacheGlyphs(Font font, char text[], int start, int limit, int layoutFlags)
    {
        /* Create new GlyphVector so glyphs can be moved around (kerning workaround; see below) without affecting caller */
        GlyphVector vector = layoutGlyphVector(font, text, start, limit, layoutFlags);

        /* Pixel aligned bounding box for the entire vector; only set if the vector has to be drawn to cache a glyph image */
        Rectangle vectorBounds = null;

        /* This forms the upper 32 bits of the fontCache key to make every font/glyph code point unique */
        long fontKey = (long) fontCache.get(font) << 32;

        int numGlyphs = vector.getNumGlyphs(); /* Length of the GlyphVector */
        Rectangle dirty = null;                /* Total area within texture that needs to be updated with glTexSubImage2D() */
        boolean vectorRendered = false;        /* True if entire GlyphVector was rendered into stringImage */

        for (int index = 0; index < numGlyphs; index++)
        {
            int glyphCode = vector.getGlyphCode(index);
            if (glyphCache.containsKey(fontKey | glyphCode))
            {
                continue;
            }
            if (!vectorRendered)
            {
                vectorRendered = true;

                /*
                 * Kerning can make it impossible to cleanly separate adjacent glyphs. To work around this,
                 * each glyph is manually advanced by 2 pixels to the right of its neighbor before rendering
                 * the entire string. The getGlyphPixelBounds() later on will return the new adjusted bounds
                 * for the glyph.
                 */
                for (int i = 0; i < numGlyphs; i++)
                {
                    Point2D pos = vector.getGlyphPosition(i);
                    pos.setLocation(pos.getX() + 2 * i, pos.getY());
                    vector.setGlyphPosition(i, pos);
                }

                /*
                 * Compute the exact area that the rendered string will take up in the image buffer. Note that
                 * the string will actually be drawn at a positive (x,y) offset from (0,0) to leave enough room
                 * for the ascent above the baseline and to correct for a few glyphs that appear to have negative
                 * horizontal bearing (e.g. U+0423 Cyrillic uppercase letter U on Windows 7).
                 */
                vectorBounds = vector.getPixelBounds(fontRenderContext, 0, 0);

                /* Enlage the stringImage if it is too small to store the entire rendered string */
                if (stringImage == null || vectorBounds.width > stringImage.getWidth() || vectorBounds.height > stringImage.getHeight())
                {
                    int width = Math.max(vectorBounds.width, stringImage.getWidth());
                    int height = Math.max(vectorBounds.height, stringImage.getHeight());
                    allocateStringImage(width, height);
                }

                /* Erase the upper-left corner where the string will get drawn*/
                stringGraphics.clearRect(0, 0, vectorBounds.width, vectorBounds.height);

                /* Draw string with opaque white color and baseline adjustment so the upper-left corner of the image is at (0,0) */
                stringGraphics.drawGlyphVector(vector, -vectorBounds.x, -vectorBounds.y);
            }

            /*
             * Get the glyph's pixel-aligned bounding box. The JavaDoc claims that the "The outline returned
             * by this method is positioned around the origin of each individual glyph." However, the actual
             * bounds are all relative to the start of the entire GlyphVector, which is actually more useful
             * for extracting the glyph's image from the rendered string.
             */
            Rectangle rect = vector.getGlyphPixelBounds(index, null, -vectorBounds.x, -vectorBounds.y);

            /* If the current line in cache image is full, then advance to the next line */
            if (cachePosX + rect.width + GLYPH_BORDER > TEXTURE_WIDTH)
            {
                cachePosX = GLYPH_BORDER;
                cachePosY += cacheLineHeight + GLYPH_BORDER;
                cacheLineHeight = 0;
            }

            /*
             * If the entire image is full, update the current OpenGL texture with everything changed so far in the image
             * (i.e. the dirty rectangle), allocate a new cache texture, and then continue storing glyph images to the
             * upper-left corner of the new texture.
             */
            if (cachePosY + rect.height + GLYPH_BORDER > TEXTURE_HEIGHT)
            {
                updateTexture(dirty);
                dirty = null;

                /* Note that allocateAndSetupTexture() will leave the GL texture already bound */
                allocateGlyphCacheTexture();
                cachePosY = cachePosX = GLYPH_BORDER;
                cacheLineHeight = 0;
            }

            /* The tallest glyph on this line determines the total vertical advance in the texture */
            if (rect.height > cacheLineHeight)
            {
                cacheLineHeight = rect.height;
            }

            /*
             * Blit the individual glyph from it's position in the temporary string buffer to its (cachePosX,
             * cachePosY) position in the texture. NOTE: We don't have to erase the area in the texture image
             * first because the composite method in the Graphics object is always set to AlphaComposite.Src.
             */
            glyphCacheGraphics.drawImage(stringImage,
                                         cachePosX, cachePosY, cachePosX + rect.width, cachePosY + rect.height,
                                         rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, null);

            /*
             * Store this glyph's position in texture and its origin offset. Note that "rect" will not be modified after
             * this point, and getGlyphPixelBounds() always returns a new Rectangle.
             */
            rect.setLocation(cachePosX, cachePosY);

            /*
             * Create new cache entry to record both the texture used by the glyph and its position within that texture.
             * Texture coordinates are normalized to 0.0-1.0 by dividing with TEXTURE_WIDTH and TEXTURE_HEIGHT.
             */
            Entry entry = new Entry();
            entry.textureName = textureName;
            entry.width = rect.width;
            entry.height = rect.height;
            entry.u1 = ((float) rect.x) / TEXTURE_WIDTH;
            entry.v1 = ((float) rect.y) / TEXTURE_HEIGHT;
            entry.u2 = ((float) (rect.x + rect.width)) / TEXTURE_WIDTH;
            entry.v2 = ((float) (rect.y + rect.height)) / TEXTURE_HEIGHT;

            /*
             * The lower 32 bits of the glyphCache key are the glyph codepoint. The upper 64 bits are the font number
             * stored in the fontCache. This creates a unique numerical id for every font/glyph combination.
             */
            glyphCache.put(fontKey | glyphCode, entry);

            /*
             * Track the overall modified region in the texture by performing a union of this glyph's texture position
             * with the update region created so far. Reusing "rect" here makes it easier to extend the dirty rectangle
             * region than using the add(x, y) method to extend by a single point. Also note that creating the first
             * dirty rectangle here avoids having to deal with the special rules for empty/non-existent rectangles.
             */
            if (dirty == null)
            {
                dirty = new Rectangle(cachePosX, cachePosY, rect.width, rect.height);
            } else
            {
                dirty.add(rect);
            }

            /* Advance cachePosX so the next glyph can be stored immediately to the right of this one */
            cachePosX += rect.width + GLYPH_BORDER;
        }

        /* Update OpenGL texture if any part of the glyphCacheImage has changed */
        updateTexture(dirty);
    }

    /**
     * Update a portion of the current glyph cache texture using the contents of the glyphCacheImage with glTexSubImage2D().
     *
     * @param dirty The rectangular region in glyphCacheImage that has changed and needs to be copied into the texture
     *              bleedover when interpolation is active or add a small "fudge factor" to the UV coordinates like already n FontRenderer
     */
    //todo Add mip-mapping support here
    //todo Test with bilinear texture interpolation and possibly add a 1 pixel transparent border around each glyph to avoid
    private void updateTexture(Rectangle dirty)
    {
        /* Only update OpenGL texture if changes were made to the texture */
        if (dirty != null)
        {
            /* Load imageBuffer with pixel data ready for transfer to OpenGL texture */
            updateImageBuffer(dirty.x, dirty.y, dirty.width, dirty.height);

            GlStateManager._bindTexture(textureName);
            GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, dirty.x, dirty.y, dirty.width, dirty.height,
                                 GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);
        }
    }

    /**
     * Allocte and initialize a new BufferedImage and Graphics2D context for rendering strings into. May need to be called
     * at runtime to re-allocate a bigger BufferedImage if cacheGlyphs() is called with a very long string.
     */
    private void allocateStringImage(int width, int height)
    {
        stringImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        stringGraphics = stringImage.createGraphics();
        setRenderingHints();

        /* Set background color for use with clearRect() */
        stringGraphics.setBackground(BACK_COLOR);

        /*
         * Full white (1.0, 1.0, 1.0, 1.0) can be modulated by vertex color to produce a full gamut of text colors, although with
         * a GL_ALPHA8 texture, only the alpha component of the color will actually get loaded into the texture.
         */
        stringGraphics.setPaint(Color.WHITE);
    }

    /**
     * Set rendering hints on stringGraphics object. Uses current antiAliasEnabled settings and is therefore called both from
     * allocateStringImage() when expanding the size of the BufferedImage and from setDefaultFont() when changing current
     * configuration.
     */
    private void setRenderingHints()
    {
        stringGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                        antiAliasEnabled ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        stringGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        antiAliasEnabled ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        stringGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }

    /**
     * Allocate a new OpenGL texture for caching pre-rendered glyph images. The new texture is initialized to fully transparent
     * white so the individual glyphs images within can have a transparent border between them. The new texture remains bound
     * after returning from the function.
     */
    //todo use GL_ALPHA4 if anti-alias is turned off for even smaller textures
    private void allocateGlyphCacheTexture()
    {
        /* Initialize the background to all white but fully transparent. */
        glyphCacheGraphics.clearRect(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        /* Allocate new OpenGL texure */
        singleIntBuffer.clear();
        GL11.glGenTextures(singleIntBuffer);
        textureName = singleIntBuffer.get(0);

        /* Load imageBuffer with pixel data ready for transfer to OpenGL texture */
        updateImageBuffer(0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT);

        /*
         * Initialize texture with the now cleared BufferedImage. Using a texture with GL_ALPHA8 internal format may result in
         * faster rendering since the GPU has to only fetch 1 byte per texel instead of 4 with a regular RGBA texture.
         */
        GlStateManager._bindTexture(textureName);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_ALPHA8, TEXTURE_WIDTH, TEXTURE_HEIGHT, 0,
                          GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, imageBuffer);

        /* Explicitely disable mipmap support becuase updateTexture() will only update the base level 0 */
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
    }

    /**
     * Copy pixel data from a region in glyphCacheImage into imageBuffer and prepare it for use with glText(Sub)Image2D(). This
     * function takes care of converting the ARGB format used with BufferedImage into the RGBA format used by OpenGL.
     *
     * @param x      the horizontal coordinate of the region's upper-left corner
     * @param y      the vertical coordinate of the region's upper-left corner
     * @param width  the width of the pixel region that will be copied into the buffer
     * @param height the height of the pixel region that will be copied into the buffer
     */
    private void updateImageBuffer(int x, int y, int width, int height)
    {
        /* Copy raw pixel data from BufferedImage to imageData array with one integer per pixel in 0xAARRGGBB form */
        glyphCacheImage.getRGB(x, y, width, height, imageData, 0, width);

        /* Swizzle each color integer from Java's ARGB format to OpenGL's RGBA */
        for (int i = 0; i < width * height; i++)
        {
            int color = imageData[i];
            imageData[i] = (color << 8) | (color >>> 24);
        }

        /* Copy int array to direct buffer; big-endian order ensures a 0xRR, 0xGG, 0xBB, 0xAA byte layout */
        imageBuffer.clear();
        imageBuffer.put(imageData);
        imageBuffer.flip();
    }
}
