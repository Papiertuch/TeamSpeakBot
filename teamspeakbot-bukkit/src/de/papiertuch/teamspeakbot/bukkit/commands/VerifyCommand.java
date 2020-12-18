package de.papiertuch.teamspeakbot.bukkit.commands;

import de.papiertuch.teamspeakbot.bukkit.TeamSpeakBot;
import de.papiertuch.teamspeakbot.bukkit.utils.VerifyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class VerifyCommand extends BukkitCommand {

    public VerifyCommand() {
        super(TeamSpeakBot.getInstance().getConfigHandler().getString("module.verify.command"));
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
        Player player = (Player) commandSender;
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
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("accept")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().getRequest().containsKey(player.getUniqueId())) {
                    VerifyHandler verifyHandler = TeamSpeakBot.getInstance().getVerifyHandler();
                    verifyHandler.setVerify(player.getUniqueId(), true);
                    verifyHandler.setClientGroups(player.getUniqueId());
                    verifyHandler.getRequest().remove(player.getUniqueId());
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.noRequest"));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("deny")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().getRequest().containsKey(player.getUniqueId())) {
                    TeamSpeakBot.getInstance().getVerifyHandler().delete(player.getUniqueId());
                    TeamSpeakBot.getInstance().getVerifyHandler().getRequest().remove(player.getUniqueId());
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.deny"));
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.noRequest"));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("update")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().isVerify(player.getUniqueId())) {
                    TeamSpeakBot.getInstance().getVerifyHandler().setClientGroups(player.getUniqueId());
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.notVerify"));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("delete")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().isVerify(player.getUniqueId())) {
                    TeamSpeakBot.getInstance().getVerifyHandler().delete(player.getUniqueId());
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.delete"));
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.notVerify"));
                }
                return true;
            }
            player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.syntax"));
        } else {
            player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.syntax"));
        }
        return false;
    }
}
