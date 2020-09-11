package dev.toastmc.client.mixin.client;

import com.sun.org.apache.xpath.internal.operations.Bool;
import dev.toastmc.client.gui.auth.AuthScreen;
import dev.toastmc.client.util.auth.LoginUtil;
import dev.toastmc.client.util.auth.Status;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinServerScreen extends Screen {

    @Shadow protected  MultiplayerServerListWidget serverListWidget;
    @Shadow private ServerList serverList;

    private static Status status = Status.UNKNOWN;
    private TexturedButtonWidget authButton;
    private ButtonWidget toastmcButton;

    private final ServerInfo server = new ServerInfo(" TOASTMC.DEV", "toastmc.dev", false);
    private int serverExists = 0;

    protected MixinServerScreen(Text text_1) {
        super(text_1);
    }

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        if (serverExists == 0){
            toastmcButton = new ButtonWidget(30, 6, 100, 20, new LiteralText("PLAY TOASTMC.DEV"), button -> {
                serverList.loadFile();
                serverList.add(server);
                serverListWidget.setServers(serverList);
                serverList.saveFile();
                toastmcButton.visible = false;
            });
            this.addButton(toastmcButton);
            serverExists = 1;
        }

        authButton = new TexturedButtonWidget(6,
                6,
                20,
                20,
                0,
                146,
                20,
                new Identifier("minecraft:textures/gui/widgets.png"),
                256,
                256,
                button -> this.client.openScreen(new AuthScreen(this)),
                new TranslatableText("Authenticate"));
        this.addButton(authButton);

        // Fetch current session status
        MixinServerScreen.status = Status.UNKNOWN;
        LoginUtil.INSTANCE.getStatus().thenAccept(status -> MixinServerScreen.status = status);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo info)
    {
        // Draw status text/icon on button
        assert this.client != null;
        drawCenteredString(matrices, this.client.textRenderer, Formatting.BOLD + status.toString(), authButton.x + authButton.getWidth(), authButton.y - 1, status.color);
    }
}
