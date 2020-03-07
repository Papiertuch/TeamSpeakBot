package de.papiertuch.teamspeakbot.proxy.listeners;

import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PostLoginListener implements Listener {

    @EventHandler(priority = 64)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.vpn.enable")) {
            if (BungeeCord.getInstance().getPluginManager().getPlugin("BanSystem-Proxy") == null) {
                if (TeamSpeakBot.getInstance().hasVPN(player.getAddress().getHostString())) {
                    player.disconnect(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.kickReason"));
                    return;
                }
            }
        }
        if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.enable")) {
            if (TeamSpeakBot.getInstance().getVerifyHandler().isExists(player.getUniqueId())) {
                int rank = TeamSpeakBot.getInstance().getVerifyHandler().getCurrentRank(player);
                if (TeamSpeakBot.getInstance().getVerifyHandler().getRank(player.getUniqueId()) != rank) {
                    ProxyServer.getInstance().getScheduler().schedule(TeamSpeakBot.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            if (TeamSpeakBot.getInstance().getTs3ApiAsync().isClientOnline(TeamSpeakBot.getInstance().getVerifyHandler().getTeamSpeakId(player.getUniqueId())).getUninterruptibly()) {
                                TeamSpeakBot.getInstance().getVerifyHandler().setClientGroups(player.getUniqueId());
                            } else {
                                TeamSpeakBot.getInstance().getVerifyHandler().setNewRank(player.getUniqueId(), rank);
                            }

                        }
                    }, 1, TimeUnit.SECONDS);
                }
            }
        }
    }
}
