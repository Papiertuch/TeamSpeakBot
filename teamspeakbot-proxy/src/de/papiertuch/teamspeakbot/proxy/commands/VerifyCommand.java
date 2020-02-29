package de.papiertuch.teamspeakbot.proxy.commands;

import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import de.papiertuch.teamspeakbot.proxy.utils.VerifyHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class VerifyCommand extends Command {

    public VerifyCommand() {
        super("verify");
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)commandSender;
        if (args.length == 0) {
            if (!TeamSpeakBot.getInstance().getVerifyHandler().isVerify(player.getUniqueId())) {
                TeamSpeakBot.getInstance().getTs3ApiAsync().getClients().onSuccess(clients -> clients.forEach(client -> {
                    if (!TeamSpeakBot.getInstance().getVerifyHandler().isVerify(client.getUniqueIdentifier()) && player.getAddress().getAddress().getHostAddress().equalsIgnoreCase(client.getIp())) {
                        TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(client.getId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.info"));
                    }
                }));
                player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.message"));
            } else {
                player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.syntax"));
            }
        }
        else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().getRequest().containsKey(player.getUniqueId())) {
                    VerifyHandler verifyHandler = TeamSpeakBot.getInstance().getVerifyHandler();
                    verifyHandler.setVerify(player.getUniqueId(), true);
                    verifyHandler.setClientGroups(player.getUniqueId());
                    verifyHandler.getRequest().remove(player.getUniqueId());
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.noRequest"));
                }
                return;
            }
            if (args[0].equalsIgnoreCase("deny")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().getRequest().containsKey(player.getUniqueId())) {
                    TeamSpeakBot.getInstance().getVerifyHandler().delete(player.getUniqueId());
                    TeamSpeakBot.getInstance().getVerifyHandler().getRequest().remove(player.getUniqueId());
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.deny"));
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.noRequest"));
                }
                return;
            }
            if (args[0].equalsIgnoreCase("update")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().isVerify(player.getUniqueId())) {
                    TeamSpeakBot.getInstance().getVerifyHandler().setClientGroups(player.getUniqueId());
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.notVerify"));
                }
                return;
            }
            if (args[0].equalsIgnoreCase("delete")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().isVerify(player.getUniqueId())) {
                    TeamSpeakBot.getInstance().getVerifyHandler().delete(player.getUniqueId());
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.delete"));
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.notVerify"));
                }
                return;
            }
            player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.syntax"));
        } else {
            player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.syntax"));
        }
    }
}
