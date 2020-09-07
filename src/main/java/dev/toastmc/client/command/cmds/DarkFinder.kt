package dev.toastmc.client.command.cmds

import dev.toastmc.client.command.CommandManifest

@CommandManifest(
    label = "DarkFinder",
    description = "Goes to a dark position.",
    aliases = ["dark"],
    usage = ".DarkFinder <threshold (<=)> [distance]"
)
class DarkFinder {
//    private var matches = ConcurrentLinkedQueue<BlockPos>()
//    private var threshold = -1
//
//    private var finder = GlobalScope.async {}
//    private var running: AtomicBoolean = AtomicBoolean(false)
//
//    override fun run(args: Array<String>) {
//        if (args.isEmpty() || args[0].toIntOrNull() == null) {
//            MessageUtil.sendMessage("Not enough arguments.", MessageUtil.Color.DARK_RED)
//            return
//        }
//        if (running.get()) {
//            MessageUtil.sendMessage("Finder is still active.", MessageUtil.Color.DARK_RED)
//            return
//        }
//        threshold = max(args[0].toIntOrNull()!!, 0)
//        running.set(true)
//        finder = GlobalScope.async {
//            if (mc.player != null && !BaritoneAPI.getProvider().primaryBaritone.customGoalProcess.isActive) {
//                val distance = if (args.size >= 2 && args[1].toIntOrNull() != null) min(args[1].toInt(), mc.options.viewDistance) else mc.options.viewDistance
//                MessageUtil.sendMessage("DarkFinder started to find blocks with light level <= $threshold within the range of $distance chunks.", MessageUtil.Color.GREEN)
//                var ran = 0
//                for (x in -distance..distance) {
//                    for (z in -distance..distance) {
//                        GlobalScope.async {
//                            val chunk = mc.world!!.getChunk(mc.player!!.blockPos.add(x shl 4, 0, z shl 4))
//                            for (x2 in chunk.pos.startX..chunk.pos.endX) {
//                                for (z2 in chunk.pos.startZ..chunk.pos.endZ) {
//                                    val height = WorldUtil.getHighestYAtXZ(x2, z2) + 1
//                                    MessageUtil.sendMessage("Height: $height", MessageUtil.Color.GREEN)
//                                    for (y in 0..height) {
//                                        if (matches.size > 0) break
//                                        val pos = BlockPos(x2, y, z2)
//                                        val blockState = mc.world!!.getBlockState(pos)
//                                        val under = mc.world!!.getBlockState(pos.add(0, -1, 0))
//                                        System.out.println("x2: $x2, z2: $z2\n")
//                                        if (
//                                            WorldInteractionUtil.REPLACEABLE.contains(blockState.block) && !WorldInteractionUtil.REPLACEABLE.contains(under.block) &&
//                                            //mc.world!!.chunkManager.lightingProvider.get(LightType.BLOCK).getLightLevel(pos) <= threshold
//                                            mc.world!!.getLightLevel(LightType.BLOCK, pos) <= threshold
//                                        ) {
//                                            matches.add(pos)
//                                            System.out.println(pos)
//                                            break
//                                        }
//                                    }
//                                    if (matches.size > 0) break
//                                }
//                                if (matches.size > 0) break
//                            }
//                            ++ran
//                        }.start()
//                        if (matches.size > 0) break
//                    }
//                    if (matches.size > 0) break
//                }
//                val async = GlobalScope.async {
//                    while (ran < (2 * distance + 1) * (2 * distance + 1) && matches.size > 0) { delay(100L) }
//                }
//                async.await()
//                MessageUtil.sendMessage(if (matches.size > 0) "DarkFinder found a dark spot at ${matches.elementAt(0)}" else "DarkFinder did not find any dark spots within render distance", MessageUtil.Color.AQUA)
//                if (matches.size > 0) {
//                    System.out.println(matches.elementAt(0))
//                    BaritoneAPI.getProvider().primaryBaritone.customGoalProcess.setGoalAndPath(GoalBlock(matches.elementAt(0)))
//                    matches.clear()
//                }
//                running.set(false)
//            }
//        }
//    }
}