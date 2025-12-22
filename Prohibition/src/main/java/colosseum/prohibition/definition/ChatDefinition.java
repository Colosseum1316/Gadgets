package colosseum.prohibition.definition;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatDefinition extends AbstractDefinition {

    public ChatDefinition() {
        super("chat");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void cancellation(AsyncPlayerChatEvent event) {
        if (event.getPlayer().hasPermission("colosseum.prohibition.chat.bypass")) {
            return;
        }
        if (!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void monitor(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            event.getPlayer().sendMessage(ChatColor.GRAY + "Chat is silenced.");
        }
    }
}
