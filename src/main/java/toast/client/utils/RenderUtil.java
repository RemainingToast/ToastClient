package toast.client.utils;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EnderCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

/**
 * Courtesy of BleachDrinker420/Bleach, maker of BleachHack
 *
 * @url {https://imgur.com/a/jNGeGBg}
 */

@Environment(EnvType.CLIENT)
public class RenderUtil {

    public static void drawFilledBox(BlockPos blockPos, float r, float g, float b, float a) {
        drawFilledBox(new Box(
                blockPos.getX(), blockPos.getY(), blockPos.getZ(),
                blockPos.getX() + 1, blockPos.getY() + 1, blockPos.getZ() + 1), r, g, b, a);
    }

    public static void drawFilledBox(Box box, float r, float g, float b, float a) {
        gl11Setup();

        // Fill
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(5, VertexFormats.POSITION_COLOR);
        WorldRenderer.drawBox(buffer,
                box.x1, box.y1, box.z1,
                box.x2, box.y2, box.z2, r, g, b, a / 2f);
        tessellator.draw();

        // Outline
        buffer.begin(3, VertexFormats.POSITION_COLOR);
        buffer.vertex(box.x1, box.y1, box.z1).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x1, box.y1, box.z2).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x2, box.y1, box.z2).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x2, box.y1, box.z1).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x1, box.y1, box.z1).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x1, box.y2, box.z1).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x2, box.y2, box.z1).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x2, box.y2, box.z2).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x1, box.y2, box.z2).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x1, box.y2, box.z1).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x1, box.y1, box.z2).color(r, b, b, 0f).next();
        buffer.vertex(box.x1, box.y2, box.z2).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x2, box.y1, box.z2).color(r, b, b, 0f).next();
        buffer.vertex(box.x2, box.y2, box.z2).color(r, b, b, a / 2f).next();
        buffer.vertex(box.x2, box.y1, box.z1).color(r, b, b, 0f).next();
        buffer.vertex(box.x2, box.y2, box.z1).color(r, b, b, a / 2f).next();
        tessellator.draw();

        gl11Cleanup();
    }

    public static void drawLine(double x1, double y1, double z1, double x2, double y2, double z2, float r, float g, float b, float t) {
        gl11Setup();
        GL11.glLineWidth(t);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(3, VertexFormats.POSITION_COLOR);
        buffer.vertex(x1, y1, z1).color(r, g, b, 0.0F).next();
        buffer.vertex(x1, y1, z1).color(r, g, b, 1.0F).next();
        buffer.vertex(x2, y2, z2).color(r, g, b, 1.0F).next();
        tessellator.draw();

        gl11Cleanup();

    }

    public static void drawLineFromEntity(Entity entity, int location, double x, double y, double z, float t) {

        float r = 0;
        float g = 0;
        float b = 0;
        double additionalY;

        if (entity instanceof PlayerEntity) {
            r = 1;
            g = 1;
        } else if (entity instanceof EnderCrystalEntity) {
            r = 1;
            b = 1;
        } else if (EntityUtils.isAnimal(entity)) {
            g = 1;
        } else if (EntityUtils.isNeutral(entity)) {
            r = 1;
            g = 1;
            b = 1;
        } else if (EntityUtils.isHostile(entity)) {
            r = 1;
        } else {
            r = 1;
            b = 1;
            g = 1;
        }

        if (location == 1) {
            additionalY = entity.getHeight() / 2;
        } else if (location == 2) {
            additionalY = entity.getStandingEyeHeight();
        } else {
            additionalY = 0;
        }

        gl11Setup();
        GL11.glLineWidth(t);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(3, VertexFormats.POSITION_COLOR);
        buffer.vertex(entity.getX(), entity.getY() + additionalY, entity.getZ()).color(r, g, b, 0.0F).next();
        buffer.vertex(entity.getX(), entity.getY() + additionalY, entity.getZ()).color(r, g, b, 1.0F).next();
        buffer.vertex(x, y, z).color(r, g, b, 1.0F).next();
        tessellator.draw();

        gl11Cleanup();

    }

    public static void drawQuad(double x1, double z1, double x2, double z2, double y, float r, float g, float b, float a) {
        gl11Setup();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_QUADS, VertexFormats.POSITION_COLOR);
        buffer.vertex(x1, y, z1).color(r, g, b, a).next();
        buffer.vertex(x2, y, z1).color(r, g, b, a).next();
        buffer.vertex(x2, y, z2).color(r, g, b, a).next();
        buffer.vertex(x1, y, z2).color(r, g, b, a).next();
        buffer.vertex(x1, y, z1).color(r, g, b, a).next();
        tessellator.draw();

        gl11Cleanup();
    }

    public static void offsetRender() {
        Camera camera = BlockEntityRenderDispatcher.INSTANCE.camera;
        Vec3d camPos = camera.getPos();
        GL11.glRotated(MathHelper.wrapDegrees(camera.getPitch()), 1, 0, 0);
        GL11.glRotated(MathHelper.wrapDegrees(camera.getYaw() + 180.0), 0, 1, 0);
        GL11.glTranslated(-camPos.x, -camPos.y, -camPos.z);
    }

    public static void gl11Setup() {
        GL11.glEnable(GL11.GL_BLEND);
        GL14.glBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);
        GL11.glLineWidth(2.5F);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glMatrixMode(5889);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glPushMatrix();
        offsetRender();
    }

    public static void gl11Cleanup() {
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
