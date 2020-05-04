package toast.client.utils;

import toast.client.ToastClient;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileManager {

    private static Path hackDirectory = null;
    private static boolean inited = false;

    public static void initFileManager() {
        hackDirectory = Paths.get(MinecraftClient.getInstance().runDirectory.getPath().replace("run\\.", "run\\").replace("minecraft\\.", "minecraft\\"), ToastClient.cleanPrefix.toLowerCase() + "/");
        if(!hackDirectory.toFile().exists()) {
            hackDirectory.toFile().mkdirs();
        }
        inited = true;
        fileManagerLogger("FileManager initialized! "+hackDirectory);
    }

    private static void fileManagerLogger(String m) {
        System.out.println("["+ToastClient.cleanPrefix+"FileManager] "+m);
    }

    public static boolean createFile(File file) {
        if(!inited) return false;
        File newFile = new File(hackDirectory + "\\" + file.getName());
        try {
            if(newFile.createNewFile()) {
                fileManagerLogger("File "+newFile.getName()+" has been created.");
            } else {
                fileManagerLogger("File "+newFile.getName()+" already exists.");
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean writeFile(File file, String lines) {
        try {
            new FileWriter(file).write(lines);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createFile(File file, String[] lines) {
        if(!inited) return false;
        File newFile = new File(hackDirectory + "\\" + file.getName());
        try {
            new FileWriter(newFile).write(String.join("\n", lines));
            if (newFile.createNewFile()) {
                fileManagerLogger("File "+newFile.getName()+" has been created with "+lines.length+" lines.");
            } else {
                fileManagerLogger("File "+newFile.getName()+" already exists.");
            }
            return true;
        } catch (IOException e) {
            fileManagerLogger("Failed to create and write lines to "+newFile.getName());
            return false;
        }
    }

    public static boolean createFile(File file, String lines) {
        if(!inited) return false;
        File newFile = new File(hackDirectory + "\\" + file.getName());
        try {
            new FileWriter(newFile).write(lines);
            if (newFile.createNewFile()) {
                fileManagerLogger("File "+newFile.getName()+" has been created with "+lines.split("\n").length+" lines.");
            } else {
                fileManagerLogger("File "+newFile.getName()+" already exists.");
            }
            return true;
        } catch (IOException e) {
            fileManagerLogger("Failed to create and write lines to "+newFile.getName());
            return false;
        }
    }

    public static boolean appendFile(File file, String text) {
        if(!inited) return false;
        try {
            new FileWriter(file).append(text);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteFile(File file) {
        return file.delete();
    }

    public static List<String> readFile(File file) {
        if(!file.exists() || !inited) return null;
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static File getFile(String name, boolean cases) {
        try {
            List<File> matches = new ArrayList<>(Arrays.asList());
            Objects.requireNonNull(getFiles()).stream()
                    .filter(file -> cases ? file.getName().equals(name) : file.getName().equalsIgnoreCase(name))
                    .forEach(matches::add);
            return matches.get(0);
        } catch(Exception ignored) { }

        return null;
    }

    public static List<File> getFiles() {
        if(!inited) return null;
        try {
            return new ArrayList<>(Arrays.asList(Objects.requireNonNull(hackDirectory.toFile().listFiles())));
        } catch(Exception e) {
            return null;
        }
    }
}
