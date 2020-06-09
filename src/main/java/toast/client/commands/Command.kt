package toast.client.commands

import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.MinecraftClient

/**
 * This class is a superclass for defining commands
 */
@Environment(EnvType.CLIENT)
abstract class Command(
        /**
         * Name of the command
         */
        var name: String,
        /**
         * Syntax of the command
         */
        var usage: String,
        /**
         * Description of the command
         */
        var desc: String,
        /**
         * Whether or not it is a developper only command
         */
        var isDev: Boolean,
        /**
         * Aliases for the command
         */
        @JvmField vararg var aliases: String) {

    /**
     *
     */
    protected var mc: MinecraftClient = MinecraftClient.getInstance()

    /**
     * Method that runs a command while passing it an array of arguments
     */
    @Throws(InterruptedException::class)
    abstract fun run(args: Array<String>)

}