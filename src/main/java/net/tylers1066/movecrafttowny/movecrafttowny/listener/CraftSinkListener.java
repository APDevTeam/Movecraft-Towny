package net.tylers1066.movecrafttowny.movecrafttowny.listener;

import com.palmergames.bukkit.towny.object.TownBlock;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.PilotedCraft;
import net.countercraft.movecraft.events.CraftSinkEvent;
import net.countercraft.movecraft.util.hitboxes.HitBox;
import net.tylers1066.movecrafttowny.movecrafttowny.config.Config;
import net.tylers1066.movecrafttowny.movecrafttowny.localisation.I18nSupport;
import net.tylers1066.movecrafttowny.movecrafttowny.utils.TownyUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class CraftSinkListener implements Listener {
    @EventHandler
    public void onCraftSink(CraftSinkEvent event) {
        final Set<TownBlock> townBlocks = new HashSet<>();
        final Craft craft = event.getCraft();
        if (!Config.TownyBlockSinkOnNoPVP)
            return;

        final HitBox hitBox = craft.getHitBox();
        if (hitBox.isEmpty())
            return;

        Player pilot = null;
        if (craft instanceof PilotedCraft) {
            pilot = ((PilotedCraft) craft).getPilot();
        }

        for (MovecraftLocation ml : hitBox) {
            TownBlock townBlock = TownyUtils.getTownBlock(ml.toBukkit(event.getCraft().getWorld()));
            if (townBlock == null || townBlocks.contains(townBlock)) {
                continue;
            }
            if (TownyUtils.validatePVP(townBlock)) {
                townBlocks.add(townBlock);
                continue;
            }
            if (pilot != null) {
                pilot.sendMessage(String.format("%s @ %d,%d,%d",
                        I18nSupport
                                .getInternationalisedString("Towny - Sinking a craft is not allowed in this town plot"),
                        ml.getX(), ml.getY(), ml.getZ()));
            }
            event.setCancelled(true);
            break;
        }
    }
}
