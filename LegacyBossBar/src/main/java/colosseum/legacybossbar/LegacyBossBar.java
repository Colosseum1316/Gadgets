package colosseum.legacybossbar;

import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public final class LegacyBossBar extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        EntityWither wither = new EntityWither(((CraftWorld) player.getWorld()).getHandle());
        wither.setGoalTarget(null);
        wither.setInvisible(true);
        wither.setCustomName(player.getName());
        wither.setCustomNameVisible(true);
        wither.getDataWatcher().watch(8, (byte) 0);
        wither.getDataWatcher().watch(15, (byte) 1);
        wither.getDataWatcher().watch(17, 0);
        wither.getDataWatcher().watch(18, 0);
        wither.getDataWatcher().watch(19, 0);
        wither.getDataWatcher().watch(20, 0);
        BukkitTask task = getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
            Location loc = player.getLocation();
            wither.setLocation(loc.getX(), loc.getY() - 3, loc.getZ(), loc.getYaw(), loc.getPitch());
            PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }, 0, 5L);
        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler(priority = EventPriority.MONITOR)
            public void onPlayerQuit(PlayerQuitEvent e) {
                if (!e.getPlayer().equals(player)) {
                    return;
                }
                HandlerList.unregisterAll(this);
                getServer().getScheduler().cancelTask(task.getTaskId());
                PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(wither.getId());
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }, this);
    }
}
