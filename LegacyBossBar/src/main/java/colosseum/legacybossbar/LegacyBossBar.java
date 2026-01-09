package colosseum.legacybossbar;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class LegacyBossBar extends JavaPlugin {

    @Getter
    private static LegacyBossBar instance;

    @Getter
    private WitherManager witherManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        witherManager = new WitherManager();
        witherManager.activate();
        getCommand("legacybossbar").setExecutor(new LegacyBossBarCommand());
    }

    @Override
    public void onDisable() {
        if (witherManager != null) {
            witherManager.deactivate();
        }
        witherManager = null;
    }
}
