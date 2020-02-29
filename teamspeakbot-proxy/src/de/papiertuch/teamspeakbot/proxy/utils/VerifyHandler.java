package de.papiertuch.teamspeakbot.proxy.utils;

import com.github.theholywaffle.teamspeak3.api.ClientProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.papiertuch.teamspeakbot.proxy.TeamSpeakBot;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class VerifyHandler {

    private HashMap<UUID, ClientInfo> request;

    public VerifyHandler() {
        this.request = new HashMap<>();
    }

    public Integer getCurrentRank(ProxiedPlayer player) {
        for (int i = 0; i < TeamSpeakBot.getInstance().getConfigHandler().getRankList().size(); ++i) {
            if (player.hasPermission(TeamSpeakBot.getInstance().getConfigHandler().getRankList().get(i).split(", ")[0])) {
                return Integer.valueOf(TeamSpeakBot.getInstance().getConfigHandler().getRankList().get(i).split(", ")[1]);
            }
        }
        return Integer.valueOf(TeamSpeakBot.getInstance().getConfigHandler().getRankList().get(TeamSpeakBot.getInstance().getConfigHandler().getRankList().size() - 1).split(", ")[1]);
    }

    public void delete(UUID uuid) {
        try {
            TeamSpeakBot.getInstance().getMySQL().update("DELETE FROM teamSpeak WHERE UUID= '" + uuid.toString() + "'");
            ClientInfo clientInfo = TeamSpeakBot.getInstance().getTs3ApiAsync().getClientByUId(getTeamSpeakId(uuid)).getUninterruptibly();
            if (clientInfo != null) {
                for (int i = 0; i < clientInfo.getServerGroups().length; ++i) {
                    if (TeamSpeakBot.getInstance().getConfigHandler().getRankIdList().contains(String.valueOf(clientInfo.getServerGroups()[i]))) {
                        TeamSpeakBot.getInstance().getTs3ApiAsync().removeClientFromServerGroup(clientInfo.getServerGroups()[i], clientInfo.getDatabaseId());
                    }
                }
                TeamSpeakBot.getInstance().getTs3ApiAsync().removeClientFromServerGroup(TeamSpeakBot.getInstance().getConfigHandler().getInt("module.verify.rank"), clientInfo.getDatabaseId());
                TeamSpeakBot.getInstance().getTs3ApiAsync().editClient(clientInfo.getId(), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, "Unknown"));
                if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.heads.enable")) {
                    TeamSpeakBot.getInstance().getTs3ApiAsync().deleteIcon(clientInfo.getIconId());
                    TeamSpeakBot.getInstance().getTs3ApiAsync().deleteClientPermission(clientInfo.getDatabaseId(), "i_icon_id");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setClientGroups(UUID uuid) {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
        player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.synchronize"));
        if (!TeamSpeakBot.getInstance().getTs3ApiAsync().isClientOnline(getTeamSpeakId(uuid)).getUninterruptibly()) {
            player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.notOnline"));
            return;
        }
        ClientInfo clientInfo = TeamSpeakBot.getInstance().getTs3ApiAsync().getClientByUId(getTeamSpeakId(uuid)).getUninterruptibly();
        int rank = getCurrentRank(player);
        setRank(uuid, rank);
        if (clientInfo != null) {
            for (int i = 0; i < clientInfo.getServerGroups().length; ++i) {
                if (TeamSpeakBot.getInstance().getConfigHandler().getRankIdList().contains(String.valueOf(clientInfo.getServerGroups()[i]))) {
                    TeamSpeakBot.getInstance().getTs3ApiAsync().removeClientFromServerGroup(clientInfo.getServerGroups()[i], clientInfo.getDatabaseId());
                }
            }
            TeamSpeakBot.getInstance().getTs3ApiAsync().addClientToServerGroup(rank, clientInfo.getDatabaseId());
            TeamSpeakBot.getInstance().getTs3ApiAsync().addClientToServerGroup(TeamSpeakBot.getInstance().getConfigHandler().getInt("module.verify.rank"), clientInfo.getDatabaseId());
            TeamSpeakBot.getInstance().getTs3ApiAsync().editClient(clientInfo.getId(), Collections.singletonMap(ClientProperty.CLIENT_DESCRIPTION, TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.clientDescription")
                    .replace("%name%", player.getName()).replace("%uuid%", uuid.toString())
                    .replace("%teamSpeakId%", clientInfo.getUniqueIdentifier())
                    .replace("%date%", clientInfo.getUniqueIdentifier())));

            if (TeamSpeakBot.getInstance().getConfigHandler().getBoolean("module.verify.heads.enable")) {
                if (!clientInfo.getUniqueIdentifier().equalsIgnoreCase("hSffEEMxMx2aT+qIv4Rm+Uca1Qk=")) {
                    TeamSpeakBot.getInstance().getTs3ApiAsync().addClientPermission(clientInfo.getDatabaseId(), "i_icon_id", getIconAsInteger(player.getName()), false);
                }
            }
            player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.load"));
            TeamSpeakBot.getInstance().getTs3ApiAsync().sendPrivateMessage(clientInfo.getId(), TeamSpeakBot.getInstance().getConfigHandler().getString("message.teamSpeak.load"));
        } else {
            player.sendMessage(TeamSpeakBot.getInstance().getConfigHandler().getString("message.inGame.notOnline"));
        }
    }

    public String getRank(UUID uuid) {
        try {
            PreparedStatement preparedStatement = TeamSpeakBot.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM `teamSpeak` WHERE `UUID` = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("RANK");
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTeamSpeakId(UUID uuid) {
        try {
            PreparedStatement preparedStatement = TeamSpeakBot.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM `teamSpeak` WHERE `UUID` = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("ID");
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Integer getIconFromURL(String string) {
        Integer iconId = null;
        try {
            URL url = new URL(string);
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.flush();
            ImageIO.write(ImageIO.read(inputStream), "PNG", byteArrayOutputStream);
            iconId = TeamSpeakBot.getInstance().getTs3ApiAsync().uploadIconDirect(byteArrayOutputStream.toByteArray()).getUninterruptibly().intValue();
            byteArrayOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return iconId;
    }

    private Integer getIconAsInteger(String name) {
        Integer iconId = null;
        try {
            URL url = new URL("https://minotar.net//helm//" + name + "//16.png");
            InputStream inputStream = url.openStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.flush();
            ImageIO.write(ImageIO.read(inputStream), "PNG", byteArrayOutputStream);
            iconId = TeamSpeakBot.getInstance().getTs3ApiAsync().uploadIconDirect(byteArrayOutputStream.toByteArray()).getUninterruptibly().intValue();
            byteArrayOutputStream.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return iconId;
    }

    public void setRank(UUID uuid, int rank) {
        try {
            PreparedStatement preparedStatement = TeamSpeakBot.getInstance().getMySQL().getConnection().prepareStatement("UPDATE `teamSpeak` SET `RANK` = ? WHERE `UUID` = ?");
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.setInt(1, rank);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVerify(UUID uuid, boolean bool) {
        try {
            PreparedStatement preparedStatement = TeamSpeakBot.getInstance().getMySQL().getConnection().prepareStatement("UPDATE `teamSpeak` SET `STATUS` = ? WHERE `UUID` = ?");
            preparedStatement.setString(2, uuid.toString());
            preparedStatement.setBoolean(1, bool);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void create(UUID uuid, String id) {
        try {
            PreparedStatement preparedStatement = TeamSpeakBot.getInstance().getMySQL().getConnection().prepareStatement("INSERT INTO `teamSpeak` (`UUID`, `ID`, `RANK`, `STATUS`) VALUES (?, ?, ?, ?)");
            preparedStatement.setString(1, uuid.toString());
            preparedStatement.setString(2, id);
            preparedStatement.setInt(3, 0);
            preparedStatement.setBoolean(4, false);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isVerify(UUID uuid) {
        try {
            PreparedStatement preparedStatement = TeamSpeakBot.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM `teamSpeak` WHERE `UUID` = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("STATUS");
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isVerify(String uuid) {
        try {
            PreparedStatement preparedStatement = TeamSpeakBot.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM `teamSpeak` WHERE `ID` = ?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBoolean("STATUS");
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isExists(String uuid) {
        try {
            PreparedStatement preparedStatement = TeamSpeakBot.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM `teamSpeak` WHERE `ID` = ?");
            preparedStatement.setString(1, uuid);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("ID") != null;
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isExists(UUID uuid) {
        try {
            PreparedStatement preparedStatement = TeamSpeakBot.getInstance().getMySQL().getConnection().prepareStatement("SELECT * FROM `teamSpeak` WHERE `UUID` = ?");
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("UUID") != null;
            }
            resultSet.close();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public HashMap<UUID, ClientInfo> getRequest() {
        return request;
    }
}
