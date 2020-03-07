package de.papiertuch.teamspeakbot.proxy.utils;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class ConfigHandler {

    private ConfigurationProvider configurationProvider = ConfigurationProvider.getProvider(YamlConfiguration.class);
    private Configuration configuration;
    private HashMap<String, String> cacheString;
    private HashMap<String, Boolean> cacheBoolean;
    private HashMap<String, Integer> cacheInt;
    private List<String> rankList;
    private List<String> supportList;
    private List<String> botList;
    private List<String> supportChannelList;
    private List<String> rankIdList;
    private String prefix;

    public ConfigHandler() {
        this.cacheString = new HashMap<>();
        this.cacheBoolean = new HashMap<>();
        this.cacheInt = new HashMap<>();
        this.rankIdList = new ArrayList<>();
    }

    public void loadConfig() {
        try {
            if (!Files.exists(Paths.get("plugins/TeamSpeakBot"))) {
                Files.createDirectories(Paths.get("plugins/TeamSpeakBot"));
            }
            if (Files.exists(Paths.get("plugins/TeamSpeakBot/config.yml"))) {
                try (InputStream inputStream = Files.newInputStream(Paths.get("plugins/TeamSpeakBot/config.yml")); InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                    configuration = configurationProvider.load(inputStreamReader);
                    load();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }
            configuration = new Configuration();
            configuration.set("mysql.host", "host");
            configuration.set("mysql.user", "user");
            configuration.set("mysql.database", "database");
            configuration.set("mysql.password", "password");
            configuration.set("mysql.port", 3306);
            configuration.set("query.host", "host");
            configuration.set("query.user", "user");
            configuration.set("query.password", "password");
            configuration.set("query.port", 10011);
            configuration.set("teamSpeak.port", 9987);
            configuration.set("teamSpeak.botName", "TeamSpeakBot");
            configuration.set("module.verify.enable", true);
            configuration.set("module.verify.heads.enable", true);
            configuration.set("module.verify.rank", 1);

            List<String> ranks = new ArrayList<>();
            ranks.add("teamSpeak.admin, 10");
            ranks.add("teamSpeak.mod, 11");
            ranks.add("teamSpeak.sup, 12");
            ranks.add("teamSpeak.default, 13");
            configuration.set("module.verify.ranks", ranks);
            configuration.set("module.support.enable", true);

            List<String> channel = new ArrayList<>();
            channel.add("34");
            channel.add("35");
            configuration.set("module.support.channels", channel);
            configuration.set("module.support.perms", "teamSpeak.notify");
            configuration.set("module.support.message", "Poke");
            List<String> list = new ArrayList<>();
            list.add("51");
            list.add("52");
            configuration.set("module.support.ranks", list);
            configuration.set("module.vpn.enable", true);
            List<String> bots = new ArrayList<>();
            bots.add("10");
            configuration.set("module.vpn.enableRanks", bots);
            configuration.set("module.vpn.apiKey", "https://iphub.info/apiKey/newFree create a Free ApiKey");

            configuration.set("message.teamSpeak.alreadyVerify", "You or the player is verified");
            configuration.set("message.teamSpeak.clientDescription", "Name: %name% | UUID: %uuid%");
            configuration.set("message.teamSpeak.syntax", "Usage: !verify <Name>");
            configuration.set("message.teamSpeak.notOnline", "The player is not online");
            configuration.set("message.teamSpeak.load", "You are verified now");
            configuration.set("message.teamSpeak.supportNotify", "%clients% waits on %channel% ");
            configuration.set("message.teamSpeak.supportJoin", "A team member was notified");
            configuration.set("message.teamSpeak.kickReason", "You were kicked because of the usage of VPN from the server");
            configuration.set("message.teamSpeak.info", "You are not verified! To verify write me !verify <name>");
            configuration.set("message.teamSpeak.request", "A request has been sent to Ingame to client %client%");

            configuration.set("message.inGame.prefix", "&8[&b&lTeamSpeak&8]");
            configuration.set("message.inGame.kickReason", "&cYou were kicked because of the usage of VPN from the server");
            configuration.set("message.inGame.supportNotify", "%prefix% &6&l%client% &7waits on channel &e&l%channel%");
            configuration.set("message.inGame.hoverAccept", "&a&lAccept");
            configuration.set("message.inGame.notVerify", "%prefix% &cYou are not verified");
            configuration.set("message.inGame.synchronize", "%prefix% &7Account is being syncedt...");
            configuration.set("message.inGame.load", "%prefix% &7Your account have been &a&lloaded");
            configuration.set("message.inGame.notOnline", "%prefix% &cYou are not online...");
            configuration.set("message.inGame.message", "%prefix% &7To verify yourself you must write to me on &6&lTeamSpeak");
            configuration.set("message.inGame.noRequest", "%prefix% &cYou do not have any requests");
            configuration.set("message.inGame.hoverDeny", "&c&lDeny");
            configuration.set("message.inGame.deny", "%prefix% &cYou have deny the request");
            configuration.set("message.inGame.request", "%prefix% &6&l%client% &7wants to connect with you");
            configuration.set("message.inGame.delete", "%prefix% &7You have &c&ldeleted &7your account");
            configuration.set("message.inGame.requestTest", "%prefix% &7Accept with &8» ");
            configuration.set("message.inGame.syntax", "%prefix% &7Use&8: /&bverify update&8/&baccept&8/&bdeny&8/&bdelete");

            try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(Files.newOutputStream(Paths.get("plugins/TeamSpeakBot/config.yml")), StandardCharsets.UTF_8)) {
                configurationProvider.save(configuration, outputStreamWriter);
            }
            load();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void load() {
        prefix = configuration.getString("message.inGame.prefix").replace("&", "§");
        rankList = configuration.getStringList("module.verify.ranks");
        supportList = configuration.getStringList("module.support.ranks");
        botList = configuration.getStringList("module.vpn.enableRanks");
        supportChannelList = configuration.getStringList("module.support.channels");
        for (int i = 0; i < rankList.size(); ++i) {
            rankIdList.add(rankList.get(i).split(", ")[1]);
        }
    }

    public List<String> getBotList() {
        return botList;
    }

    public List<String> getSupportList() {
        return supportList;
    }

    public List<String> getSupportChannelList() {
        return supportChannelList;
    }

    public List<String> getRankIdList() {
        return rankIdList;
    }

    public List<String> getRankList() {
        return rankList;
    }

    public Integer getInt(String string) {
        return configuration.getInt(string);
    }

    public Boolean getBoolean(String string) {
        if (!cacheBoolean.containsKey(string)) {
            cacheBoolean.put(string, configuration.getBoolean(string));
        }
        return cacheBoolean.get(string);
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getString(String string) {
        if (!cacheString.containsKey(string)) {
            cacheString.put(string, configuration.getString(string));
        }
        return cacheString.get(string).replace("&", "§").replace("%prefix%", prefix);
    }
}
