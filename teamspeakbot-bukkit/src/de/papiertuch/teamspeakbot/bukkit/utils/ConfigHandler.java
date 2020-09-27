package de.papiertuch.teamspeakbot.bukkit.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class ConfigHandler {

    private File file = new File("plugins/TeamSpeakBot/config.yml");
    private FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
    private HashMap<String, String> cacheString;
    private HashMap<String, Boolean> cacheBoolean;
    private List<String> rankList;
    private List<String> supportList;
    private List<String> botList;
    private List<String> supportChannelList;
    private List<String> rankIdList;
    private String prefix;

    public ConfigHandler() {
        this.cacheString = new HashMap<>();
        this.cacheBoolean = new HashMap<>();
        this.rankIdList = new ArrayList<>();
    }

    public void loadConfig() {
        configuration.options().copyDefaults(true);
        configuration.addDefault("mysql.host", "host");
        configuration.addDefault("mysql.user", "user");
        configuration.addDefault("mysql.database", "database");
        configuration.addDefault("mysql.password", "password");
        configuration.addDefault("mysql.port", 3306);
        configuration.addDefault("query.host", "host");
        configuration.addDefault("query.user", "user");
        configuration.addDefault("query.password", "password");
        configuration.addDefault("query.port", 10011);
        configuration.addDefault("teamSpeak.port", 9987);
        configuration.addDefault("teamSpeak.botName", "TeamSpeakBot");
        configuration.addDefault("module.verify.enable", true);
        configuration.addDefault("module.verify.command", "verify");
        configuration.addDefault("module.verify.heads.enable", true);
        configuration.addDefault("module.verify.rank", 1);

        List<String> ranks = new ArrayList<>();
        ranks.add("teamSpeak.admin, 10");
        ranks.add("teamSpeak.mod, 11");
        ranks.add("teamSpeak.sup, 12");
        ranks.add("teamSpeak.default, 13");
        configuration.addDefault("module.verify.ranks", ranks);
        configuration.addDefault("module.support.enable", true);

        List<String> channel = new ArrayList<>();
        channel.add("34");
        channel.add("35");
        configuration.addDefault("module.support.channels", channel);
        configuration.addDefault("module.support.perms", "teamSpeak.notify");
        configuration.addDefault("module.support.message", "Poke");
        List<String> list = new ArrayList<>();
        list.add("51");
        list.add("52");
        configuration.addDefault("module.support.ranks", list);
        configuration.addDefault("module.vpn.enable", true);
        List<String> bots = new ArrayList<>();
        bots.add("10");
        configuration.addDefault("module.vpn.enableRanks", bots);
        configuration.addDefault("module.vpn.apiKey", "https://iphub.info/apiKey/newFree create a Free ApiKey");

        configuration.addDefault("message.teamSpeak.alreadyVerify", "You or the player is verified");
        configuration.addDefault("message.teamSpeak.clientDescription", "Name: %name% | UUID: %uuid% | ID: %teamSpeakId%");
        configuration.addDefault("message.teamSpeak.syntax", "Usage: !verify <Name>");
        configuration.addDefault("message.teamSpeak.notOnline", "The player is not online");
        configuration.addDefault("message.teamSpeak.load", "You are verified now");
        configuration.addDefault("message.teamSpeak.supportNotify", "%clients% waits on %channel% ");
        configuration.addDefault("message.teamSpeak.supportJoin", "A team member was notified");
        configuration.addDefault("message.teamSpeak.kickReason", "You were kicked because of the usage of VPN from the server");
        configuration.addDefault("message.teamSpeak.info", "You are not verified! To verify write me !verify <name>");
        configuration.addDefault("message.teamSpeak.request", "A request has been sent to Ingame to client %client%");

        configuration.addDefault("message.inGame.prefix", "&8[&b&lTeamSpeak&8]");
        configuration.addDefault("message.inGame.kickReason", "&cYou were kicked because of the usage of VPN from the server");
        configuration.addDefault("message.inGame.supportNotify", "%prefix% &6&l%client% &7waits on channel &e&l%channel%");
        configuration.addDefault("message.inGame.hoverAccept", "&a&lAccept");
        configuration.addDefault("message.inGame.notVerify", "%prefix% &cYou are not verified");
        configuration.addDefault("message.inGame.synchronize", "%prefix% &7Account is being syncedt...");
        configuration.addDefault("message.inGame.load", "%prefix% &7Your account have been &a&lloaded");
        configuration.addDefault("message.inGame.notOnline", "%prefix% &cYou are not online...");
        configuration.addDefault("message.inGame.message", "%prefix% &7To verify yourself you must write to me on &6&lTeamSpeak");
        configuration.addDefault("message.inGame.noRequest", "%prefix% &cYou do not have any requests");
        configuration.addDefault("message.inGame.hoverDeny", "&c&lDeny");
        configuration.addDefault("message.inGame.deny", "%prefix% &cYou have deny the request");
        configuration.addDefault("message.inGame.request", "%prefix% &6&l%client% &7wants to connect with you");
        configuration.addDefault("message.inGame.delete", "%prefix% &7You have &c&ldeleted &7your account");
        configuration.addDefault("message.inGame.requestTest", "%prefix% &7Accept with &8» ");
        configuration.addDefault("message.inGame.syntax", "%prefix% &7Use&8: /&bverify update&8/&baccept&8/&bdeny&8/&bdelete");

        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        load();
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

    public String getPrefix() {
        return prefix;
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public String getString(String string) {
        if (!cacheString.containsKey(string)) {
            cacheString.put(string, configuration.getString(string));
        }
        return cacheString.get(string).replace("&", "§").replace("%prefix%", prefix);
    }
}
