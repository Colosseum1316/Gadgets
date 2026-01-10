package colosseum.legacybossbar;

import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityWither;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class WitherManager implements Listener {
    private final Map<UUID, Integer> activeWithers = new HashMap<>();
    private BukkitTask withersTeleportTask;

    @Setter
    private int offset = -3;

    public void activate() {
        Bukkit.getPluginManager().registerEvents(this, LegacyBossBar.getInstance());
        withersTeleportTask = Bukkit.getScheduler().runTaskTimer(LegacyBossBar.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                int eid = activeWithers.getOrDefault(player.getUniqueId(), -1);
                if (eid == -1) {
                    continue;
                }
                Location loc = player.getLocation().add(0, offset, 0);
                PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(eid, (int) (loc.getX() * 32), (int) (loc.getY() * 32.0), (int) (loc.getZ() * 32.0), (byte) 0, (byte) 0, false);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }, 0, 1L);
    }

    public void deactivate() {
        withersTeleportTask.cancel();
        activeWithers.clear();
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isDead()) {
            return;
        }
        spawnWither(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        despawnWither(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerChangedWorldEvent e) {
        despawnWither(e.getPlayer());
        spawnWither(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        despawnWither(player);
        Bukkit.getScheduler().runTaskLater(LegacyBossBar.getInstance(), () -> {
            if (player.isOnline()) {
                spawnWither(player);
            }
        }, 20L);
    }

    private void spawnWither(Player player) {
        if (activeWithers.containsKey(player.getUniqueId())) {
            return;
        }
        final EntityWither wither = new EntityWither(((CraftWorld) player.getWorld()).getHandle());
        wither.setGoalTarget(null);
        wither.setInvisible(true);
        wither.setCustomName(player.getName());
        wither.setCustomNameVisible(true);
        // https://minecraft.wiki/w/Entity_metadata?oldid=2767708#Living_Entity
        wither.getDataWatcher().watch(0, (byte) (1 << 5));
        wither.getDataWatcher().watch(4, (byte) 1);
        wither.getDataWatcher().watch(15, (byte) 1);
        wither.getDataWatcher().watch(17, 0);
        wither.getDataWatcher().watch(18, 0);
        wither.getDataWatcher().watch(19, 0);
        wither.getDataWatcher().watch(20, 0);
        Location loc = player.getLocation();
        wither.setLocation(loc.getX(), loc.getY() + offset, loc.getZ(), loc.getYaw(), loc.getPitch());
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(wither);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        activeWithers.put(player.getUniqueId(), wither.getId());
    }

    private void despawnWither(Player player) {
        Integer id = activeWithers.remove(player.getUniqueId());
        if (id == null || id == -1) {
            return;
        }
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(id);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
