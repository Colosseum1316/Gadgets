package colosseum.voiddummy;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class VoidDummy extends JavaPlugin implements CommandExecutor {

    private final VoidDummyEventListener eventListener = new VoidDummyEventListener();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(eventListener, this);
        getCommand("voiddummy").setExecutor(this);
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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender) && !(sender instanceof RemoteConsoleCommandSender)) {
            return false;
        }
        getServer().getPluginManager().disablePlugin(this);
        return true;
    }
}
