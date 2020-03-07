package de.papiertuch.teamspeakbot.bukkit.listeners;

import de.papiertuch.teamspeakbot.bukkit.TeamSpeakBot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerLoginListener implements Listener {

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (TeamSpeakBot.getInstance().getMySQL().getConnection() == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.prefix") + " §cThere is no mysql connection. Please check your mysql data");
            return;
        }
        if (!TeamSpeakBot.getInstance().getTs3Query().isConnected()) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.prefix") + " §cThere is no connection the teamSpeak Server. Please check your query data");
            return;
        }
        Player player = event.getPlayer();
        if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.vpn.enable")) {
            if (Bukkit.getPluginManager().getPlugin("BanSystem-Bukkit") == null) {
                if (TeamSpeakBot.getInstance().hasVPN(player.getAddress().getHostString())) {
                    player.kickPlayer(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.kickReason"));
                    return;
                }
            }
        }
        if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.enable")) {
            if (TeamSpeakBot.getInstance().getVerifyHandler().isExists(player.getUniqueId())) {
                int rank = TeamSpeakBot.getInstance().getVerifyHandler().getCurrentRank(player);
                if (TeamSpeakBot.getInstance().getVerifyHandler().getRank(player.getUniqueId()) != rank) {
                    Bukkit.getScheduler().runTaskLater(TeamSpeakBot.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            if (TeamSpeakBot.getInstance().getTs3ApiAsync().isClientOnline(TeamSpeakBot.getInstance().getVerifyHandler().getTeamSpeakId(player.getUniqueId())).getUninterruptibly()) {
                                TeamSpeakBot.getInstance().getVerifyHandler().setClientGroups(player.getUniqueId());
                            } else {
                                TeamSpeakBot.getInstance().getVerifyHandler().setNewRank(player.getUniqueId(), rank);
                            }

                        }
                    }, 20);
                }
            }
        }
    }
}
