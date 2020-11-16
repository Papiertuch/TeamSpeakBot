package de.papiertuch.teamspeakbot.proxy.api;


import de.papiertuch.teamspeakbot.proxy.utils.VerifyHandler;

import java.util.UUID;

public class EinBotApi {

    private VerifyHandler verifyHandler;

    public EinBotApi() {
        verifyHandler = new VerifyHandler();
    }

    public void delete(String uuid) {
        verifyHandler.delete(uuid);
    }

    public void setClientGroups(String uuid) {
        verifyHandler.setClientGroups(uuid);
    }

    public int getRank(String uuid) {
        return verifyHandler.getRank(uuid);
    }

    public String getTeamSpeakId(String uuid) {
        return verifyHandler.getTeamSpeakId(uuid);
    }

    public boolean isVerify(UUID uuid) {
        return verifyHandler.isVerify(uuid.toString());
    }

    public boolean isVerify(String teamSpeakUuid) {
        return verifyHandler.isVerifyFromTeamSpeak(teamSpeakUuid);
    }

    public boolean isExists(UUID uuid) {
        return verifyHandler.isExists(uuid.toString());
    }

    public boolean isExists(String teamSpeakUuid) {
        return verifyHandler.isExistsFromTeamSpeak(teamSpeakUuid);
    }
}
