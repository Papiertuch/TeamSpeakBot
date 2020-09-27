package de.papiertuch.teamspeakbot.proxy.commands;

import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import de.papiertuch.teamspeakbot.proxy.utils.VerifyHandler;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class VerifyCommand extends Command {

    private HashMap<String, Long> coolDown = new HashMap<>();

    public VerifyCommand() {
        super(TeamSpeakBot.getInstance().getConfigHandler().getString("module.verify.command"));
    }

    public void execute(CommandSender commandSender, String[] args) {
        if (!(commandSender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)commandSender;
        if (args.length == 0) {
            if (!TeamSpeakBot.getInstance().getVerifyHandler().isVerify((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
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
                if (TeamSpeakBot.getInstance().getVerifyHandler().getRequest().containsKey((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
                    VerifyHandler verifyHandler = TeamSpeakBot.getInstance().getVerifyHandler();
                    verifyHandler.setVerify((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()), true);
                    verifyHandler.setClientGroups((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
                    verifyHandler.getRequest().remove((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.noRequest"));
                }
                return;
            }
            if (args[0].equalsIgnoreCase("deny")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().getRequest().containsKey((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
                    TeamSpeakBot.getInstance().getVerifyHandler().delete((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
                    TeamSpeakBot.getInstance().getVerifyHandler().getRequest().remove((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.deny"));
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.noRequest"));
                }
                return;
            }
            if (args[0].equalsIgnoreCase("update")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().isVerify((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
                    if (coolDown.containsKey((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
                        if (coolDown.get((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName())) >= System.currentTimeMillis()) {
                            player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.waiting"));
                            return;
                        }
                    }
                    coolDown.put((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()), (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(3)));
                    TeamSpeakBot.getInstance().getVerifyHandler().setClientGroups((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
                } else {
                    player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.notVerify"));
                }
                return;
            }
            if (args[0].equalsIgnoreCase("delete")) {
                if (TeamSpeakBot.getInstance().getVerifyHandler().isVerify((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
                    TeamSpeakBot.getInstance().getVerifyHandler().delete((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()));
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
