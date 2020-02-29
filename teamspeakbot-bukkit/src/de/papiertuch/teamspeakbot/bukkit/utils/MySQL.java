package de.papiertuch.teamspeakbot.bukkit.utils;

import de.papiertuch.teamspeakbot.bukkit.TeamSpeakBot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class MySQL {

    private Connection connection;

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + TeamSpeakBot.getInstance().getConfigHandler().getString("mysql.host") + ":" + TeamSpeakBot.getInstance().getConfigHandler().getInt("mysql.port") + "/" + TeamSpeakBot.getInstance().getConfigHandler().getString("mysql.database") + "?autoReconnect=true", TeamSpeakBot.getInstance().getConfigHandler().getString("mysql.user"), TeamSpeakBot.getInstance().getConfigHandler().getString("mysql.password"));
            TeamSpeakBot.getInstance().sendMessage("§aA connection to MySQL was successful");
        } catch (Exception e) {
            TeamSpeakBot.getInstance().sendMessage("§cThe connection to the MySQL server failed...");
        }
    }

    public void createTable() {
        connect();
        update("CREATE TABLE IF NOT EXISTS `teamSpeak` (`UUID` varchar(64), `ID` varchar(64), `RANK` int, `STATUS` bool);");
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
