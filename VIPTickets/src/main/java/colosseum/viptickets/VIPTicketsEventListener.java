package colosseum.viptickets;

import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_BANNED;
import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER;

@NoArgsConstructor
public final class VIPTicketsEventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        if (event.getUniqueId() == null) {
            event.disallow(KICK_BANNED, "You are not allowed to perform this action.");
            return;
        }
        if (VIPTickets.TICKETS.contains(event.getUniqueId())) {
            Bukkit.getServer().getLogger().info(String.format("WOW!!!!!!!!!! Welcome %s (%s) to the server!!!!!!!!!!!!!!!", event.getName(), event.getUniqueId()));
            event.disallow(KICK_OTHER, "");
        }
    }
}
