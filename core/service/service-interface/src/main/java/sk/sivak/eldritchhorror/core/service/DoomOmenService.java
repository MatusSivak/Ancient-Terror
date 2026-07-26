package sk.sivak.eldritchhorror.core.service;

import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenInfo;
import sk.sivak.eldritchhorror.core.eventtype.data.rumor.InitRumorCardData;

public interface DoomOmenService extends CommonService {
    void retreatDoom();

    void advanceDoom();

    void advanceDoom(int amount);

    void advanceOmen();

    void advanceDoomByCurrentOmen(OmenInfo omenInfo);

    void selectNewOmen();

    void addTokenToOmenTrack(OmenId omenId);

    void removeTokenFromOmenTrack(OmenId omenId);

    void initMythosPhase();

    void resolveCurrentMystery();

    void drawMythosCard();

    void displayVictoryPaper();

    void onTriggeredReckoning();

    void unlockRandomCard();

    void initRumorCard(InitRumorCardData initRumorCardData);

    void showRumorCard(String rumorId);

    void countdownRumorCard(String rumorCardId, int size);

    void highlightRumorFailure(String rumorId);

    void highlightRumorObjective(String rumorId);

    void highlightRumorReckoning(String rumorCardId);

    void spawnStorm(String stormId, LocationId location);

    void ancientOneAwakens();

    void increaseAncientOnePower(int power);
}
