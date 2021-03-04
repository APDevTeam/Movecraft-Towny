package net.tylers1066.movecrafttowny.movecrafttowny;

import net.tylers1066.movecrafttowny.movecrafttowny.config.Config;
import net.tylers1066.movecrafttowny.movecrafttowny.localisation.I18nSupport;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MovecraftTowny extends JavaPlugin {
    private static MovecraftTowny instance;

    public static MovecraftTowny getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        // TODO other languages
        String[] languages = {"en"};
        for (String s : languages) {
            if (!new File(getDataFolder()  + "/localisation/mc-cannonslang_"+ s +".properties").exists()) {
                this.saveResource("localisation/mc-cannonslang_"+ s +".properties", false);
            }
        }
        Config.Locale = getConfig().getString("Locale", "en");
        I18nSupport.init();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
