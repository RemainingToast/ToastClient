package toast.client.utils;

import toast.client.lemongui.settings.Setting;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class Config {
    public static boolean write() throws IOException {
        FileManager.createFile(new File("modules.txt"));
        File file = FileManager.getFile("modules", false);
        if (file == null) return false;
        List<String> lines = Files.readAllLines(file.toPath());
        FileWriter writer = new FileWriter(file);
        writer.write("");
        for (Module module : ModuleManager.modules) {
            writer.append("Name : " + module.getName() + "\n");
            writer.append("Enabled : " + module.isEnabled() + "\n");
            for (Setting setting : ModuleManager.setmgr.getSettingsByMod(module)) {
                try {
                    writer.append(setting.getName() + " : " + setting.getValString() + "\n");
                } catch (IOException exceptionA) {
                    try {
                        writer.append(setting.getName() + " : " + setting.getValBoolean() + "\n");
                    } catch (IOException exceptionB) {
                        try {
                            writer.append(setting.getName() + " : " + setting.getValDouble() + "\n");
                        } catch (IOException ignored) { }
                    }
                }
            }
        }
        return false;
    }
}
