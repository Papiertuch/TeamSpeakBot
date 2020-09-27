package de.papiertuch.teamspeakbot.nametags;

import de.papiertuch.teamspeakbot.nametags.listeners.PlayerJoinListener;
import de.papiertuch.teamspeakbot.nametags.utils.MySQL;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class NameTags extends JavaPlugin {

    private static NameTags instance;
    private MySQL mySQL;
    private File file;
    private FileConfiguration configuration;
    private String verifyTag;
    private String unVerifyTag;

    @Override
    public void onEnable() {
        instance = this;
        register();

        file = new File("plugins/TeamSpeakBot-NameTags", "config.yml");
        configuration = YamlConfiguration.loadConfiguration(this.file);

        configuration.options().copyDefaults(true);
        configuration.addDefault("mysql.host", "host");
        configuration.addDefault("mysql.user", "user");
        configuration.addDefault("mysql.database", "database");
        configuration.addDefault("mysql.password", "password");
        configuration.addDefault("mysql.port", 3306);
        configuration.addDefault("nameTag.verify.enable", true);
        configuration.addDefault("nameTag.verify.text", " &a\u2714");
        configuration.addDefault("nameTag.unVerify.enable", true);
        configuration.addDefault("nameTag.unVerify.text", " &c\u2716");
        try {
            configuration.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        verifyTag = configuration.getString("nameTag.verify.text").replace("&", "§");
        unVerifyTag = configuration.getString("nameTag.unVerify.text").replace("&", "§");
        mySQL = new MySQL(configuration.getString("mysql.host"), configuration.getString("mysql.database"), this.configuration.getString("mysql.user"), this.configuration.getString("mysql.password"), this.configuration.getInt("mysql.port"));
        mySQL.createTable();
    }

    @Override
    public void onDisable() {
        if (mySQL.isConnected()) {
            mySQL.disconnect();
        }
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public String getVerifyTag() {
        return verifyTag;
    }

    public String getUnVerifyTag() {
        return unVerifyTag;
    }

    private void register() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerJoinListener(), this);
    }

    public MySQL getMySQL() {
        return this.mySQL;
    }

    public static NameTags getInstance() {
        return NameTags.instance;
    }
}
