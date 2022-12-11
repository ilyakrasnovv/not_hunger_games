package hunger.hunger.utilities

import hunger.hunger.Hunger
import org.bukkit.*
import org.bukkit.block.Chest
import org.bukkit.inventory.ItemStack
import java.lang.Math.PI
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Level
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private const val DISTANCE_BETWEEN_IN_CHUNKS = 6
private const val CHUNK_SIZE = 16
private const val BASE_RADIUS = 1
private /**/  val HARD_BLOCK = Material.OBSIDIAN
private /**/  val STARTER_PACK = listOf(
    ItemStack(Material.CRAFTING_TABLE, 1),
    ItemStack(Material.DIRT, 64),
    ItemStack(Material.APPLE, 32),
    ItemStack(Material.LADDER, 64),
    ItemStack(Material.WATER_BUCKET, 1),
    ItemStack(Material.ACACIA_BOAT, 1),
)

fun generateHWorld(playersAmount: Int): World {
    return initWorld().apply {
        buildHWorld(playersAmount)
    }
}

private fun World.buildHWorld(playersAmount: Int) {
    val radius = DISTANCE_BETWEEN_IN_CHUNKS * CHUNK_SIZE * playersAmount / (2 * PI)
    val positions = List(playersAmount) {
        val angle = 2.0 * PI * (it.toDouble() / playersAmount)
        Pair(
            (cos(angle) * radius).roundToInt(),
            (sin(angle) * radius).roundToInt()
        )
    }
    positions.forEach { (x, z) ->
        generateBase(x, z)
    }
}

private fun World.generateBase(x: Int, z: Int) {
    val center = (-64..320).reversed().map { y ->
        Location(this, x.toDouble(), y.toDouble(), z.toDouble())
    }.find { loc ->
        !getBlockAt(loc).isEmpty
    }!!.apply {
        y = min(320.0 - 4, y + 2)
    }

    fun deltaIterator(initialAxisValue: Int, block: (Int) -> Unit) {
        ((initialAxisValue + -BASE_RADIUS)..(initialAxisValue + BASE_RADIUS)).forEach { delta ->
            block(delta)
        }
    }
    deltaIterator(center.x.toInt()) { x ->
        deltaIterator(center.y.toInt()) { y ->
            deltaIterator(center.z.toInt()) { z ->
                getBlockAt(x, y, z).type = HARD_BLOCK
            }
        }
    }
    getBlockAt(center).type = Material.CHEST
    getBlockAt(center.clone().apply {
        y += 2
    }).run {
        this.type = Material.CHEST
        (this.state as Chest).blockInventory.contents = STARTER_PACK.toTypedArray()
    }
    Hunger.instance.logger.log(Level.INFO, "generated base at $center")
}

private fun initWorld(): World {
    fun nameGen(): String = "HWorld/${SimpleDateFormat("yyyy/M/dd/hh-mm-ss").format(Date())}_${
        (1..4).joinToString(separator = "") {
            ('a'..'z').random().toString()
        }
    }"
    return Hunger.instance.server.createWorld(WorldCreator(
        nameGen()
    ).apply {
        environment(World.Environment.NORMAL)
        type(WorldType.NORMAL)
    })!!
}


