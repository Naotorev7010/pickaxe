package com.example.pickaxe

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

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

        //  メインハンドがピッケルかどうかを確認
        val item = player.inventory.itemInMainHand
        if (!Tag.ITEMS_PICKAXES.isTagged(item.type)) return

        val direction = player.eyeLocation.direction.normalize()
        val cx = block.x
        val cy = block.y
        val cz = block.z

        // 向いている方向を単純化して「奥行き1」分の軸ベクトルにする
        val axis = getDominantDirection(direction)

        // 3×3×1 範囲を破壊
        for (dx in -1..1) {
            for (dy in -1..1) {
                // 奥行き方向に1ブロックずらす
                val tx = cx + dx * (if (axis.x == 0.0) 1 else 0) + axis.x
                val ty = cy + dy * (if (axis.y == 0.0) 1 else 0) + axis.y
                val tz = cz + dx * (if (axis.z == 0.0) 1 else 0) + axis.z

                val target = world.getBlockAt(tx.toInt(), ty.toInt(), tz.toInt())
                if (target.type == Material.AIR || target.type == Material.BEDROCK) continue

                if (player.gameMode == GameMode.CREATIVE) {
                    target.type = Material.AIR
                } else {
                    target.breakNaturally(item)
                }
            }
        }
    }

    /**
     * プレイヤーの視線方向ベクトルから主要方向（N/S/E/W/UP/DOWN）を決定
     */
    private fun getDominantDirection(vec: Vector): Vector {
        val absX = kotlin.math.abs(vec.x)
        val absY = kotlin.math.abs(vec.y)
        val absZ = kotlin.math.abs(vec.z)

        return when {
            absY > absX && absY > absZ -> {
                if (vec.y > 0) Vector(0, 1, 0) else Vector(0, -1, 0)
            }
            absX > absZ -> {
                if (vec.x > 0) Vector(1, 0, 0) else Vector(-1, 0, 0)
            }
            else -> {
                if (vec.z > 0) Vector(0, 0, 1) else Vector(0, 0, -1)
            }
        }
    }
}
