package colosseum.viptickets;

import com.google.common.collect.ImmutableSet;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// https://www.spigotmc.org/wiki/creating-a-config-file/
// https://www.spigotmc.org/threads/how-to-add-an-array-list-in-config-yml-using-adddefault.235654/
public final class VIPTickets extends JavaPlugin {
    static ImmutableSet<UUID> TICKETS;

    private VIPTicketsEventListener eventListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        FileConfiguration config = getConfig();
        config.addDefault(ConfigKeys.TICKETS, new ArrayList<String>());
        config.options().copyDefaults(true);
        saveConfig();

        List<String> pending = config.getStringList(ConfigKeys.TICKETS);
        if (pending.isEmpty()) {
            getLogger().info("No tickets to prepare!");
            TICKETS = ImmutableSet.of();
        } else {
            getLogger().info(String.format("Preparing %d tickets!", pending.size()));
            ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();
            for (String s : pending) {
                try {
                    UUID u = UUID.fromString(s);
                    getLogger().info(String.format("Preparing a ticket for %s", u));
                    builder.add(u);
                } catch (Exception e) {
                    getLogger().warning("Invalid UUID: " + s);
                }
            }
            TICKETS = builder.build();
        }

        eventListener = new VIPTicketsEventListener();
        getServer().getPluginManager().registerEvents(eventListener, this);
        if (!TICKETS.isEmpty()) {
            getLogger().info("Please welcome our top-notch guests!!!");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("Goodbye!");
        saveConfig();
        if (eventListener != null) {
            HandlerList.unregisterAll(eventListener);
        }
        eventListener = null;
    }
}
