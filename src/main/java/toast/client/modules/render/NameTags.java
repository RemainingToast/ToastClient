package toast.client.modules.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Formatting;
import toast.client.modules.Module;

public class NameTags extends Module {
    private static MinecraftClient mc = MinecraftClient.getInstance();

    public NameTags() {
        super("NameTags", "Displays a nametag above other players", Category.RENDER, -1);
    }

    // TODO: custom nametags (not just health and not mc code)
    public static void renderNameTag(Entity entity, String name, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int int_1) {
        double distance = MinecraftClient.getInstance().getEntityRenderManager().getSquaredDistanceToCamera(entity);
        if (entity instanceof LivingEntity) {
            name = entity.getDisplayName().asFormattedString() + " " + (
                    (((LivingEntity) entity).getHealth() > ((LivingEntity) entity).getMaximumHealth() / 3.0F) ? ((((LivingEntity) entity).getHealth() > ((LivingEntity) entity).getMaximumHealth() / 3.0F * 2.0F) ?
                            Formatting.GREEN : Formatting.YELLOW) : Formatting.DARK_RED) + ((int) (((LivingEntity) entity).getHealth() * 2.0F) / 2.0F) + "h";
        }
        if (distance <= 4096.0D) {
            boolean notSneaking = !entity.isSneaky();
            float height = entity.getHeight() + 0.5F;
            int deadmau5offset = "deadmau5".equals(name) ? -10 : 0;
            matrixStack.push();
            matrixStack.translate(0.0D, height, 0.0D);
            matrixStack.multiply(MinecraftClient.getInstance().getEntityRenderManager().getRotation());
            matrixStack.scale(-0.025F, -0.025F, 0.025F);
            Matrix4f matrix4f = matrixStack.peek().getModel();
            float backgroundOpacity = MinecraftClient.getInstance().options.getTextBackgroundOpacity(0.25F);
            int backgroundcolor = (int) (backgroundOpacity * 255.0F) << 24;
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            float nameWidth = (float) (-textRenderer.getStringWidth(name) / 2);
            textRenderer.draw(name, nameWidth, (float) deadmau5offset, 553648127, false, matrix4f, vertexConsumerProvider, notSneaking, backgroundcolor, int_1);
            textRenderer.draw(name, nameWidth, (float) deadmau5offset, -1, false, matrix4f, vertexConsumerProvider, false, 0, int_1);
            matrixStack.pop();
        }
    }
}
