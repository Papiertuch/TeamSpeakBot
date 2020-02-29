package de.papiertuch.teamspeakbot.nametags.listeners;

import de.papiertuch.teamspeakbot.nametags.NameTags;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Team;

/**
 * Created by Leon on 29.02.2020.
 * development with love.
 * © Copyright by Papiertuch
 */

public class PlayerJoinListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(NameTags.getInstance(), (Runnable)new Runnable() {
            @Override
            public void run() {
                if (NameTags.getInstance().getMySQL().isVerify(player.getUniqueId())) {
                    PlayerJoinListener.this.setNameTag(player, NameTags.getInstance().getVerifyTag());
                } else {
                    PlayerJoinListener.this.setNameTag(player, NameTags.getInstance().getUnVerifyTag());
                }
            }
        }, 5L);
    }

    private void setNameTag(Player player, String nameTag) {
        Team team = player.getScoreboard().getEntryTeam(player.getName());
        if (team != null) {
            final String prefix = (team.getPrefix() != null) ? team.getPrefix() : "";
            final String suffix = (team.getSuffix() != null) ? team.getSuffix() : "";
            player.setPlayerListName(prefix + player.getName() + suffix + nameTag);
        } else {
            player.setPlayerListName(player.getName() + nameTag);
        }
    }
}
