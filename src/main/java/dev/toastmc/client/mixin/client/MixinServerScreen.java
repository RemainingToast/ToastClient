package dev.toastmc.client.mixin.client;

import com.sun.org.apache.xpath.internal.operations.Bool;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.options.ServerList;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiplayerScreen.class)
public abstract class MixinServerScreen extends Screen {

    @Shadow protected MultiplayerServerListWidget serverListWidget;
    @Shadow private ServerList serverList;

    private final ServerInfo server = new ServerInfo(" TOASTMC.DEV", "toastmc.dev", false);
    private Boolean serverExists = false;

    protected MixinServerScreen(Text text_1) {
        super(text_1);
    }

    @Inject(at = @At("HEAD"), method = "init()V")
    private void init(CallbackInfo info) {
        addButton(new ButtonWidget(7, 7, 100, 20, new LiteralText("PLAY TOASTMC.DEV"), button -> {
            if(!serverExists){
                serverList.loadFile();
                serverList.add(server);
                serverListWidget.setServers(serverList);
                serverList.saveFile();
                serverExists = true;
            }
        }));
    }
}
