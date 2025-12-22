package colosseum.prohibition.definition;

import colosseum.prohibition.Prohibition;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class AbstractDefinition implements CommandExecutor, Listener {

    @Getter
    private boolean enabled = false;  // Default is false.

    @Getter
    private final String name;

    protected AbstractDefinition(String name) {
        this.name = name;
    }

    public void configure(FileConfiguration config) {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender || (sender instanceof Player && ((Player) sender).isOp())) {
            activate();
            sender.sendMessage(ChatColor.GRAY + name + (enabled ? ChatColor.GREEN + " on" : ChatColor.RED + " off"));
            Prohibition.getInstance().getLogger().info(name + (enabled ? " on" : " off"));
            return true;
        }
        return false;
    }

    public void activate() {
        if (enabled) {
            HandlerList.unregisterAll(this);
        } else {
            Bukkit.getPluginManager().registerEvents(this, Prohibition.getInstance());
        }
        enabled = !enabled;
    }
}
