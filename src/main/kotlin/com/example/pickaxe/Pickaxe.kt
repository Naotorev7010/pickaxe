package com.example.pickaxe

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin

class Pickaxe : JavaPlugin(), Listener {
    override fun onEnable() {
        server.pluginManager.registerEvents(this,this)
        logger.info("Pickaxe enabled")
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        val world = block.world

        val item = player.inventory.itemInMainHand
        if(!Tag.ITEMS_PICKAXES.isTagged(item.type))return

        val cx = block.x
        val cy = block.y
        val cz = block.z

        for(dx in-1..1){
            for (dz in -1..1){
                if (dx == 0 && dz == 0) continue //プレイヤーがすでに壊してあるブロック
                val target = world.getBlockAt(cx + dx,cy, cz + dz)
                if(target.type == Material.AIR || target.type == Material.BEDROCK)continue
                if(player.gameMode == GameMode.CREATIVE) target.type = Material.AIR
                else target.breakNaturally(item)

            }
        }
    }

}
