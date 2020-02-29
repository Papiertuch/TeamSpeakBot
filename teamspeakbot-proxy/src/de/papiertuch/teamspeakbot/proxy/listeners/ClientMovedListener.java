package de.papiertuch.teamspeakbot.proxy.listeners;

import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class ClientMovedListener {

    public void register() {
        TeamSpeakBot.getInstance().getTs3ApiAsync().registerEvent(TS3EventType.CHANNEL, 0);
        TeamSpeakBot.getInstance().getTs3ApiAsync().addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onClientMoved(ClientMovedEvent event) {
                if (TeamSpeakBot.getInstance().getConfigHandler().getSupportChannelList().contains(String.valueOf(event.getTargetChannelId()))) {
                    String name = TeamSpeakBot.getInstance().getTs3ApiAsync().getClientInfo(event.getClientId()).getUninterruptibly().getNickname();
                    String channel = TeamSpeakBot.getInstance().getTs3ApiAsync().getChannelInfo(event.getTargetChannelId()).getUninterruptibly().getName();
                    String messageTeamSpeak = TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.supportNotify")
                            .replace("%client%", name)
                            .replace("%channel%", channel);
                    TeamSpeakBot.getInstance().getTs3ApiAsync().getClients().onSuccess(clients -> clients.forEach(client -> {
                        for (int i = 0; i < TeamSpeakBot.getInstance().getConfigHandler().getSupportList().size(); i++) {
                            if (client.isInServerGroup(Integer.valueOf(TeamSpeakBot.getInstance().getConfigHandler().getSupportList().get(i)))) {
                                if (TeamSpeakBot.getInstance().getConfigHandler().getString("module.support.message").equalsIgnoreCase("Poke")) {
                                    TeamSpeakBot.getInstance().getTs3ApiAsync().pokeClient(client.getId(), messageTeamSpeak);
                                } else {
                                    TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(client.getId(), messageTeamSpeak);
                                }
                            }
                        }
                    }));
                    for (ProxiedPlayer a : ProxyServer.getInstance().getPlayers()) {
                        if (a.hasPermission(TeamSpeakBot.getInstance().getConfigHandler().getString("module.support.perms"))) {
                            a.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.supportNotify")
                                    .replace("%client%", name)
                                    .replace("%channel%", channel));
                        }
                    }
                    TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(event.getClientId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.supportJoin"));
                }
            }
        });
    }
}
