package toast.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import toast.client.lemongui.clickgui.ClickGui;
import toast.client.lemongui.clickgui.component.Component;
import toast.client.lemongui.clickgui.component.Frame;
import toast.client.lemongui.clickgui.component.components.Button;
import toast.client.lemongui.clickgui.settings.Setting;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static Map<String, Boolean> modules = new HashMap<>();
    public static Map<String, Map<String, Setting>> options = new HashMap<>();
    public static Map<String, String> config = new HashMap<>();
    public static Map<String, toast.client.utils.Config.ClickGuiFrame> clickgui = new HashMap<>();

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void initConfigManager() {
        FileManager.createFile(new File("modules.json"));
        FileManager.createFile(new File("options.json"));
        FileManager.createFile(new File("config.json"));
        FileManager.createFile(new File("clickgui.json"));
    }

    public static void updateRead() {
        try {
            modules = gson.fromJson(new FileReader(FileManager.createFile("modules.json")), new TypeToken<Map<String, Boolean>>(){}.getType());
            options = gson.fromJson(new FileReader(FileManager.createFile("options.json")), new TypeToken<Map<String, Map<String, Setting>>>(){}.getType());
            config = gson.fromJson(new FileReader(FileManager.createFile("config.json")), new TypeToken<Map<String, String>>(){}.getType());
            clickgui = gson.fromJson(new FileReader(FileManager.createFile("clickgui.json")), new TypeToken<Map<String, toast.client.utils.Config.ClickGuiFrame>>(){}.getType());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void writeModules() {
        Map<String, Boolean> modules = new HashMap<>();
        for (Module module : ModuleManager.modules) {
            modules.put(module.getName(), module.isEnabled());
            // System.out.println(module.getName()+":"+module.isEnabled());
        }
        FileManager.writeFile("modules.json", gson.toJson(modules));
    }

    public static void writeOptions() {
        Map<String, Map<String, Setting>> options = new HashMap<>();
        for (Module module : ModuleManager.getModules()) {
            options.put(module.getName(), module.getSettings());
            //System.out.println(module.getName()+" -> "+parseSettings(module));
        }
        FileManager.writeFile("options.json", gson.toJson(options));
    }

    static class ClickGuiFrame {
        @SerializedName("Open")
        private boolean isopen;
        @SerializedName("Pos X")
        private int x;
        @SerializedName("Pos Y")
        private int y;
        @SerializedName("Modules")
        private Map<String, ClickGuiFrameButton>  components;

        public ClickGuiFrame(boolean isopen, int x, int y, Map<String, ClickGuiFrameButton>  components) {
            this.isopen = isopen;
            this.x = x;
            this.y = y;
            this.components = components;
        }

        public boolean isOpen() {
            return isopen;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public Map<String, ClickGuiFrameButton> getComponents() {
            return components;
        }

        static class ClickGuiFrameButton {
            @SerializedName("Open")
            private boolean open;

            public ClickGuiFrameButton(boolean open) {
                this.open = open;
            }

            public boolean isOpen() {
                return open;
            }
        }
    }

    public static void loadClickGui() {
        updateRead();
        if (clickgui == null) {
            clickgui = new HashMap<>();
            writeClickGui();
            return;
        }
        for (Map.Entry<String, toast.client.utils.Config.ClickGuiFrame> frameNew : clickgui.entrySet()) {
            for (Frame frame : ClickGui.getFrames()) {
                if (frame.getCategory().toString().equals(frameNew.getKey())) {
                    frame.setOpen(frameNew.getValue().isOpen());
                    frame.setX(frameNew.getValue().getX());
                    frame.setY(frameNew.getValue().getY());
                    if (frameNew.getValue().getComponents() != null) {
                        for (Component component : frame.getComponents()) {
                            if (component instanceof Button) {
                                Button b = ((Button) component);
                                if (frameNew.getValue().getComponents().containsKey(b.getModName())) {
                                    b.setOpen(frameNew.getValue().getComponents().get(b.getModName()).isOpen());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void writeClickGui() {
        clickgui.clear();
        for (Frame frame : ClickGui.getFrames()) {
            Map<String, ClickGuiFrame.ClickGuiFrameButton>  components = new HashMap<>();
            for (Component component : frame.getComponents()) {
                if (component instanceof Button) {
                    Button b = ((Button) component);
                    components.put(b.getModName(), new ClickGuiFrame.ClickGuiFrameButton(b.isOpen()));
                }
            }
            clickgui.put(frame.getCategory().toString(), new ClickGuiFrame(frame.isOpen(), frame.getX(), frame.getY(), components));
        }
        FileManager.writeFile("clickgui.json", gson.toJson(clickgui));
    }

    public static void writeConfig() {
        config.put("prefix", "."); // hardcoded for now
        FileManager.writeFile("config.json", gson.toJson(config));
    }

    public static void loadOptionsAuto() {
        updateRead();
        if (options == null) {
            writeOptions();
            updateRead();
            return;
        }
        loadOptions(options);
    }

    public static void loadOptions(Map<String, Map<String, Setting>> options) {
        for (Module module : ModuleManager.getModules()) {
            if (module != null && options != null) {
                if (options.containsKey(module.getName())) {
                    module.setSettings(options.get(module.getName()));
                }
            }
        }
    }

    public static void loadModulesAuto() {
        updateRead();
        if (modules == null) {
            writeModules();
            updateRead();
            return;
        }
        loadModules(modules);
    }

    public static void loadModules(Map<String, Boolean> modules) {
        for (Module module : ModuleManager.getModules()) {
            if (module != null && modules != null) {
                if (modules.containsKey(module.getName())) {
                    module.setToggled(modules.get(module.getName()));
                }
            }
        }
    }
}
