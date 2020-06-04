package toast.client.commands;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public abstract class Command {
    public String[] aliases;
    public String name;
    public String usage;
    public String desc;
    public boolean dev;
    protected MinecraftClient mc = MinecraftClient.getInstance();

    public Command(String name,String usage, String desc, boolean dev, String... aliases) {
        this.usage = usage;
        this.name = name;
        this.dev = dev;
        this.aliases = aliases;
        this.desc = desc;
    }

    public String[] getAliases() {
        return aliases;
    }

    public String getName() {
        return name;
    }

    public String getUsage() {
        return usage;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isDev() {
        return dev;
    }

    public abstract void run(String[] args) throws InterruptedException;
}
