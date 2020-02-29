package de.papiertuch.teamspeakbot.nametags.utils;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.UUID;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class MySQL {

    private Connection connection;
    private String host;
    private String database;
    private String user;
    private String password;
    private int port;

    public MySQL(String host, String database, String user, String password, int port) {
        this.host = host;
        this.database = database;
        this.user = user;
        this.password = password;
        this.port = port;
    }

    public boolean isVerify(UUID uuid) {
        try {
            PreparedStatement preparedStatement = this.getConnection().prepareStatement("SELECT * FROM `teamSpeak` WHERE `UUID` = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("STATUS");
            }
            resultSet.close();
            preparedStatement.close();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true", user, password);
            Bukkit.getConsoleSender().sendMessage("[TeamSpeakBot-NameTags] §aA connection to MySQL was successful");
        } catch (Exception e) {
            Bukkit.getConsoleSender().sendMessage("[TeamSpeakBot-NameTags] §cThe connection to the MySQL server failed...");
        }
    }

    public void createTable() {
        this.connect();
    }

    public void disconnect() {
        try {
            this.connection.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return this.connection != null;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void update(final String qry) {
        try {
            final PreparedStatement ps = this.connection.prepareStatement(qry);
            ps.executeUpdate();
            ps.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
