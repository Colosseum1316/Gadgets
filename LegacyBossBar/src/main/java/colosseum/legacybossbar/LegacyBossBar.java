package colosseum.legacybossbar;

import org.bukkit.plugin.java.JavaPlugin;

public final class LegacyBossBar extends JavaPlugin {

    private static LegacyBossBar instance;

    public static JavaPlugin getInstance() {
        return instance;
    }

    private WitherManager witherManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        witherManager = new WitherManager();
        witherManager.activate();
    }

    @Override
    public void onDisable() {
        if (witherManager != null) {
            witherManager.deactivate();
        }
        witherManager = null;
    }
}
