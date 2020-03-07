package de.papiertuch.teamspeakbot.bukkit.utils;

import de.papiertuch.teamspeakbot.bukkit.TeamSpeakBot;
import net.md_5.bungee.api.ProxyServer;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeUnit;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class MySQL {

    private Connection connection;

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + TeamSpeakBot.getInstance().getConfigHandler().getString("mysql.host") + ":" + TeamSpeakBot.getInstance().getConfigHandler().getInt("mysql.port") + "/" + TeamSpeakBot.getInstance().getConfigHandler().getString("mysql.database") + "?autoReconnect=true", TeamSpeakBot.getInstance().getConfigHandler().getString("mysql.user"), TeamSpeakBot.getInstance().getConfigHandler().getConfiguration().getString("mysql.password"));
            TeamSpeakBot.getInstance().sendMessage("§aA connection to MySQL was successful");
        } catch (Exception e) {
            TeamSpeakBot.getInstance().sendMessage("§cThe connection to the MySQL server failed...");
        }
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(TeamSpeakBot.getInstance(), new Runnable() {
            @Override
            public void run() {
                update("CREATE TABLE IF NOT EXISTS `teamSpeak` (`uuid` varchar(64), `id` varchar(64), `rank` int, `newRank` int, `status` bool);");
            }
        }, 0, 216000);
    }

    public void createTable() {
        connect();
        update("CREATE TABLE IF NOT EXISTS `teamSpeak` (`uuid` varchar(64), `id` varchar(64), `rank` int, `newRank` int, `status` bool);");
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void update(String qry) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(qry);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
        }
    }
}
