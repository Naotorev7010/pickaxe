package com.example.pickaxe
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.RayTraceResult

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

        val eyeLocation = player.eyeLocation
        val direction = eyeLocation.direction
        val rayResult: RayTraceResult? =
            player.world.rayTraceBlocks(eyeLocation,direction,5.0)
        val hitFace: BlockFace = rayResult?.hitBlockFace ?: return

        val cx = block.x
        val cy = block.y
        val cz = block.z

        when(hitFace){
            BlockFace.UP, BlockFace.DOWN -> {
                //xz方面
                for(dx in -1..1){
                    for (dz in -1..1){
                        if(dx == 0 && dz == 0)continue
                         val target = world.getBlockAt( cx + dx,cy, cz + dz)
                        breakBlock(target,player,item)
                    }
                }
            }

            BlockFace.NORTH, BlockFace.SOUTH -> {
                //xy方面
                for(dx in -1..1){
                    for (dy in -1..1){
                        if (dx == 0 && dy == 0)continue //プレイヤーがすでに壊したブロック
                        val target = world.getBlockAt(cx + dx,cy + dy,cz)
                        breakBlock(target,player,item)
                    }
                }
            }

            BlockFace.EAST, BlockFace.WEST ->{
                //xy平面
                for (dy in -1..1){
                    for (dz in -1..1){
                        if(dy == 0 && dz == 0)continue//プレイヤーがすでに壊したブロック
                        val target = world.getBlockAt(cx ,cy + dy,cz + dz)
                        breakBlock(target,player,item)
                    }
                }
            }
            else ->return

        }



//        for(dx in-1..1){
//            for (dz in -1..1){
//                if (dx == 0 && dz == 0) continue //プレイヤーがすでに壊してあるブロック
//                val target = world.getBlockAt(cx + dx,cy, cz + dz)
//
//
//            }
//        }
    }

    private fun breakBlock(block: Block,player: Player,item: ItemStack){
        if(block.type == Material.AIR || block.type == Material.BEDROCK)return
        if(player.gameMode == GameMode.CREATIVE) block.type = Material.AIR
        else block.breakNaturally(item)
    }

}
