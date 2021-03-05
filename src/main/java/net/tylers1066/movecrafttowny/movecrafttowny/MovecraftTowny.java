package net.tylers1066.movecrafttowny.movecrafttowny;

import com.palmergames.bukkit.towny.Towny;
import net.tylers1066.movecrafttowny.movecrafttowny.config.Config;
import net.tylers1066.movecrafttowny.movecrafttowny.listener.CraftDetectListener;
import net.tylers1066.movecrafttowny.movecrafttowny.listener.CraftRotateListener;
import net.tylers1066.movecrafttowny.movecrafttowny.listener.CraftSinkListener;
import net.tylers1066.movecrafttowny.movecrafttowny.listener.CraftTranslateListener;
import net.tylers1066.movecrafttowny.movecrafttowny.localisation.I18nSupport;
import net.tylers1066.movecrafttowny.movecrafttowny.utils.TownyUtils;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.logging.Level;

public final class MovecraftTowny extends JavaPlugin {
    private static MovecraftTowny instance;
    private static Towny townyPlugin = null;

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
            if (!new File(getDataFolder()  + "/localisation/movecrafttownylang_"+ s +".properties").exists()) {
                this.saveResource("localisation/movecrafttownylang_"+ s +".properties", false);
            }
        }
        Config.Locale = getConfig().getString("Locale", "en");
        I18nSupport.init();

        Plugin temp = getServer().getPluginManager().getPlugin("Towny");
        if(temp == null || !(temp instanceof Towny)) {
            getLogger().log(Level.SEVERE, I18nSupport.getInternationalisedString("Startup - Towny Not Found"));
            return;
        }

        getLogger().log(Level.INFO, I18nSupport.getInternationalisedString("Startup - Towny Found"));
        townyPlugin = (Towny) temp;

        TownyUtils.initTownyConfig();

        Config.TownyBlockMoveOnSwitchPerm = getConfig().getBoolean("TownyBlockMoveOnSwitchPerm", true);
        Config.TownyBlockSinkOnNoPVP = getConfig().getBoolean("TownyBlockSinkOnNoPVP", false);
        getLogger().log(Level.INFO, "Settings: TownyBlockMoveOnSwitchPerm - {0}", Config.TownyBlockMoveOnSwitchPerm);
        getLogger().log(Level.INFO, "Settings: TownyBlockSinkOnNoPVP - {0}", Config.TownyBlockSinkOnNoPVP);

        getServer().getPluginManager().registerEvents(new CraftDetectListener(), this);
        getServer().getPluginManager().registerEvents(new CraftRotateListener(), this);
        getServer().getPluginManager().registerEvents(new CraftSinkListener(), this);
        getServer().getPluginManager().registerEvents(new CraftTranslateListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @NotNull
    public Towny getTownyPlugin() {
        return townyPlugin;
    }
}
