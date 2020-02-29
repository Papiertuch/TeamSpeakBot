package de.papiertuch.teamspeakbot.proxy.listeners;

import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class LoginListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        if (TeamSpeakBot.getInstance().getMySQL().getConnection() == null) {
            event.setCancelled(true);
            event.setCancelReason(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.prefix") + " §cThere is no mysql connection. Please check your mysql data");
            return;
        }
        if (!TeamSpeakBot.getInstance().getTs3Query().isConnected()) {
            event.setCancelled(true);
            event.setCancelReason(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.prefix") + " §cThere is no connection the teamSpeak Server. Please check your query data");
            return;
        }
    }
}
