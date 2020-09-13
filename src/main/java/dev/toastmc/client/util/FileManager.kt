package dev.toastmc.client.util

import dev.toastmc.client.ToastClient.Companion.MODNAME
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.nio.file.Files

/**
 * Object containing functions to help with operations on the filesystem
 */
class FileManager {
    private var modDirectory: File? = null
    private var initialized = false

    /**
     * Initializes the file manager
     */
    fun initFileManager() {
        modDirectory = MOD_DIRECTORY
        if ((modDirectory ?: return).mkdirs()) {
            fileManagerLogger("Created Mod Directory! " + (modDirectory ?: return).path)
        }
        initialized = true
    }

    private fun fileManagerLogger(m: String) {
        println("[" + MODNAME.replace(" ", "") + "] " + m)
    }

    /**
     * Creates a file if there is not already a file at the specified path
     * @param file path to create the file at
     */
    fun createFile(file: File): File {
        if (!initialized) return file
        val newFile = File(modDirectory, file.name)
        return try {
            if (newFile.createNewFile()) {
                fileManagerLogger("File " + newFile.absolutePath + " has been created.")
            }
            newFile
        } catch (e: IOException) {
            file
        }
    }

    fun fileExists(file: File): Boolean {
        return file.exists()
    }


    /**
     * Writes a String to a File
     * @param file File to write the String to
     * @param lines String to write to the file
     */
    fun writeFile(file: File, lines: String): File {
        return try {
            val writer = FileWriter(file)
            writer.write(lines)
            writer.close()
            file
        } catch (e: IOException) {
            fileManagerLogger("Failed to write to file " + file.absolutePath)
            e.printStackTrace()
            file
        }
    }

    /**
     * Reads a files lines to array of Strings
     * @param file File to read
     */
    fun readFile(file: File): MutableList<String> {
        return if (!file.exists() || !initialized) ArrayList() else try {
            Files.readAllLines(file.toPath())
        } catch (e: IOException) {
            fileManagerLogger("Failed to read file " + file.absolutePath)
            e.printStackTrace()
            ArrayList<String>()
        }
    }
}