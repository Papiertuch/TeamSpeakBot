package de.papiertuch.teamspeakbot.bukkit.listeners;

import de.papiertuch.teamspeakbot.bukkit.TeamSpeakBot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (TeamSpeakBot.getInstance().getVerifyHandler().getRequest().containsKey(player.getUniqueId())) {
            TeamSpeakBot.getInstance().getVerifyHandler().delete(player.getUniqueId());
            TeamSpeakBot.getInstance().getVerifyHandler().getRequest().remove(player.getUniqueId());
        }
    }
}
