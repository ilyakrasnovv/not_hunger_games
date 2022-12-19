package hunger.hunger.utilities

import hunger.hunger.Hunger
import net.kyori.adventure.text.Component
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


private const val CHUNK_SIZE = 16
private const val BASE_RADIUS = 1
private const val MIN_HEIGHT = -64
private const val MAX_HEIGHT = 320
private /**/  val HARD_BLOCK = Material.OBSIDIAN
private /**/  val STARTER_PACK = listOf<ItemStack>(
    ItemStack(Material.CRAFTING_TABLE, 1),
    ItemStack(Material.DIRT, 64),
    ItemStack(Material.APPLE, 32),
    ItemStack(Material.LADDER, 64),
    ItemStack(Material.WATER_BUCKET, 1),
    ItemStack(Material.ACACIA_BOAT, 1),
    ItemStack(Hunger.hConfig.UNCAPTURED_BASE_MATERIAL).apply {
        val meta = itemMeta
        meta.displayName(Component.text("База"))
        itemMeta = meta
    },
)

fun generateHWorld(playersAmount: Int): Pair<World, List<Location>> {
    val spawnLocations: List<Location>
    return initWorld().apply {
        spawnLocations = buildHWorld(playersAmount)
    }.run {
        Pair(this, spawnLocations)
    }
}

private fun World.buildHWorld(playersAmount: Int): List<Location> {
    val radius = Hunger.hConfig.DISTANCE_BETWEEN_IN_CHUNKS * CHUNK_SIZE * playersAmount / (2 * PI)
    val positions = List(playersAmount) {
        val angle = 2.0 * PI * (it.toDouble() / playersAmount)
        Pair(
            (cos(angle) * radius).roundToInt(),
            (sin(angle) * radius).roundToInt()
        )
    }
    return positions.map { (x, z) ->
        generateBase(x, z)
    }
}

private fun World.generateBase(x: Int, z: Int): Location {
    val center = (MIN_HEIGHT..MAX_HEIGHT).reversed().map { y ->
        Location(this, x.toDouble(), y.toDouble(), z.toDouble())
    }.find { loc ->
        !getBlockAt(loc).isEmpty
    }!!.apply {
        y = min(MAX_HEIGHT.toDouble() - 4, y + BASE_RADIUS + 1)
    }

    fun deltaIterator(initialAxisValue: Int, block: (Int) -> Unit) {
        ((initialAxisValue + -BASE_RADIUS)..(initialAxisValue + BASE_RADIUS)).forEach { delta ->
            block(delta)
        }
    }
    deltaIterator(center.x.toInt()) { nx ->
        deltaIterator(center.y.toInt()) { ny ->
            deltaIterator(center.z.toInt()) { nz ->
                getBlockAt(nx, ny, nz).type = HARD_BLOCK
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
    return center.clone().apply {
        this.x += 1
        this.y += 2
    }
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



