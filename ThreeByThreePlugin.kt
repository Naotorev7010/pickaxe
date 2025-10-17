// ファイル: src/main/kotlin/com/example/threebythree/ThreeByThreePlugin.kt
package com.example.pickaxe

import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

/**
 * Paper/Spigot プラグイン (Kotlin) — プレイヤーがピッケルでブロックを壊したとき、
 * そのブロックを中心に 3x3x1 の範囲を同時に壊す機能を実装します。
 *
 * 動作のポイント:
 * - プレイヤーがメインハンドに持っているアイテムをチェックし、Tag.PICKAXES を使って
 *   それがピッケルかどうかを判定します。
 * - 元の BlockBreakEvent はキャンセルせず、代わりに周囲の 8 ブロックをプログラム的に破壊します。
 *   プレイヤーは中心ブロックのドロップを通常通り取得し、ツールの耐久値はその1回分だけ減少します。
 *   周囲のブロックは Block.breakNaturally(itemInMainHand) で壊され、ドロップは発生しますが、
 *   ツールの耐久値は自動では減りません。
 * - クリエイティブモードの場合、周囲ブロックはドロップなしで AIR に置き換えられます。
 * - BEDROCK のような壊せないブロックや AIR はスキップされます。
 * - この実装はシンプルにしており、保護プラグイン（例: WorldGuard）、ツール耐久の管理、
 *   権限設定、範囲や形状の変更などを追加する場合は別途拡張が必要です。
 */
class ThreeByThreePlugin : JavaPlugin(), Listener {

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        logger.info("ThreeByThreePlugin enabled")
    }

    override fun onDisable() {
        logger.info("ThreeByThreePlugin disabled")
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        // 他のプラグインがキャンセルしている場合は処理しない
        if (event.isCancelled) return

        // メインハンドのアイテムがピッケルかどうかを確認
        val item = player.inventory.itemInMainHand
        if (item == null) return
        if (!Tag.PICKAXES.isTagged(item.type)) return

        // 空気や破壊不能ブロックの場合は無視
        if (block.type == Material.AIR || block.type == Material.BEDROCK) return

        // 同じY座標の3x3範囲を処理（中心のブロックは除外）
        val world = block.world
        val cx = block.x
        val cy = block.y
        val cz = block.z

        for (dx in -1..1) {
            for (dz in -1..1) {
                if (dx == 0 && dz == 0) continue // 中心ブロックはプレイヤーが既に破壊

                val target = world.getBlockAt(cx + dx, cy, cz + dz)

                // 空気やBEDROCKはスキップ
                if (target.type == Material.AIR || target.type == Material.BEDROCK) continue

                // クリエイティブの場合はドロップなしで削除
                if (player.gameMode.name == "CREATIVE") {
                    target.type = Material.AIR
                    continue
                }

                // プレイヤーの持つアイテムを使って自然破壊（ドロップあり）
                target.breakNaturally(item)
            }
        }

        // 連続でブロックを壊したときに二重処理されないよう、必要ならクールダウン処理を追加可能
    }
}