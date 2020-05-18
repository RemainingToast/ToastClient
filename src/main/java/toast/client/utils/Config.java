package toast.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import toast.client.lemongui.clickgui.settings.Setting;
import toast.client.modules.Module;
import toast.client.modules.ModuleManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

public class Config {
    public static Map<String, Boolean> modules;
    public static Map<String, Map<String, Setting>> options;
    public static Map<String, String> config;
    public static Map<String, String> clickgui; //help pls aaaaaaaa

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
            clickgui = null;//hlep
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

    public static void parseClickguiSettingsAndSet() {
        /*for (String line : getClickguiLines()) {
            if(line.equals("")) return;
            String[] split = line.split("\\|");
            for (String frameString : split) {
                if(frameString.contains("[FRAME]")) {
                    String[] frameSplit = frameString.split(":");
                    String name = frameSplit[0].split("\\[FRAME]")[1];
                    int x = Integer.parseInt(frameSplit[1].split("\\[X]")[1]);
                    int y = Integer.parseInt(frameSplit[2].split("\\[Y]")[1]);
                    boolean open = Boolean.parseBoolean(frameSplit[3].split("\\[OPEN]")[1]);
                    for (Frame frame : ClickGui.getFrames()) {
                        if (frame.category.name().equalsIgnoreCase(name)) {
                            frame.setX(x);
                            frame.setY(y);
                            frame.setOpen(open);
                        }
                    }
                } else if(frameString.contains("[COMPONENT]")) {
                    String[] componentSplit = frameString.split(":");
                    String compName = componentSplit[0].split("\\[COMPONENT]")[1];
                    String parentFrameName = componentSplit[1].split("\\[FRAMEPARENT]")[1];
                    boolean open = Boolean.parseBoolean(componentSplit[2].split("\\[OPEN]")[1]);
                    for (Frame frame : ClickGui.getFrames()) {
                        for (Component component : frame.components) {
                            if (component instanceof Button) {
                                Button b = ((Button) component);
                                if (b.getParent().category.name().equalsIgnoreCase(parentFrameName)) {
                                    if (b.mod.getName().equalsIgnoreCase(compName)) {
                                        b.setOpen(open);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }*/
    }

    public static void writeClickgui() {
        FileManager.writeFile("clickgui.json", gson.toJson(clickgui));
    }

    public static void writeConfig() {
        config.put("prefix", ".");
        FileManager.writeFile("config.json", gson.toJson(config));// hardcoded for now
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
