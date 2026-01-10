package colosseum.legacybossbar;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class LegacyBossBar extends JavaPlugin {

    @Getter
    private static LegacyBossBar instance;

    @Getter
    private FakeEntityManager fakeEntityManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        fakeEntityManager = new FakeEntityManager();
        fakeEntityManager.activate();
    }

    @Override
    public void onDisable() {
        if (fakeEntityManager != null) {
            fakeEntityManager.deactivate();
        }
        fakeEntityManager = null;
    }
}
