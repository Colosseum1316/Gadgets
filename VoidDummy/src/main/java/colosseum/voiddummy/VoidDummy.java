package colosseum.voiddummy;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class VoidDummy extends JavaPlugin {

    private final VoidDummyEventListener eventListener = new VoidDummyEventListener();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(eventListener, this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(eventListener);
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new ChunkGenerator() {
            @Override
            public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                return createChunkData(world);
            }
        };
    }
}
