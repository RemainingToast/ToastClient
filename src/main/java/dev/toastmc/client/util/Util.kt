package dev.toastmc.client.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mojang.authlib.Agent
import com.mojang.authlib.exceptions.AuthenticationException
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication
import com.mojang.util.UUIDTypeAdapter
import dev.toastmc.client.ToastClient
import dev.toastmc.client.event.ChunkEvent
import dev.toastmc.client.mixin.client.IClientPlayerInteractionManager
import io.netty.util.internal.ConcurrentSet
import me.zero.alpine.listener.EventHandler
import me.zero.alpine.listener.EventHook
import me.zero.alpine.listener.Listener
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.Session
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.Enchantments
import net.minecraft.entity.DamageUtil
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityGroup
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.Saddleable
import net.minecraft.entity.attribute.EntityAttributes
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.*
import net.minecraft.entity.passive.AnimalEntity
import net.minecraft.entity.passive.GolemEntity
import net.minecraft.entity.passive.VillagerEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.vehicle.BoatEntity
import net.minecraft.entity.vehicle.MinecartEntity
import net.minecraft.item.*
import net.minecraft.nbt.ListTag
import net.minecraft.network.MessageType
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket
import net.minecraft.text.LiteralText
import net.minecraft.util.Formatting
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.EntityHitResult
import net.minecraft.util.math.*
import net.minecraft.world.Difficulty
import net.minecraft.world.Heightmap
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.explosion.Explosion
import org.apache.commons.lang3.ClassUtils
import org.apache.commons.lang3.reflect.MethodUtils
import java.io.File
import java.lang.reflect.Field
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.function.Consumer
import java.util.stream.Collectors
import kotlin.math.*

val gson: Gson = GsonBuilder().setPrettyPrinting().create()
val mc: MinecraftClient = MinecraftClient.getInstance()

val MOD_DIRECTORY: File = File(mc.runDirectory, "toastclient" + "/")

fun lit(string: String): LiteralText {
    return LiteralText(string)
}

val CHAT_PREFIX = Formatting.DARK_GRAY.toString() + "[" + Formatting.RED + Formatting.BOLD + "Toast" + Formatting.DARK_GRAY + "] "
fun sendRawMessage(message: String?) {
    assert(mc.player != null)
    mc.inGameHud.addChatMessage(
        MessageType.CHAT,
        LiteralText(message),
        mc.player!!.uuid
    )
}

fun sendPublicMessage(message: String?) {
    if (mc.player == null) return
    mc.player!!.sendChatMessage(message)
}

fun defaultErrorMessage() {
    sendRawMessage(CHAT_PREFIX + Formatting.RED + "Computer Says No.")
}

fun sendMessage(message: String, color: Color?) {
    when (color) {
        Color.DARK_RED -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_RED + message)
        Color.RED -> sendRawMessage(CHAT_PREFIX + Formatting.RED + message)
        Color.GOLD -> sendRawMessage(CHAT_PREFIX + Formatting.GOLD + message)
        Color.YELLOW -> sendRawMessage(CHAT_PREFIX + Formatting.YELLOW + message)
        Color.DARK_GREEN -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_GREEN + message)
        Color.GREEN -> sendRawMessage(CHAT_PREFIX + Formatting.GREEN + message)
        Color.AQUA -> sendRawMessage(CHAT_PREFIX + Formatting.AQUA + message)
        Color.DARK_AQUA -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_AQUA + message)
        Color.DARK_BLUE -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_BLUE + message)
        Color.BLUE -> sendRawMessage(CHAT_PREFIX + Formatting.BLUE + message)
        Color.LIGHT_PURPLE -> sendRawMessage(CHAT_PREFIX + Formatting.LIGHT_PURPLE + message)
        Color.DARK_PURPLE -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_PURPLE + message)
        Color.WHITE -> sendRawMessage(CHAT_PREFIX + Formatting.WHITE + message)
        Color.DARK_GRAY -> sendRawMessage(CHAT_PREFIX + Formatting.DARK_GRAY + message)
        Color.BLACK -> sendRawMessage(CHAT_PREFIX + Formatting.BLACK + message)
        else -> sendRawMessage(CHAT_PREFIX + Formatting.GRAY + message)
    }
}

//A - Z Please
enum class Color {
    DARK_RED, RED, GOLD, YELLOW, DARK_GREEN, GREEN, AQUA, DARK_AQUA, DARK_BLUE, BLUE, LIGHT_PURPLE, DARK_PURPLE, WHITE, GRAY, DARK_GRAY, BLACK
}

fun parsePossible(string: String) : Any {
    return try {
        val doubleStr = string.toDouble()
        val intStr = doubleStr.toInt()
        if (doubleStr.compareTo(intStr) == 0) intStr else doubleStr
    } catch (e: NumberFormatException) {
        if (string.equals("true", true)) true else if (string.equals("false", true)) false else string
    }
}

fun getPossibleType(string: String): Class<*>? {
    return parsePossible(string).javaClass
}

class TimerUtil {
    private var lastMS: Long = 0

    fun everyDelay(delay: Long): Boolean {
        if (System.currentTimeMillis() - lastMS >= delay) {
            lastMS = System.currentTimeMillis().coerceAtMost(lastMS + delay)
            return true
        }
        return false
    }

    fun hasReached(f: Float): Boolean {
        return (System.currentTimeMillis() - lastMS).toFloat() >= f
    }

    /**
     * Checks if and amount of time has passed since the last reset
     */
    fun isDelayComplete(delay: Double): Boolean {
        return System.currentTimeMillis() - lastMS >= delay
    }

    /**
     * Returns the amount of time in milliseconds that has passed since last resetting the timer
     */
    fun pastTime(): Int {
        return (System.currentTimeMillis() - lastMS).toInt()
    }

    /**
     * Sets the last recorded millisecond to the current time in milliseconds
     */
    fun reset() {
        lastMS = System.currentTimeMillis()
    }

    init {
        reset()
    }
}

fun interpolateEntity(entity: Entity, time: Float): Vec3d {
    return Vec3d(
        entity.prevX + (entity.prevX - entity.prevX) * time,
        entity.prevY + (entity.prevY - entity.prevY) * time,
        entity.prevZ + (entity.prevZ - entity.prevZ) * time
    )
}


object WorldUtil {
    var loadedChunks: ConcurrentSet<Chunk> = ConcurrentSet()

    /**
     * Get all tile entities inside a given Chunk and collect them into a map of BlockPos and Block
     *
     * @param world  World of the chunk
     * @param chunkX Chunk-level X value (normal X / 16.0D)
     * @param chunkZ Chunk-level Z value (normal Z / 16.0D)
     * @return Tile entities inside the Chunk collected as a map of BlockPos and Block
     * @see Chunk.getBlockEntityPositions
     */
    fun getTileEntitiesInChunk(
        world: World,
        chunkX: Int,
        chunkZ: Int
    ): LinkedHashMap<BlockPos, Block> {
        val map =
                LinkedHashMap<BlockPos, Block>()
        if (!world.isChunkLoaded(BlockPos((chunkX shr 4).toDouble(), 80.0, (chunkX shr 4).toDouble()))) {
            return map
        }
        val chunk: Chunk = world.getChunk(chunkX, chunkZ)
        chunk.blockEntityPositions.forEach(Consumer { tilePos: BlockPos ->
            map[tilePos] = world.getBlockState(tilePos).block
        })
        return map
    }

    /**
     * Get all tile entities inside a given world and collect them into a map of BlockPos and Block
     *
     * @param world World of the chunk
     * @return Tile entities inside the world which are loaded by the player collected as a map of BlockPos and Block
     * @see Chunk.getBlockEntityPositions
     */
    fun World.getTileEntitiesInWorld(): LinkedHashMap<BlockPos, Block> {
        val map =
                LinkedHashMap<BlockPos, Block>()
        this.blockEntities.forEach(Consumer { tilePos: BlockEntity ->
            val pos = tilePos.pos
            map[pos] = this.getBlockState(pos).block
        })
        return map
    }

    /**
     * Gets distance between two vectors
     *
     * @param vecA First Vector
     * @param vecB Second Vector
     * @return the distance between two vectors
     */
    fun getDistance(vecA: Vec3d, vecB: Vec3d): Double {
        return sqrt(
            (vecA.x - vecB.x).pow(2.0) + (vecA.y - vecB.y).pow(2.0) + (vecA.z - vecB.z).pow(2.0)
        )
    }

    /**
     * Gets vectors between two given vectors (startVec and destinationVec) every (distance between the given vectors) / steps
     *
     * @param startVec       Beginning vector
     * @param destinationVec Ending vector
     * @param steps          distance between given vectors
     * @return all vectors between startVec and destinationVec divided by steps
     */
    fun Vec3d.extendVec(
        destinationVec: Vec3d,
        steps: Int
    ): ArrayList<Vec3d> {
        val returnList =
                ArrayList<Vec3d>(steps + 1)
        val stepDistance = getDistance(this, destinationVec) / steps
        for (i in 0 until steps.coerceAtLeast(1) + 1) {
            returnList.add(this.advanceVec(destinationVec, stepDistance * i))
        }
        return returnList
    }

    /**
     * Moves a vector towards a destination based on distance
     *
     * @param startVec       Starting vector
     * @param destinationVec returned vector
     * @param distance       distance to move startVec by
     * @return vector based on startVec that is moved towards destinationVec by distance
     */
    fun Vec3d.advanceVec(
        destinationVec: Vec3d,
        distance: Double
    ): Vec3d {
        val advanceDirection = destinationVec.subtract(this).normalize()
        return if (destinationVec.distanceTo(this) < distance) destinationVec else advanceDirection.multiply(
            distance
        )
    }

    /**
     * Get all rounded block positions inside a 3-dimensional area between pos1 and pos2.
     *
     * @param end Ending vector
     * @return rounded block positions inside a 3d area between pos1 and pos2
     */
    fun Vec3d.getBlockPositionsInArea(
        end: Vec3d
    ): List<BlockPos> {
        val minX: Int = this.x.coerceAtMost(end.x).roundToInt()
        val maxX: Int = this.x.coerceAtLeast(end.x).roundToInt()
        val minY: Int = this.y.coerceAtMost(end.y).roundToInt()
        val maxY: Int = this.y.coerceAtLeast(end.y).roundToInt()
        val minZ: Int = this.z.coerceAtMost(end.z).roundToInt()
        val maxZ: Int = this.z.coerceAtLeast(end.z).roundToInt()
        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).collect(Collectors.toList())
    }

    /**
     * Get all block positions inside a 3d area between pos1 and pos2
     *
     * @param end Ending blockPos
     * @return block positions inside a 3d area between pos1 and pos2
     */
    fun BlockPos.getBlockPositionsInArea(end: BlockPos): List<BlockPos> {
        val minX: Int = this.x.coerceAtMost(end.x)
        val maxX: Int = this.x.coerceAtLeast(end.x)
        val minY: Int = this.y.coerceAtMost(end.y)
        val maxY: Int = this.y.coerceAtLeast(end.y)
        val minZ: Int = this.z.coerceAtMost(end.z)
        val maxZ: Int = this.z.coerceAtLeast(end.z)
        return BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).collect(Collectors.toList())
    }

    /**
     * Get all positions of block matches below the surface of an area
     *
     * @param startX
     * @param startZ
     * @param endX
     * @param endZ
     * @return block positions inside a 3d area between pos1 and pos2
     */
    fun getBlockMatchesBelowSurface(match: Block, startX: Int, startZ: Int, endX: Int, endZ: Int): List<BlockPos> {
        val returnList = mutableListOf<BlockPos>()
        for (x in startX..endX) {
            for (z in startZ..endZ) {
                for (y in 0..getHighestYAtXZ(x, z)) {
                    if (mc.world!!.getBlockState(BlockPos(x, y, z)).block == match) returnList.add(BlockPos(x, y, z))
                }
            }
        }
        return returnList
    }

    /**
     * Gets the Y coordinate of the highest non-air block in the column of x, z
     *
     * @param x X coordinate of the column
     * @param z Z coordinate of the column
     * @return Y coordinate of the highest non-air block in the column
     */
    fun getHighestYAtXZ(x: Int, z: Int): Int {
        return mc.world!!.getChunk(BlockPos(x, 0, z)).sampleHeightmap(Heightmap.Type.WORLD_SURFACE, x, z)
    }

    val BlockPos.block: Block
        get() = mc.world!!.getBlockState(this).block

    val BlockPos.vec3d: Vec3d
        get() = Vec3d(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())

    fun Block.matches(vararg blocks: Block): Boolean {
        return blocks.contains(this)
    }

    @EventHandler
    val onChunkEvent = Listener(EventHook<ChunkEvent> {
        if (mc.world!!.isChunkLoaded(it.chunk!!.pos.startX, it.chunk.pos.startZ))
            loadedChunks.add(it.chunk)
        else
            loadedChunks.remove(it.chunk)
    })

    init {
        ToastClient.EVENT_BUS.subscribe(onChunkEvent)
    }

    val AIR = listOf(Blocks.AIR, Blocks.CAVE_AIR)
    val REPLACEABLE = listOf(
        Blocks.AIR, Blocks.CAVE_AIR, Blocks.LAVA, Blocks.WATER,
        Blocks.GRASS, Blocks.TALL_GRASS, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.FERN, Blocks.DEAD_BUSH,
        Blocks.VINE, Blocks.FIRE, Blocks.STRUCTURE_VOID
    )

    //what i mean with special items are like if you rightclick a cauldron with a waterbottle it fills it
    private val RIGHTCLICKABLE_NOSPECIALITEM: MutableList<Block> = listOf(
        Blocks.DISPENSER,
        Blocks.NOTE_BLOCK,
        Blocks.WHITE_BED,
        Blocks.ORANGE_BED,
        Blocks.MAGENTA_BED,
        Blocks.LIGHT_BLUE_BED,
        Blocks.YELLOW_BED,
        Blocks.LIME_BED,
        Blocks.PINK_BED,
        Blocks.GRAY_BED,
        Blocks.LIGHT_GRAY_BED,
        Blocks.CYAN_BED,
        Blocks.PURPLE_BED,
        Blocks.BLUE_BED,
        Blocks.BROWN_BED,
        Blocks.GREEN_BED,
        Blocks.RED_BED,
        Blocks.BLACK_BED,
        Blocks.CHEST,
        Blocks.FURNACE,
        Blocks.OAK_DOOR,
        Blocks.LEVER,
        Blocks.STONE_BUTTON,
        Blocks.CAKE,
        Blocks.REPEATER,
        Blocks.OAK_TRAPDOOR,
        Blocks.SPRUCE_TRAPDOOR,
        Blocks.BIRCH_TRAPDOOR,
        Blocks.JUNGLE_TRAPDOOR,
        Blocks.ACACIA_TRAPDOOR,
        Blocks.DARK_OAK_TRAPDOOR,
        Blocks.OAK_FENCE_GATE,
        Blocks.BREWING_STAND,
        Blocks.ENDER_CHEST,
        Blocks.COMMAND_BLOCK,
        Blocks.OAK_BUTTON,
        Blocks.SPRUCE_BUTTON,
        Blocks.BIRCH_BUTTON,
        Blocks.JUNGLE_BUTTON,
        Blocks.ACACIA_BUTTON,
        Blocks.DARK_OAK_BUTTON,
        Blocks.ANVIL,
        Blocks.CHIPPED_ANVIL,
        Blocks.DAMAGED_ANVIL,
        Blocks.TRAPPED_CHEST,
        Blocks.COMPARATOR,
        Blocks.DAYLIGHT_DETECTOR,
        Blocks.HOPPER,
        Blocks.DROPPER,
        Blocks.SPRUCE_FENCE_GATE,
        Blocks.BIRCH_FENCE_GATE,
        Blocks.JUNGLE_FENCE_GATE,
        Blocks.ACACIA_FENCE_GATE,
        Blocks.DARK_OAK_FENCE_GATE,
        Blocks.SPRUCE_DOOR,
        Blocks.BIRCH_DOOR,
        Blocks.JUNGLE_DOOR,
        Blocks.ACACIA_DOOR,
        Blocks.DARK_OAK_DOOR,
        Blocks.REPEATING_COMMAND_BLOCK,
        Blocks.CHAIN_COMMAND_BLOCK,
        Blocks.SHULKER_BOX,
        Blocks.WHITE_SHULKER_BOX,
        Blocks.ORANGE_SHULKER_BOX,
        Blocks.MAGENTA_SHULKER_BOX,
        Blocks.LIGHT_BLUE_SHULKER_BOX,
        Blocks.YELLOW_SHULKER_BOX,
        Blocks.LIME_SHULKER_BOX,
        Blocks.PINK_SHULKER_BOX,
        Blocks.GRAY_SHULKER_BOX,
        Blocks.LIGHT_GRAY_SHULKER_BOX,
        Blocks.CYAN_SHULKER_BOX,
        Blocks.PURPLE_SHULKER_BOX,
        Blocks.BLUE_SHULKER_BOX,
        Blocks.BROWN_SHULKER_BOX,
        Blocks.GREEN_SHULKER_BOX,
        Blocks.RED_SHULKER_BOX,
        Blocks.BLACK_SHULKER_BOX,
        Blocks.LOOM,
        Blocks.BARREL,
        Blocks.SMOKER,
        Blocks.BLAST_FURNACE,
        Blocks.GRINDSTONE,
        Blocks.LECTERN,
        Blocks.STONECUTTER,
        Blocks.BELL,
        Blocks.SWEET_BERRY_BUSH,
        Blocks.STRUCTURE_BLOCK,
        Blocks.JIGSAW
    ) as MutableList<Block>

    fun isReplaceable(b: Block): Boolean {
        return REPLACEABLE.contains(b)
    }

    fun isRightClickable(b: Block): Boolean {
        return RIGHTCLICKABLE_NOSPECIALITEM.contains(b)
    }

    fun isFluid(pos: BlockPos): Boolean {
        val fluids: List<Material> = Arrays.asList(Material.WATER, Material.LAVA, Material.UNDERWATER_PLANT)
        return fluids.contains(MinecraftClient.getInstance().world!!.getBlockState(pos).material)
    }

    fun isRightClickable(b: BlockState): Boolean {
        return isRightClickable(b.block)
    }

    fun isReplaceable(b: BlockState): Boolean {
        return isReplaceable(b.block)
    }

    fun placeBlock(pos: BlockPos, hand: Hand?, doRotations: Boolean): Boolean {
        val mc = MinecraftClient.getInstance()
        if (mc.world == null || mc.player == null) return false
        val player = mc.player
        for (direction in Direction.values()) {
            val offsetPos = pos.offset(direction)
            val offsetBlock = mc.world!!.getBlockState(offsetPos).block
            if (!isReplaceable(offsetBlock)) {
                if (doRotations) {
                    val dx = offsetPos.x + 0.5 - player!!.x
                    val dy = offsetPos.y + 0.5 - (player.y + player.standingEyeHeight)
                    val dz = offsetPos.z + 0.5 - player.z
                    val dh = sqrt(dx * dx + dz * dz)
                    val yaw = Math.toDegrees(atan2(dz, dx)).toFloat() - 90
                    val pitch = (-Math.toDegrees(atan2(dy, dh))).toFloat()
                    player.networkHandler.sendPacket(PlayerMoveC2SPacket.LookOnly(yaw, pitch, player.isOnGround))
                }
                if (isRightClickable(offsetBlock)) {
                    player!!.networkHandler.sendPacket(
                        ClientCommandC2SPacket(
                            player,
                            ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY
                        )
                    )
                }
                if (player!!.getStackInHand(hand).item != null && player.getStackInHand(hand)
                                .item !== Items.AIR
                ) {
                    mc.interactionManager!!.interactBlock(
                        player, mc.world, hand,
                        BlockHitResult(
                            Vec3d(pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble()),
                            direction.opposite,
                            offsetPos,
                            false
                        )
                    )
                    player.swingHand(hand)
                } else {
                    return false
                }
                if (isRightClickable(offsetBlock)) {
                    player.networkHandler.sendPacket(
                        ClientCommandC2SPacket(
                            mc.player,
                            ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY
                        )
                    )
                }
                return true
            }
        }
        return false
    }
}

enum class Status(val langKey: String, val color: Int) {
    VALID("\u2717", -0xff0100), INVALID("\u2717", -0x10000), UNKNOWN("\u2026", -0x666667);

    override fun toString(): String {
        return langKey
    }
}

object LoginUtil {
    private val mc = MinecraftClient.getInstance()
    private var yas: YggdrasilAuthenticationService? = null
    private var yua: YggdrasilUserAuthentication? = null
    private var ymss: YggdrasilMinecraftSessionService? = null

    init {
        yas = YggdrasilAuthenticationService(MinecraftClient.getInstance().networkProxy, "")
        yua = yas!!.createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication
        ymss = yas!!.createMinecraftSessionService() as YggdrasilMinecraftSessionService
    }

    private fun setSession(newSession: Session?): Boolean {
        if (mc == null) return false
        var newField: Field? = null
        for (field in mc.javaClass.declaredFields) {
            if (field.name.equals("session", ignoreCase = true) || field.name.equals("field_1726", ignoreCase = true)) {
                newField = field
            }
        }
        return try {
            newField!!.isAccessible = true
            newField[mc] = newSession
            newField.isAccessible = false
            true
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            false
        }
    }

    @JvmStatic
    fun loginWithPass(mail: String, pass: String): Boolean {
        if (mail.isEmpty() || pass.isEmpty()) return false
        val auth = YggdrasilAuthenticationService(Proxy.NO_PROXY, "").createUserAuthentication(Agent.MINECRAFT) as YggdrasilUserAuthentication
        auth.setUsername(mail); auth.setPassword(pass)
        try { auth.logIn() } catch (e: AuthenticationException) { e.printStackTrace(); return false }
        val account = Session(
            auth.selectedProfile.name,
            auth.selectedProfile.id.toString(),
            auth.authenticatedToken,
            "mojang"
        )
        return setSession(account)
    }

    fun loginCracked(username: String): Boolean {
        if (username.isEmpty()) return false
        val session = Session(username, UUID.randomUUID().toString(), "0", "legacy")
        return setSession(session)
    }

    fun login(username: String): Session? {
        return try {
            val uuid = UUID.nameUUIDFromBytes("offline:$username".toByteArray())
            val session = Session(username, uuid.toString(), "invalidtoken", Session.AccountType.LEGACY.name)
            setSession(session)
            session
        } catch (e: Exception) {
            getSession()
        }
    }

    fun login(username: String?, password: String?): CompletableFuture<Session?>? {
        return CompletableFuture.supplyAsync {
            try {
                yua!!.setUsername(username)
                yua!!.setPassword(password)
                yua!!.logIn()
                val name: String = yua!!.selectedProfile.name
                val uuid = UUIDTypeAdapter.fromUUID(yua!!.selectedProfile.id)
                val token: String = yua!!.authenticatedToken
                val type: String = yua!!.userType.getName()
                yua!!.logOut()
                val session = Session(name, uuid, token, type)
                setSession(session)
                return@supplyAsync session
            } catch (e: java.lang.Exception) {
                throw CompletionException(e)
            }
        }
    }

    private var lastStatus = Status.UNKNOWN
    private var lastStatusCheck: Long = 0

    fun getStatus(): CompletableFuture<Status?>? {
        return if (System.currentTimeMillis() - lastStatusCheck < 60000) CompletableFuture.completedFuture(lastStatus) else CompletableFuture.supplyAsync {
            val session = getSession()
            val gp = session!!.profile
            val token = session.accessToken
            val id = UUID.randomUUID().toString()
            lastStatus = try {
                ymss!!.joinServer(gp, token, id)
                if (ymss!!.hasJoinedServer(gp, id, null).isComplete) {
                    Status.VALID
                } else {
                    Status.INVALID
                }
            } catch (e: AuthenticationException) {
                Status.INVALID
            }
            lastStatusCheck = System.currentTimeMillis()
            lastStatus
        }
    }

    fun getSession(): Session? {
        return MinecraftClient.getInstance().session
    }

    private fun setProxy(proxy: Proxy?): Boolean {
        val mc = MinecraftClient.getInstance()
        var newField: Field? = null
        for (field in mc.javaClass.declaredFields) {
            if (field.name.equals("netProxy", ignoreCase = true)) {
                newField = field
            }
        }
        return try {
            newField!!.isAccessible = true
            newField[mc] = proxy
            newField.isAccessible = false
            true
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
            false
        }
    }

    fun connectProxy(proxyIP: String?, proxyPort: Int, type: Proxy.Type?): Boolean {
        val proxy = Proxy(type, InetSocketAddress(proxyIP, proxyPort))
        return setProxy(proxy)
    }
}

fun runScript(script: List<String>) {
    val mc = MinecraftClient.getInstance()
    val player = mc.player?: return
    val intManager = mc.interactionManager?: return
    Thread {
        try {
            val scriptIter = script.listIterator()
            loop@ while (scriptIter.hasNext()) {
                val next = scriptIter.next()
                when (next) {
                    "JUMP" -> player.jump()

                    "USE" -> player.interact(player, player.activeHand)
                    "HIT" -> {
                        mc.crosshairTarget ?: continue@loop
                        if (mc.crosshairTarget is EntityHitResult) player.attack((mc.crosshairTarget as EntityHitResult).entity)
                        else if (mc.crosshairTarget is BlockHitResult) intManager.breakBlock((mc.crosshairTarget as BlockHitResult).blockPos)
                    }
                }
            }
        } catch (e: Throwable) {
            sendMessage("Script failed to execute", Color.RED)
        }
    }.start()
}

object KeyUtil {
    val keys = LinkedHashMap<String, Int>()

    fun isNumeric(str: String): Boolean {
        try {
            str.toInt()
        } catch (nfe: NumberFormatException) {
            return false
        }
        return true
    }

    fun getKeyCode(char: String): Int {
        if (char.length != 1) return -1;
        return keys.getValue(char.toUpperCase())
    }

    init {
//        keys["!"] =
        keys[" "] = 32
        keys["'"] = 39
        keys[","] = 44
        keys["-"] = 45
        keys["."] = 46
        keys["/"] = 47
        keys["0"] = 48
        keys["1"] = 49
        keys["2"] = 50
        keys["3"] = 51
        keys["4"] = 52
        keys["5"] = 53
        keys["6"] = 54
        keys["7"] = 55
        keys["8"] = 56
        keys["9"] = 57
        keys[";"] = 59
        keys["="] = 61
        keys["A"] = 65
        keys["B"] = 66
        keys["C"] = 67
        keys["D"] = 68
        keys["E"] = 69
        keys["F"] = 70
        keys["G"] = 71
        keys["H"] = 72
        keys["I"] = 73
        keys["J"] = 74
        keys["K"] = 75
        keys["L"] = 76
        keys["M"] = 77
        keys["N"] = 78
        keys["O"] = 79
        keys["P"] = 80
        keys["Q"] = 81
        keys["R"] = 82
        keys["S"] = 83
        keys["T"] = 84
        keys["U"] = 85
        keys["V"] = 86
        keys["W"] = 87
        keys["X"] = 88
        keys["Y"] = 89
        keys["Z"] = 90
        keys["`"] = 192
        keys["["] = 219
        keys["\\"] = 220
        keys["]"] = 221
    }
}

object ItemUtil {
    private val pickaxeList = listOf(
        Items.WOODEN_PICKAXE,
        Items.STONE_PICKAXE,
        Items.IRON_PICKAXE,
        Items.GOLDEN_PICKAXE,
        Items.DIAMOND_PICKAXE,
        Items.NETHERITE_PICKAXE
    )

    fun isPickaxe(item: Item): Boolean {
        return pickaxeList.contains(item)
    }

    fun equipBestTool(blockState: BlockState) {
        var bestSlot = -1
        var max = 0.0
        for (i in 0..8) {
            val stack = mc.player?.inventory?.getStack(i)
            if (stack != null) {
                if (stack.isEmpty) continue
            }
            var speed = stack?.getMiningSpeedMultiplier(blockState)
            var eff: Int
            if (speed != null) {
                if (speed > 1) {
                    speed += (if (EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack).also {
                                eff = it
                            } > 0) (eff.toDouble().pow(2.0) + 1) else 0.0).toFloat()
                    if (speed > max) {
                        max = speed.toDouble()
                        bestSlot = i
                    }
                }
            }
        }
        if (bestSlot != -1) equip(bestSlot)
    }

    fun equipBestWeapon(): Int {
        var bestSlot = -1
        var maxDamage = 0.0
        for (i in 0..8) {
            val stack = mc.player?.inventory?.getStack(i)
            if (stack != null) {
                if (stack.isEmpty) continue
            }
            if (stack != null) {
                if (stack.item is MiningToolItem || stack.item is SwordItem) {
                    // Not sure of the best way to cast stack.item as either SwordItem or MiningToolItem
                    val damage = if (stack.item is SwordItem) {
                        (stack.item as SwordItem).attackDamage + EnchantmentHelper.getAttackDamage(
                            stack,
                            EntityGroup.DEFAULT
                        ).toDouble()
                    } else {
                        (stack.item as MiningToolItem).attackDamage + EnchantmentHelper.getAttackDamage(
                            stack,
                            EntityGroup.DEFAULT
                        ).toDouble()
                    }
                    if (damage > maxDamage) {
                        maxDamage = damage
                        bestSlot = i
                    }
                }
            }
        }
        if (bestSlot != -1) equip(bestSlot)
        return bestSlot
    }

    fun equip(slot: Int) {
        mc.player?.inventory?.selectedSlot = slot
        (mc.interactionManager as IClientPlayerInteractionManager).invokeSyncSelectedSlot()
    }

    private val shulkerList = listOf(
        Blocks.SHULKER_BOX,
        Blocks.WHITE_SHULKER_BOX,
        Blocks.ORANGE_SHULKER_BOX,
        Blocks.MAGENTA_SHULKER_BOX,
        Blocks.LIGHT_BLUE_SHULKER_BOX,
        Blocks.YELLOW_SHULKER_BOX,
        Blocks.LIME_SHULKER_BOX,
        Blocks.PINK_SHULKER_BOX,
        Blocks.GRAY_SHULKER_BOX,
        Blocks.LIGHT_GRAY_SHULKER_BOX,
        Blocks.CYAN_SHULKER_BOX,
        Blocks.PURPLE_SHULKER_BOX,
        Blocks.BLUE_SHULKER_BOX,
        Blocks.BROWN_SHULKER_BOX,
        Blocks.GREEN_SHULKER_BOX,
        Blocks.RED_SHULKER_BOX,
        Blocks.BLACK_SHULKER_BOX
    )

    fun isShulkerBox(item: Item): Boolean {
        return item is BlockItem && isShulkerBox(item.block)
    }

    fun isShulkerBox(block: Block): Boolean {
        return shulkerList.contains(block)
    }

    @JvmStatic
    fun isShulkerBox(item: ItemStack): Boolean {
        return (item.item as BlockItem).block is ShulkerBoxBlock
    }

    @JvmStatic
    fun getItemsInShulker(item: ItemStack): List<ItemStack> {
        val items: MutableList<ItemStack> =
                ArrayList(Collections.nCopies(27, ItemStack(Items.AIR)))
        val nbt = item.tag
        if (nbt != null && nbt.contains("BlockEntityTag")) {
            val nbtt = nbt.getCompound("BlockEntityTag")
            if (nbtt.contains("Items")) {
                val nbtList = nbtt["Items"] as ListTag?
                for (i in nbtList!!.indices) {
                    items[nbtList.getCompound(i).getByte("Slot").toInt()] = ItemStack.fromTag(nbtList.getCompound(i))
                }
            }
        }
        return items
    }
}

object FabricReflect {
    fun getField(cls: Class<*>?, obfName: String, deobfName: String): Field? {
        if (cls == null) return null
        var field: Field? = null
        var cls1 = cls
        while (cls1 != null) {
            try {
                field = cls1.getDeclaredField(obfName)
            } catch (e: java.lang.Exception) {
                try {
                    field = cls1.getDeclaredField(deobfName)
                } catch (e1: java.lang.Exception) {
                    cls1 = cls1.superclass
                    continue
                }
            }
            if (!field!!.isAccessible) {
                field.isAccessible = true
            }
            return field
            cls1 = cls1.superclass
        }
        for (class1 in ClassUtils.getAllInterfaces(cls)) {
            field = try {
                class1.getField(obfName)
            } catch (e: java.lang.Exception) {
                try {
                    class1.getField(deobfName)
                } catch (e1: java.lang.Exception) {
                    continue
                }
            }
            return field
        }
        throw RuntimeException("Error reflecting field: " + deobfName + "/" + obfName + " @" + cls.simpleName)
    }

    fun getFieldValue(target: Any?, obfName: String, deobfName: String): Any? {
        if (target == null) return null
        val cls: Class<*> = target.javaClass
        var field: Field? = null
        var cls1: Class<*>? = cls
        while (cls1 != null) {
            try {
                field = cls1.getDeclaredField(obfName)
            } catch (e: java.lang.Exception) {
                try {
                    field = cls1.getDeclaredField(deobfName)
                } catch (e1: java.lang.Exception) {
                    cls1 = cls1.superclass
                    continue
                }
            }
            if (!field!!.isAccessible) {
                field.isAccessible = true
            }
            return try {
                field[target]
            } catch (e: java.lang.Exception) {
                throw RuntimeException("Error getting reflected field value: " + deobfName + "/" + obfName + " @" + target.javaClass.simpleName)
            }
            cls1 = cls1.superclass
        }
        for (class1 in ClassUtils.getAllInterfaces(cls)) {
            field = try {
                class1.getField(obfName)
            } catch (e: java.lang.Exception) {
                try {
                    class1.getField(deobfName)
                } catch (e1: java.lang.Exception) {
                    continue
                }
            }
            return try {
                field!![target]
            } catch (e: java.lang.Exception) {
                throw RuntimeException("Error getting reflected field value: " + deobfName + "/" + obfName + " @" + target.javaClass.simpleName)
            }
        }
        throw RuntimeException("Error getting reflected field value: " + deobfName + "/" + obfName + " @" + target.javaClass.simpleName)
    }

    fun writeField(target: Any?, value: Any?, obfName: String, deobfName: String) {
        if (target == null) return
        val cls: Class<*> = target.javaClass
        val field = getField(cls, obfName, deobfName)
        if (!field!!.isAccessible) {
            field.isAccessible = true
        }
        try {
            field[target] = value
        } catch (e: java.lang.Exception) {
            throw RuntimeException("Error writing reflected field: " + deobfName + "/" + obfName + " @" + target.javaClass.simpleName)
        }
    }

    fun invokeMethod(target: Any, obfName: String, deobfName: String, vararg args: Any?): Any? {
        /* i just gave up here */
        var o: Any? = null
        o = try {
            MethodUtils.invokeMethod(target, true, obfName, *args)
        } catch (e: java.lang.Exception) {
            try {
                MethodUtils.invokeMethod(target, true, deobfName, *args)
            } catch (e1: java.lang.Exception) {
                throw RuntimeException("Error reflecting method: " + deobfName + "/" + obfName + " @" + target.javaClass.simpleName)
            }
        }
        return o
    }
}

object EntityUtils {
    fun notSelf(e: Entity): Boolean {
        return e !== mc.player && e !== mc.cameraEntity
    }

    fun isAnimal(e: Entity?): Boolean {
        return e is AnimalEntity ||
                e is AmbientEntity ||
                e is WaterCreatureEntity ||
                e is GolemEntity && !e.handSwinging ||
                e is VillagerEntity
    }

    fun isLiving(e: Entity?): Boolean {
        return e is LivingEntity
    }

    fun isHostile(e: Entity?): Boolean {
        return e is HostileEntity && e !is PiglinEntity && e !is EndermanEntity ||
                e is PiglinEntity && e.isAngryAt(mc.player) ||
                e is WolfEntity && e.isAttacking && e.ownerUuid !== mc.player!!.uuid ||
                e is EndermanEntity && e.isAngry ||
                e is GolemEntity && e.handSwinging ||
                e is MobEntity && e.isAttacking
    }

    fun isNeutral(e: Entity?): Boolean {
        return e is PiglinEntity && !e.isAngryAt(mc.player) ||
                e is WolfEntity && (!e.isAttacking || e.ownerUuid === mc.player!!.uuid) ||
                e is EndermanEntity && !e.isAngry ||
                e is GolemEntity && !e.handSwinging
    }
    fun isVehicle(e: Entity?): Boolean {
        return e is BoatEntity ||
                e is MinecartEntity ||
                (e is Saddleable && e.isSaddled)
    }

    private val mc = MinecraftClient.getInstance()
    private fun lookPacket(yaw: Double, pitch: Double) {
        if (mc.player == null) return
        mc.player!!.networkHandler.sendPacket(
            PlayerMoveC2SPacket.LookOnly(
                yaw.toFloat(),
                pitch.toFloat(),
                mc.player!!.isOnGround
            )
        )
    }

    private fun lookClient(yaw: Double, pitch: Double) {
        if (mc.player == null) return
        mc.player!!.yaw = yaw.toFloat()
        mc.player!!.pitch = pitch.toFloat()
    }

    fun lookAt(point: Vec3d, packet: Boolean) {
        if (mc.player == null) return
        var lookx = mc.player!!.x - point.getX()
        var looky = mc.player!!.y - point.getY()
        var lookz = mc.player!!.z - point.getZ()
        val len = sqrt(lookx * lookx + looky * looky + lookz * lookz)
        lookx /= len
        looky /= len
        lookz /= len
        var pitch = asin(looky)
        var yaw = atan2(lookz, lookx)

        //to degree
        pitch = pitch * 180.0 / Math.PI
        yaw = yaw * 180.0 / Math.PI
        yaw += 90.0
        if (packet) {
            lookPacket(yaw, pitch)
        } else {
            lookClient(yaw, pitch)
        }
    }

    fun getMovementYaw(): Double {
        var strafe = 90f
        strafe += if(mc.player!!.input.movementForward != 0F) mc.player!!.input.movementForward * 0.5F else 1F
        var yaw = mc.player!!.yaw - strafe
        yaw -= if(mc.player!!.input.movementForward < 0F)180 else 0
        return Math.toRadians(yaw.toDouble())
    }


    fun getSpeed(): Double {
        return sqrt(mc.player!!.velocity.x.pow(2) + mc.player!!.velocity.z.pow(2))
    }

    fun setSpeed(speed: Double) {
        mc.player!!.setVelocity(-sin(getMovementYaw()) * speed, mc.player!!.velocity.y, cos(getMovementYaw()) * speed)
    }
}

object DamageUtil {
    private val mc: MinecraftClient = MinecraftClient.getInstance()
    private val damageCache: HashMap<Entity, Float> = HashMap()

    fun getExplosionDamage(basePos: BlockPos, target: LivingEntity): Float {
        if (mc.world!!.difficulty == Difficulty.PEACEFUL) return 0f
        if (damageCache.containsKey(target)) return damageCache[target]!!
        val crystalPos: Vec3d = Vec3d.of(basePos).add(0.5, 1.0, 0.5)
        val explosion = Explosion(
            mc.world,
            null,
            crystalPos.x,
            crystalPos.y,
            crystalPos.z,
            6f,
            false,
            Explosion.DestructionType.DESTROY
        )
        val power = 12.0
        if (!mc.world!!.getOtherEntities(
                null as Entity?, Box(
                    MathHelper.floor(crystalPos.x - power - 1.0).toDouble(),
                    MathHelper.floor(
                        crystalPos.y - power - 1.0
                    ).toDouble(),
                    MathHelper.floor(crystalPos.z - power - 1.0).toDouble(),
                    MathHelper.floor(crystalPos.x + power + 1.0).toDouble(),
                    MathHelper.floor(
                        crystalPos.y + power + 1.0
                    ).toDouble(),
                    MathHelper.floor(crystalPos.z + power + 1.0).toDouble()
                )
            ).contains(target)) {
            damageCache[target] = 0f
            return 0f
        }
        if (!target.isImmuneToExplosion) {
            val double_8 = MathHelper.sqrt(target.squaredDistanceTo(crystalPos)) / power
            if (double_8 <= 1.0) {
                var x: Double = target.x - crystalPos.x
                var y: Double = target.y + target.standingEyeHeight - crystalPos.y
                var z: Double = target.z - crystalPos.z
                val double_12 = MathHelper.sqrt(x * x + y * y + z * z).toDouble()
                if (double_12 != 0.0) {
                    x /= double_12
                    y /= double_12
                    z /= double_12
                    val double_13 = Explosion.getExposure(crystalPos, target).toDouble()
                    val double_14 = (1.0 - double_8) * double_13

                    // entity_1.damage(explosion.getDamageSource(), (float)((int)((double_14 *
                    // double_14 + double_14) / 2.0D * 7.0D * power + 1.0D)));
                    var toDamage = Math.floor((double_14 * double_14 + double_14) / 2.0 * 7.0 * power + 1.0).toFloat()
                    if (target is PlayerEntity) {
                        if (mc.world!!.difficulty == Difficulty.EASY) toDamage = Math.min(
                            toDamage / 2.0f + 1.0f,
                            toDamage
                        ) else if (mc.world!!.difficulty == Difficulty.HARD) toDamage = toDamage * 3.0f / 2.0f
                    }

                    // Armor
                    toDamage = DamageUtil.getDamageLeft(
                        toDamage, target.armor.toFloat(), target.getAttributeInstance(
                            EntityAttributes.GENERIC_ARMOR_TOUGHNESS
                        )!!.value.toFloat()
                    )

                    // Enchantments
                    if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                        val resistance = (target.getStatusEffect(StatusEffects.RESISTANCE)!!.amplifier + 1) * 5
                        val int_2 = 25 - resistance
                        val resistance_1 = toDamage * int_2
                        toDamage = Math.max(resistance_1 / 25.0f, 0.0f)
                    }
                    if (toDamage <= 0.0f) { toDamage = 0.0f } else {
                        val protAmount = EnchantmentHelper.getProtectionAmount(
                            target.armorItems,
                            explosion.damageSource
                        )
                        if (protAmount > 0) { toDamage = DamageUtil.getInflictedDamage(toDamage, protAmount.toFloat()) }
                    }
                    damageCache[target] = toDamage
                    return toDamage
                }
            }
        }
        damageCache[target] = 0f
        return 0f
    }

    fun getDamageCache(): HashMap<Entity, Float> {
        return damageCache
    }
}

