package com.example.pickaxe

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class BlockBreaker3D : JavaPlugin(), Listener {

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        logger.info("BlockBreaker3D enabled!")
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val world = block.world
        val item = player.inventory.itemInMainHand

        // 壊されたブロックの位置
        val cx = block.x
        val cy = block.y
        val cz = block.z

        // どの面から壊したかを取得
        val face = event.blockFace

        // 破壊範囲を決定
        when (face.axis) {
            org.bukkit.Axis.Y -> {
                // 上下から掘った → XZ平面
                for (dx in -1..1) {
                    for (dz in -1..1) {
                        if (dx == 0 && dz == 0) continue
                        val target = world.getBlockAt(cx + dx, cy, cz + dz)
                        breakBlock(target, player, item)
                    }
                }
            }

            org.bukkit.Axis.X -> {
                // 横（東西）から掘った → YZ平面
                for (dy in -1..1) {
                    for (dz in -1..1) {
                        if (dy == 0 && dz == 0) continue
                        val target = world.getBlockAt(cx, cy + dy, cz + dz)
                        breakBlock(target, player, item)
                    }
                }
            }

            org.bukkit.Axis.Z -> {
                // 正面（南北）から掘った → XY平面
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        if (dx == 0 && dy == 0) continue
                        val target = world.getBlockAt(cx + dx, cy + dy, cz)
                        breakBlock(target, player, item)
                    }
                }
            }

            else -> {}
        }
    }

    private fun breakBlock(block: org.bukkit.block.Block, player: org.bukkit.entity.Player, item: org.bukkit.inventory.ItemStack) {
        if (block.type == Material.AIR || block.type == Material.BEDROCK) return

        if (player.gameMode.name == "CREATIVE") {
            block.type = Material.AIR
        } else {
            block.breakNaturally(item)
        }
    }
}
