package de.papiertuch.teamspeakbot.proxy.listeners;

import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerDisconnectListener implements Listener {

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (TeamSpeakBot.getInstance().getVerifyHandler().getRequest().containsKey((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
            TeamSpeakBot.getInstance().getVerifyHandler().delete((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
            TeamSpeakBot.getInstance().getVerifyHandler().getRequest().remove((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
        }
    }
}
