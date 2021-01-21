package de.papiertuch.teamspeakbot.proxy.listeners;

import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import de.papiertuch.teamspeakbot.proxy.utils.VerifyHandler;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;


public class PostLoginListener implements Listener {


    @EventHandler
    public void onQuit(PlayerDisconnectEvent event) {
        ProxiedPlayer player = event.getPlayer();
        VerifyHandler verifyHandler = TeamSpeakBot.getInstance().getVerifyHandler();
        if (verifyHandler.getRequest().containsKey((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
            verifyHandler.getRequest().remove((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
        }
    }

    @EventHandler(priority = 64)
    public void onPostLogin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (TeamSpeakBot.getInstance().getMySQL().getConnection() == null) {
            if (player.hasPermission("update.notify")) {
                player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.prefix") + " §cThere is no mysql connection. Please check your mysql data");
            }
            return;
        }
        if (!TeamSpeakBot.getInstance().getTs3Query().isConnected()) {
            if (player.hasPermission("update.notify")) {
                player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.prefix") + " §cThere is no connection the teamSpeak Server. Please check your query data");
            }
            return;
        }
        if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.vpn.enable")) {
            if (BungeeCord.getInstance().getPluginManager().getPlugin("BanSystem-Proxy") == null) {
                if (TeamSpeakBot.getInstance().hasVPN(player.getAddress().getHostString())) {
                    player.disconnect(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.kickReason"));
                    return;
                }
            }
        }
        ProxyServer.getInstance().getScheduler().schedule(TeamSpeakBot.getInstance(), () -> {
            if (player.hasPermission("update.notify")) {
                if (TeamSpeakBot.getInstance().getNewVersion() != null && !TeamSpeakBot.getInstance().getNewVersion().equalsIgnoreCase(TeamSpeakBot.getInstance().getDescription().getVersion())) {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getPrefix() + " §aA new version is available §8» §f§l" + TeamSpeakBot.getInstance().getNewVersion());
                    player.sendMessage("§ehttps://www.spigotmc.org/resources/einbot-teamspeak-verification-and-support-notify.48188/");
                }
            }
        }, 1, TimeUnit.SECONDS);

        if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.enable")) {
            if (TeamSpeakBot.getInstance().getVerifyHandler().isExists((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
                int rank = TeamSpeakBot.getInstance().getVerifyHandler().getCurrentRank(player);
                if (TeamSpeakBot.getInstance().getVerifyHandler().getRank((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName())) != rank) {
                    ProxyServer.getInstance().getScheduler().schedule(TeamSpeakBot.getInstance(), () -> {
                        if (TeamSpeakBot.getInstance().getTs3ApiAsync().isClientOnline(TeamSpeakBot.getInstance().getVerifyHandler().getTeamSpeakId((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))).getUninterruptibly()) {
                            TeamSpeakBot.getInstance().getVerifyHandler().setClientGroups((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
                        } else {
                            TeamSpeakBot.getInstance().getVerifyHandler().setNewRank((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()), rank);
                        }

                    }, 1, TimeUnit.SECONDS);
                }
            }
        }
    }
}
