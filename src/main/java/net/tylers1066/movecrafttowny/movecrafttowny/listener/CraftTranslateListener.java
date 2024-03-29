package net.tylers1066.movecrafttowny.movecrafttowny.listener;

import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyWorld;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import net.tylers1066.movecrafttowny.movecrafttowny.config.Config;
import net.tylers1066.movecrafttowny.movecrafttowny.localisation.I18nSupport;
import net.tylers1066.movecrafttowny.movecrafttowny.utils.TownyUtils;
import net.tylers1066.movecrafttowny.movecrafttowny.utils.TownyWorldHeightLimits;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class CraftTranslateListener implements Listener {
    @EventHandler
    public void onCraftTranslate(CraftTranslateEvent event) {
        final Set<TownBlock> townBlocks = new HashSet<>();
        final Craft craft = event.getCraft();
        final TownyWorld townyWorld = TownyUtils.getTownyWorld(craft.getWorld());
        if (!Config.TownyBlockMoveOnSwitchPerm)
            return;

        if (!(craft instanceof PilotedCraft))
            return;

        final Player pilot = ((PilotedCraft) craft).getPilot();
        for (MovecraftLocation ml : event.getNewHitBox()) {
            Town town;
            try {
                TownBlock townBlock = TownyUtils.getTownBlock(ml.toBukkit(event.getCraft().getWorld()));
                if (townBlock == null || townBlocks.contains(townBlock)) {
                    continue;
                }
                if (TownyUtils.validateCraftMoveEvent(pilot, ml.toBukkit(craft.getWorld()), townyWorld)) {
                    townBlocks.add(townBlock);
                    continue;
                }
                town = TownyUtils.getTown(townBlock);
                if (town == null)
                    continue;
                final TownyWorldHeightLimits whLim = TownyUtils.getWorldLimits(event.getCraft().getWorld());
                final Location spawnLoc = TownyUtils.getTownSpawn(townBlock);
                if (whLim.validate(ml.getY(), spawnLoc.getBlockY())) {
                    continue;
                }
            } catch (NullPointerException ignored) {
                continue;
            }
            event.setFailMessage(String.format("%s %s @ %d,%d,%d",
                    I18nSupport.getInternationalisedString("Towny - Translation Failed"), town.getName(), ml.getX(),
                    ml.getY(), ml.getZ()));
            event.setCancelled(true);
            break;
        }
    }
}
