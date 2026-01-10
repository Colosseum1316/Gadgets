package colosseum.legacybossbar;

import lombok.Setter;
import net.minecraft.server.v1_8_R3.EntityEnderDragon;
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

public final class FakeEntityManager implements Listener {
    private final Map<UUID, Integer> activeEntities = new HashMap<>();
    private BukkitTask fakeEntitiesTeleportTask;

    public void activate() {
        Bukkit.getPluginManager().registerEvents(this, LegacyBossBar.getInstance());
        fakeEntitiesTeleportTask = Bukkit.getScheduler().runTaskTimer(LegacyBossBar.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                int eid = activeEntities.getOrDefault(player.getUniqueId(), -1);
                if (eid == -1) {
                    continue;
                }
                Location loc = player.getLocation();
                loc.setY(-200);
                PacketPlayOutEntityTeleport packet = new PacketPlayOutEntityTeleport(eid, (int) (loc.getX() * 32), (int) (loc.getY() * 32.0), (int) (loc.getZ() * 32.0), (byte) 0, (byte) 0, false);
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }, 0, 1L);
    }

    public void deactivate() {
        fakeEntitiesTeleportTask.cancel();
        activeEntities.clear();
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (e.getPlayer().isDead()) {
            return;
        }
        spawnEntity(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        despawnEntity(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerChangedWorldEvent e) {
        despawnEntity(e.getPlayer());
        spawnEntity(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        despawnEntity(player);
        Bukkit.getScheduler().runTaskLater(LegacyBossBar.getInstance(), () -> {
            if (player.isOnline()) {
                spawnEntity(player);
            }
        }, 20L);
    }

    private void spawnEntity(Player player) {
        if (activeEntities.containsKey(player.getUniqueId())) {
            return;
        }
        final EntityEnderDragon dragon = new EntityEnderDragon(((CraftWorld) player.getWorld()).getHandle());
        dragon.setCustomName(player.getName());
        dragon.setCustomNameVisible(false);
        dragon.setGoalTarget(null);
        dragon.setInvisible(true);
        dragon.getDataWatcher().watch(4, (byte) 1);
        Location loc = player.getLocation();
        dragon.setLocation(loc.getX(), 0, loc.getZ(), 0, 0);
        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(dragon);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        activeEntities.put(player.getUniqueId(), dragon.getId());
    }

    private void despawnEntity(Player player) {
        Integer id = activeEntities.remove(player.getUniqueId());
        if (id == null || id == -1) {
            return;
        }
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(id);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
