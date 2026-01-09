package colosseum.legacybossbar;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;

public final class LegacyBossBarCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof ConsoleCommandSender) && !(sender instanceof RemoteConsoleCommandSender)) {
            return false;
        }
        if (args.length != 1) {
            return false;
        }
        int n = Integer.parseInt(args[0]);
        LegacyBossBar.getInstance().getWitherManager().setOffset(n);
        sender.sendMessage("Set new offset " + n);
        return true;
    }
}
