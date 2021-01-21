package de.papiertuch.teamspeakbot.proxy;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import de.papiertuch.teamspeakbot.proxy.api.EinBotApi;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
    private EinBotApi einBotApi;
    private ConfigHandler configHandler;
    private HashMap<String, Boolean> cacheAddress;
    private String newVersion;

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
        System.out.print("> TeamSpeakBot-Proxy - by Papiertuch | Discord: https://papiertu.ch/go/discord/");
        System.out.print("> Version: " + getDescription().getVersion());
        System.out.print("> Java: " + System.getProperty("java.version") + " System: " + System.getProperty("os.name"));
        System.out.print("   ");
        verifyHandler = new VerifyHandler();
        configHandler = new ConfigHandler();
        einBotApi = new EinBotApi();
        mySQL = new MySQL();
        ts3Config = new TS3Config();
        ts3Query = new TS3Query(ts3Config);
        cacheAddress = new HashMap<>();
        ts3ApiAsync = ts3Query.getAsyncApi();
        configHandler.loadConfig();

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://papiertu.ch/check/teamSpeakBot.php").openConnection();
            connection.setRequestProperty("User-Agent", this.getDescription().getVersion());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            newVersion = bufferedReader.readLine();
            if (newVersion.equalsIgnoreCase("false")) {
                sendMessage("§cYou have a version that's been deactivated");
                sendMessage("§cPlease download the latest version");
                sendMessage("§bDiscord https://papiertu.ch/go/discord/ or Papiertuch#7836");
                sendMessage("§ehttps://www.spigotmc.org/resources/einbot-teamspeak-verification-and-support-notify.48188/");
                return;
            } else if (!newVersion.equalsIgnoreCase(getDescription().getVersion())) {
                sendMessage("§aA new version is available §8» §f§l" + newVersion);
                sendMessage("§ehttps://www.spigotmc.org/resources/einbot-teamspeak-verification-and-support-notify.48188/");
            }
        } catch (IOException e) {
            sendMessage("§cNo connection to the WebServer could be established, you will not receive update notifications");
        }

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
            e.printStackTrace();
            sendMessage("§cThe connection to the TeamSpeak server failed...");
            return;
        }
        if (ts3Query.isConnected()) {
            sendMessage("§aA connection to TeamSpeak was successful");
        } else {
            sendMessage("§cThe connection to the TeamSpeak server failed...");
            return;
        }
        getProxy().getScheduler().schedule(this, () -> {
            if (!ts3Query.isConnected()) {
                try {
                    ts3Config = new TS3Config();
                    ts3Query = new TS3Query(ts3Config);
                    ts3ApiAsync = ts3Query.getAsyncApi();
                    ts3Config.setHost(configHandler.getString("query.host"));
                    ts3Config.setQueryPort(configHandler.getInt("query.port"));
                    ts3Config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
                    ts3Config.setReconnectStrategy(ReconnectStrategy.exponentialBackoff());
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
                    sendMessage("§cA new attempt to connect to TeamSpeak has failed");
                }
            }
            ts3ApiAsync.getClients().onSuccess(clients -> clients.forEach(client -> {
                if (hasVPN(client.getIp())) {
                    TeamSpeakBot.getInstance().getTs3ApiAsync().kickClientFromServer(TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.kickReason"), client.getId());
                }
            }));
        }, 1, 1, TimeUnit.MINUTES);
    }

    private void register() {
        PluginManager pluginManager = getProxy().getPluginManager();
        pluginManager.registerCommand(this, new VerifyCommand());
        pluginManager.registerListener(this, new PostLoginListener());
        pluginManager.registerListener(this, new PlayerDisconnectListener());
    }

    @Override
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

    public EinBotApi getEinBotApi() {
        return einBotApi;
    }

    public String getNewVersion() {
        return newVersion;
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
