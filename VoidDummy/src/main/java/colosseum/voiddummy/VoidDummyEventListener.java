package colosseum.voiddummy;

import lombok.NoArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_FULL;

@NoArgsConstructor
public final class VoidDummyEventListener implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        event.disallow(KICK_FULL, "Your IP address is " + event.getAddress().getHostAddress());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPing(ServerListPingEvent event) {
        event.setMotd("");
        event.setMaxPlayers(0);
    }
}
