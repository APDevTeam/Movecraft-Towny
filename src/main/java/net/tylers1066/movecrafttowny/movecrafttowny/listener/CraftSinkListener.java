package net.tylers1066.movecrafttowny.movecrafttowny.listener;

import com.palmergames.bukkit.towny.object.TownBlock;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftSinkEvent;
import net.countercraft.movecraft.utils.HitBox;
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

        for (MovecraftLocation ml : hitBox) {
            TownBlock townBlock = TownyUtils.getTownBlock(ml.toBukkit(event.getCraft().getW()));
            if (townBlock == null || townBlocks.contains(townBlock)) {
                continue;
            }
            if (TownyUtils.validatePVP(townBlock)) {
                townBlocks.add(townBlock);
                continue;
            }
            final Player notifyP = craft.getNotificationPlayer();
            if (notifyP != null) {
                notifyP.sendMessage(String.format(I18nSupport.getInternationalisedString("Towny - Sinking a craft is not allowed in this town plot") + " @ %d,%d,%d", ml.getX(), ml.getY(), ml.getZ()));
            }
            event.setCancelled(true);
            break;
        }
    }
}
