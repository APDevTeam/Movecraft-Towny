package net.tylers1066.movecrafttowny.movecrafttowny.utils;

import com.palmergames.bukkit.towny.TownyUniverse;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.exceptions.TownyException;
import com.palmergames.bukkit.towny.object.Coord;
import com.palmergames.bukkit.towny.object.PlayerCache;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.object.TownyWorld;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import com.palmergames.bukkit.towny.war.common.WarZoneConfig;
import net.tylers1066.movecrafttowny.movecrafttowny.MovecraftTowny;
import net.tylers1066.movecrafttowny.movecrafttowny.config.Config;
import net.tylers1066.movecrafttowny.movecrafttowny.localisation.I18nSupport;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

public class TownyUtils {
    public static final String TOWN_MIN = "worldMin";
    public static final String TOWN_MAX = "worldMax";
    public static final String TOWN_ABOVE = "aboveTownSpawn";
    public static final String TOWN_UNDER = "underTownSpawn";
    public static final String TOWN_HEIGHT_LIMITS = "TownyWorldHeightLimits";

    @Nullable
    public static TownyWorld getTownyWorld(World w) {
        TownyWorld tw;
        try {
            tw = TownyUniverse.getInstance().getDataSource().getWorld(w.getName());
            if(!tw.isUsingTowny())
                return null;
        }
        catch (NotRegisteredException e) {
            return null;
        }
        return tw;
    }

    @Nullable
    public static TownBlock getTownBlock(Location loc) {
        Coord coo = Coord.parseCoord(loc);
        TownyWorld tw = getTownyWorld(loc.getWorld());
        TownBlock tb = null;
        try {
            if(tw != null)
                tb = tw.getTownBlock(coo);
        }
        catch (NotRegisteredException e) {
            return null;
        }
        return tb;
    }

    @Nullable
    public static Town getTown(TownBlock townBlock) {
        try {
            Town town;
            town = townBlock.getTown();
            return town;
        }
        catch (TownyException e) {
            return null;
        }
    }

    @Nullable
    public static Location getTownSpawn(@Nullable TownBlock townBlock) {
        if(townBlock == null)
            return null;

        Town t = getTown(townBlock);
        if(t == null)
            return null;

        try {
            return t.getSpawn();
        }
        catch (TownyException e) {
            MovecraftTowny.getInstance().getLogger().log(Level.SEVERE, String.format(I18nSupport.getInternationalisedString("Towny - Spawn Not Found"), t.getName()), e);
            return null;
        }
    }

    public static boolean validateResident(Player player) {
        try {
            Resident resident = TownyUniverse.getInstance().getDataSource().getResident(player.getName());
            return true;
        }
        catch (TownyException e) {
            return false;
        }
    }

    public static boolean validateCraftMoveEvent(Player player, Location loc, TownyWorld world) {
        if(player != null && !validateResident(player))
            return true;

        boolean bSwitch = PlayerCacheUtil.getCachePermission(player, loc, Material.STONE_BUTTON, TownyPermission.ActionType.SWITCH);

        if(bSwitch)
            return true;

        PlayerCache playerCache = MovecraftTowny.getInstance().getTownyPlugin().getCache(player);
        PlayerCache.TownBlockStatus status = playerCache.getStatus();

        return !playerCache.hasBlockErrMsg() && status == PlayerCache.TownBlockStatus.WARZONE && WarZoneConfig.isAllowingSwitchesInWarZone();
    }

    public static boolean validatePVP(TownBlock tb) {
        Town t = getTown(tb);
        if(t == null)
            return tb.getPermissions().pvp;
        else
            return t.getPermissions().pvp || tb.getPermissions().pvp;
    }

    public static boolean validateExplosion(TownBlock tb) {
        Town t = getTown(tb);
        if(t == null)
            return tb.getPermissions().explosion;
        else
            return tb.getPermissions().explosion || t.getPermissions().explosion;
    }

    public static void initTownyConfig() {
        Config.TownProtectionHeightLimits = getTownyConfigFromUniverse();
        loadTownyConfig();
    }

    private static Map<String, TownyWorldHeightLimits> getTownyConfigFromUniverse() {
        Map<String, TownyWorldHeightLimits> worldsMap = new HashMap<>();
        List<World> worlds = MovecraftTowny.getInstance().getServer().getWorlds();
        for (World w : worlds) {
            TownyWorld tw = getTownyWorld(w);
            if (tw != null) {
                if (tw.isUsingTowny()) {
                    worldsMap.put(w.getName(), new TownyWorldHeightLimits());
                }
            }
        }
        return worldsMap;
    }

    public static TownyWorldHeightLimits getWorldLimits(World w) {
        boolean oNew = false;
        String wName = w.getName();
        Map<String, TownyWorldHeightLimits> worldsMap = Config.TownProtectionHeightLimits;
        if (!worldsMap.containsKey(wName)) {
            TownyWorld tw = getTownyWorld(w);
            if (tw != null) {
                if (tw.isUsingTowny()) {
                    worldsMap.put(wName, new TownyWorldHeightLimits());
                    Config.TownProtectionHeightLimits = worldsMap;
                    MovecraftTowny.getInstance().getLogger().log(Level.INFO, I18nSupport.getInternationalisedString("Towny - Added Config Defaults"), w.getName());
                    oNew = true;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        }
        if (oNew) {
            saveWorldLimits();
        }
        return worldsMap.get(wName);
    }

    private static void saveWorldLimits() {
        Map<String, TownyWorldHeightLimits> worldsMap = Config.TownProtectionHeightLimits;
        Map<String, Object> townyWorldsMap = new HashMap<>();
        Set<String> worlds = worldsMap.keySet();

        for (String world : worlds) {
            World w = MovecraftTowny.getInstance().getServer().getWorld(world);
            if (w != null) {
                TownyWorld tw = getTownyWorld(w);
                if (tw != null) {
                    if (tw.isUsingTowny()) {
                        Map<String, Integer> townyTWorldMap = new HashMap<>();
                        TownyWorldHeightLimits twhl = worldsMap.get(world);
                        townyTWorldMap.put(TOWN_MIN, twhl.world_min);
                        townyTWorldMap.put(TOWN_MAX, twhl.world_max);
                        townyTWorldMap.put(TOWN_ABOVE, twhl.above_town);
                        townyTWorldMap.put(TOWN_UNDER, twhl.under_town);
                        townyWorldsMap.put(world, townyTWorldMap);
                        MovecraftTowny.getInstance().getConfig().set(TOWN_HEIGHT_LIMITS + "." + world, townyTWorldMap);
                    }
                }
            }
        }
        MovecraftTowny.getInstance().saveConfig();
        MovecraftTowny.getInstance().getLogger().log(Level.INFO, I18nSupport.getInternationalisedString("Towny - Saved Settings"));
    }

    private static void loadTownyConfig() {
        FileConfiguration fc = MovecraftTowny.getInstance().getConfig();
        ConfigurationSection csObj = fc.getConfigurationSection(TOWN_HEIGHT_LIMITS);
        Map<String, TownyWorldHeightLimits> townyWorldHeightLimits = new HashMap<>();

        if (csObj != null) {
            Set<String> worlds = csObj.getKeys(false);
            for (String worldName : worlds) {
                TownyWorldHeightLimits twhl = new TownyWorldHeightLimits();
                twhl.world_min = fc.getInt(TOWN_HEIGHT_LIMITS + "." + worldName + "." + TOWN_MIN, TownyWorldHeightLimits.DEFAULT_WORLD_MIN);
                twhl.world_max = fc.getInt(TOWN_HEIGHT_LIMITS + "." + worldName + "." + TOWN_MAX, TownyWorldHeightLimits.DEFAULT_WORLD_MAX);
                twhl.above_town = fc.getInt(TOWN_HEIGHT_LIMITS + "." + worldName + "." + TOWN_ABOVE, TownyWorldHeightLimits.DEFAULT_TOWN_ABOVE);
                twhl.under_town = fc.getInt(TOWN_HEIGHT_LIMITS + "." + worldName + "." + TOWN_UNDER, TownyWorldHeightLimits.DEFAULT_TOWN_UNDER);
                townyWorldHeightLimits.put(worldName, twhl);
                MovecraftTowny.getInstance().getLogger().log(Level.INFO, I18nSupport.getInternationalisedString("Towny - Loaded Settings"), worldName);
            }
        }
        if (!townyWorldHeightLimits.equals(Config.TownProtectionHeightLimits)) {
            Config.TownProtectionHeightLimits.putAll(townyWorldHeightLimits);
            saveWorldLimits();
        }
    }
}
