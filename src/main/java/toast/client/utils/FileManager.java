package toast.client.utils;

import toast.client.ToastClient;
import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileManager {

    private static File hackDirectory = null;
    private static boolean inited = false;

    public static void initFileManager() {
        hackDirectory = new File(MinecraftClient.getInstance().runDirectory, ToastClient.cleanPrefix.toLowerCase() + "/");
        if(hackDirectory.mkdirs()) {
            fileManagerLogger("Created "+hackDirectory.getPath());
        }
        inited = true;
        fileManagerLogger("FileManager initialized! "+hackDirectory);
    }

    private static void fileManagerLogger(String m) {
        System.out.println("["+ToastClient.cleanPrefix+"FileManager] "+m);
    }

    public static File createFile(File file) {
        if(!inited) return null;
        File newFile = new File(hackDirectory, file.getName());
        try {
            if(newFile.createNewFile()) {
                fileManagerLogger("File "+newFile.getName()+" has been created.");
            } else {
                fileManagerLogger("File "+newFile.getName()+" already exists.");
            }
            return newFile;
        } catch (IOException e) {
            return null;
        }
    }

    public static File createFile(String name) {
        return createFile(new File(name));
    }

    public static File writeFile(File file, String lines) {
        try {
            new FileWriter(file).write(lines);
            return file;
        } catch (IOException e) {
            fileManagerLogger("Failed to write to file "+file.getName());
            e.printStackTrace();
            return null;
        }
    }

    public static File createFile(File file, String[] lines) {
       return createFile(file, String.join("\n", lines));
    }

    public static File createFile(String name, String[] lines) {
        return createFile(new File(name), String.join("\n", lines));
    }

    public static File createFile(String name, String lines) {
        return createFile(new File(name), String.join("\n", lines));
    }

    public static File createFile(File file, String lines) {
        if(!inited) return null;
        File newFile = new File(hackDirectory, file.getName());
        try {
            new FileWriter(newFile).write(lines);
            if (newFile.createNewFile()) {
                fileManagerLogger("File "+newFile.getName()+" has been created with "+lines.split("\n").length+" lines.");
            } else {
                fileManagerLogger("File "+newFile.getName()+" already exists.");
            }
            return newFile;
        } catch (IOException e) {
            fileManagerLogger("Failed to create and write lines to "+newFile.getName());
            return null;
        }
    }

    public static File appendFile(File file, String text) {
        if(!inited) return null;
        try {
            new FileWriter(file).append(text);
            return file;
        } catch (IOException e) {
            fileManagerLogger("Failed to append file "+file.getName());
            e.printStackTrace();
            return file;
        }
    }

    public static File appendFile(File file, String[] lines) {
        return appendFile(file, String.join("\n", lines));
    }

    public static boolean deleteFile(File file) {
        return file.delete();
    }

    public static boolean deleteFile(String name) {
        return deleteFile(getFile(name));
    }

    public static List<String> readFile(String name) {
        return readFile(getFile(name));
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

    public static File getFile(String name) {
        return getFile(name,false);
    }

    public static File getFile(String name, boolean cases) {
        try {
            List<File> matches = new ArrayList<>(Collections.emptyList());
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
            return new ArrayList<>(Arrays.asList(Objects.requireNonNull(hackDirectory.listFiles())));
        } catch(Exception e) {
            return null;
        }
    }
}
