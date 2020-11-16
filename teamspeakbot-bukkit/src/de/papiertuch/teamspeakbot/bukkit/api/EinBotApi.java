package de.papiertuch.teamspeakbot.bukkit.api;


import de.papiertuch.teamspeakbot.bukkit.utils.VerifyHandler;

import java.util.UUID;

public class EinBotApi {

    private VerifyHandler verifyHandler;

    public EinBotApi() {
        verifyHandler = new VerifyHandler();
    }

    public void delete(UUID uuid) {
        verifyHandler.delete(uuid);
    }

    public void setClientGroups(UUID uuid) {
        verifyHandler.setClientGroups(uuid);
    }

    public int getRank(UUID uuid) {
        return verifyHandler.getRank(uuid);
    }

    public String getTeamSpeakId(UUID uuid) {
        return verifyHandler.getTeamSpeakId(uuid);
    }

    public boolean isVerify(UUID uuid) {
        return verifyHandler.isVerify(uuid.toString());
    }

    public boolean isExists(UUID uuid) {
        return verifyHandler.isExists(uuid.toString());
    }

}
