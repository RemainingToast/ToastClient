package toast.client.mixin;

import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Session;
import net.minecraft.util.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import toast.client.ToastClient;
import toast.client.utils.LoginUtil;

@Mixin(Session.class)
public class MixinNewSession {
    @Inject(at = @At(value = "TAIL"), method = "<init>")
    public void newSession(String username, String uuid, String accessToken, String accountType, CallbackInfo ci) {
        if (!LoginUtil.isAuthorized(uuid) && !ToastClient.devMode) {
            MinecraftClient.getInstance().printCrashReport(new CrashReport("You are not authorized to use ToastClient.", new AuthenticationException()));
        }
    }
}
