package de.papiertuch.teamspeakbot.proxy;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import de.papiertuch.teamspeakbot.proxy.commands.VerifyCommand;
import de.papiertuch.teamspeakbot.proxy.listeners.*;
import de.papiertuch.teamspeakbot.proxy.utils.ConfigHandler;
import de.papiertuch.teamspeakbot.proxy.utils.MySQL;
import de.papiertuch.teamspeakbot.proxy.utils.VerifyHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class TeamSpeakBot extends Plugin {

    private static TeamSpeakBot instance;
    private VerifyHandler verifyHandler;
    private MySQL mySQL;
    private TS3Config ts3Config;
    private TS3Query ts3Query;
    private TS3ApiAsync ts3ApiAsync;
    private ConfigHandler configHandler;
    private HashMap<String, Boolean> cacheAddress;

    @Override
    public void onEnable() {
        instance = this;
        System.out.print(" _______                    _____                  _    ____        _   ");
        System.out.print("|__   __|                  / ____|                | |  |  _ \\      | |  ");
        System.out.print("   | | ___  __ _ _ __ ___ | (___  _ __   ___  __ _| | _| |_) | ___ | |_ ");
        System.out.print("   | |/ _ \\/ _` | '_ ` _ \\ \\___ \\| '_ \\ / _ \\/ _` | |/ /  _ < / _ \\| __|");
        System.out.print("   | |  __/ (_| | | | | | |____) | |_) |  __/ (_| |   <| |_) | (_) | |_ ");
        System.out.print("   |_|\\___|\\__,_|_| |_| |_|_____/| .__/ \\___|\\__,_|_|\\_\\____/ \\___/ \\__|");
        System.out.print("                                 | |                                    ");
        System.out.print("                                 |_|                                    ");
        System.out.print("                               ");
        System.out.print("> TeamSpeakBot-Proxy - by Papiertuch | Discord: https://discord.gg/4T9jV9d");
        System.out.print("> Version: " + getDescription().getVersion());
        System.out.print("> Java: " + System.getProperty("java.version") + " System: " + System.getProperty("os.name"));
        System.out.print("   ");
        verifyHandler = new VerifyHandler();
        configHandler = new ConfigHandler();
        mySQL = new MySQL();
        ts3Config = new TS3Config();
        ts3Query = new TS3Query(ts3Config);
        cacheAddress = new HashMap<>();
        ts3ApiAsync = ts3Query.getAsyncApi();
        configHandler.loadConfig();

        ProxyServer.getInstance().getPluginManager().registerListener(this, new LoginListener());

        if (configHandler.getBoolean("module.verify.enable")) {
            mySQL.createTable();
        }
        ts3Config.setHost(configHandler.getString("query.host"));
        ts3Config.setQueryPort(configHandler.getInt("query.port"));
        ts3Config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
        ts3Config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
        try {
            ts3Query.connect();
            ts3ApiAsync.login(configHandler.getString("query.user"), configHandler.getString("query.password"));
            ts3ApiAsync.selectVirtualServerByPort(configHandler.getInt("teamSpeak.port"));
            ts3ApiAsync.setNickname(configHandler.getString("teamSpeak.botName"));

            if (configHandler.getBoolean("module.verify.enable")) {
                new TextMessageListener().register();
                register();
            }
            if (configHandler.getBoolean("module.support.enable")) {
                new ClientMovedListener().register();
            }
            new ClientJoinListener().register();
        } catch (Exception e) {
            sendMessage("§cThe connection to the TeamSpeak server failed...");
            return;
        }
        if (ts3Query.isConnected()) {
            sendMessage("§aA connection to TeamSpeak was successful");
        } else {
            sendMessage("§cThe connection to the TeamSpeak server failed...");
            return;
        }
    }

    private void register() {
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new VerifyCommand());
        pluginManager.registerListener(this, new PostLoginListener());
    }

    public void onDisable() {
        if (ts3Query.isConnected()) {
            ts3ApiAsync.logout();
            ts3Query.exit();
        }
        if (mySQL.isConnected()) {
            mySQL.disconnect();
        }
    }

    public boolean hasVPN(String address) {
        try {
            if (configHandler.getBoolean("module.vpn.enable")) {
                if (cacheAddress.containsKey(address)) {
                    return cacheAddress.get(address);
                }
                OkHttpClient caller = new OkHttpClient();
                Request request = new Request.Builder().url("http://v2.api.iphub.info/ip/" + address).addHeader("X-Key", configHandler.getString("module.vpn.apiKey")).build();
                try {
                    Response response = caller.newCall(request).execute();
                    JSONObject json = new JSONObject(response.body().string());
                    int block = (int) json.get("block");
                    if (block == 1) {
                        cacheAddress.put(address, true);
                        return true;
                    }
                } catch (Exception e) {
                    ProxyServer.getInstance().getConsole().sendMessage("[TeamSpeakBot] §cNo API key was found...");
                }
                cacheAddress.put(address, false);
                return false;
            }
        } catch (Exception e) {
            ProxyServer.getInstance().getConsole().sendMessage("[TeamSpeakBot] §cNo API key was found...");
        }
        return false;
    }

    public void sendMessage(String message) {
        ProxyServer.getInstance().getConsole().sendMessage("[TeamSpeakBot] " + message);
    }

    public static TeamSpeakBot getInstance() {
        return instance;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public MySQL getMySQL() {
        return mySQL;
    }

    public TS3ApiAsync getTs3ApiAsync() {
        return ts3ApiAsync;
    }

    public TS3Config getTs3Config() {
        return ts3Config;
    }

    public TS3Query getTs3Query() {
        return ts3Query;
    }

    public VerifyHandler getVerifyHandler() {
        return verifyHandler;
    }
}
