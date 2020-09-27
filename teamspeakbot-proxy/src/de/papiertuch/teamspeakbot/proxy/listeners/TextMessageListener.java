package de.papiertuch.teamspeakbot.proxy.listeners;

import com.github.theholywaffle.teamspeak3.api.TextMessageTargetMode;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class TextMessageListener {

    public void register() {
        TeamSpeakBot.getInstance().getTs3ApiAsync().registerEvent(TS3EventType.TEXT_PRIVATE, 0);
        TeamSpeakBot.getInstance().getTs3ApiAsync().registerEvent(TS3EventType.SERVER, 0);
        TeamSpeakBot.getInstance().getTs3ApiAsync().addTS3Listeners(new TS3EventAdapter() {
            @Override
            public void onTextMessage(TextMessageEvent event) {
                if (event.getTargetMode() == TextMessageTargetMode.CLIENT && event.getInvokerId() != TeamSpeakBot.getInstance().getTs3ApiAsync().whoAmI().getUninterruptibly().getId()) {
                    ClientInfo clientInfo = TeamSpeakBot.getInstance().getTs3ApiAsync().getClientByUId(event.getInvokerUniqueId()).getUninterruptibly();
                    if (event.getMessage().startsWith("!" + TeamSpeakBot.getInstance().getConfigHandler().getString("module.verify.command"))) {
                        if (TeamSpeakBot.getInstance().getVerifyHandler().isExists(event.getInvokerUniqueId()) && TeamSpeakBot.getInstance().getVerifyHandler().isVerify(event.getInvokerUniqueId())) {
                            TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(clientInfo.getId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.alreadyVerify"));
                            return;
                        }
                        String[] args = event.getMessage().split(" ");
                        if (args.length == 2) {
                            String name = args[1];
                            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(name);
                            if (player == null) {
                                TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(clientInfo.getId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.notOnline"));
                                return;
                            }
                            if (TeamSpeakBot.getInstance().getVerifyHandler().isVerify((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()))) {
                                TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(clientInfo.getId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.alreadyVerify"));
                                return;
                            }
                            TeamSpeakBot.getInstance().getVerifyHandler().getRequest().put((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()), clientInfo);
                            TeamSpeakBot.getInstance().getVerifyHandler().create((TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.useUuids") ? player.getUniqueId().toString() : player.getName()), event.getInvokerUniqueId());
                            TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(clientInfo.getId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.request").replace("%client%", name));
                            player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.request").replace("%client%", clientInfo.getNickname()));
                            sendRequest(player);
                        } else {
                            TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(clientInfo.getId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.syntax"));
                        }
                    } else {
                        TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(clientInfo.getId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.syntax"));
                    }
                }
            }
        });
    }

    private void sendRequest(ProxiedPlayer player) {
        TextComponent accept = new TextComponent(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.hoverAccept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.hoverAccept")).create()));
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + TeamSpeakBot.getInstance().getConfigHandler().getString("module.verify.command") + " accept"));

        TextComponent deny = new TextComponent(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.hoverDeny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.hoverDeny")).create()));
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/" + TeamSpeakBot.getInstance().getConfigHandler().getString("module.verify.command") + " deny"));

        TextComponent txt = new TextComponent(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.requestMiddleText"));
        TextComponent msg = new TextComponent(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.requestTest"));
        msg.addExtra(accept);
        msg.addExtra(txt);
        msg.addExtra(deny);
        player.sendMessage(msg);
    }
}
