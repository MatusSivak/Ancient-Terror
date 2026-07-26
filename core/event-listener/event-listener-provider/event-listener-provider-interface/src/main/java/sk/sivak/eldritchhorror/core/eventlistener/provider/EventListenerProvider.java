package sk.sivak.eldritchhorror.core.eventlistener.provider;

import sk.sivak.eldritchhorror.core.constants.MysteryCardInfo;
import sk.sivak.eldritchhorror.core.constants.ancientone.AncientOneInfo;
import sk.sivak.eldritchhorror.core.constants.condition.ConditionInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.location.LocationType;
import sk.sivak.eldritchhorror.core.constants.monster.MonsterInfo;
import sk.sivak.eldritchhorror.core.constants.mythos.MythosCard;
import sk.sivak.eldritchhorror.core.eventtype.data.encounter.LocationEncounter;
import sk.sivak.eldritchhorror.core.eventtype.data.investigator.DefeatedInvestigatorEncounterData;

import java.util.List;

/**
 * @author msivak
 */
public interface EventListenerProvider {

    void registerCollectCommonEncountersListener();

    void registerInvestigatorListeners(InvestigatorId investigator);

    void justRegisterInvestigatorListeners(InvestigatorId investigator);

    void unregisterInvestigatorListeners(InvestigatorId investigator);

    void registerAncientOneListeners(AncientOneInfo ancientOneInfo);

    void loadAncientOneListeners(AncientOneInfo ancientOneInfo);

    void registerMysteryListeners(MysteryCardInfo mysteryCardInfo);

    void justRegisterMysteryListeners(MysteryCardInfo mysteryCardInfo, int progress);

    void justAddRedPins(MysteryCardInfo mysteryCardInfo);

    void unregisterMysteryListeners(MysteryCardInfo currentMysteryCard);

    void advanceActiveMysteryAction(MysteryCardInfo mysteryCardInfo);

    void registerMonsterListeners(MonsterInfo monsterInfo);

    void beforeSpawnMonsterInit(MonsterInfo monsterInfo);

    void executeConditionBack(ConditionInfo conditionInfo);

    void executeGeneralEncounter(int page, LocationType locationType);

    void unregisterDefeatedListener(InvestigatorId investigatorId);

    void executeResearchEncounter(Integer page, LocationType locationType, LocationId locationId);

    void executeExpeditionEncounter(int page, LocationId locationId);

    void executeOtherWorldEncounter(int page);

    void executeLocationEncounter(Integer page, LocationEncounter.LocationEncounterType locationEncounterType);

    void justRegisterMonsterListeners(MonsterInfo monsterInfo);

    void unregisterMonsterListeners(MonsterInfo monsterInfo);

    void registerDefeatedListener(InvestigatorId investigatorId, boolean health);

    void executeDefeatedInvestigatorEncounter(DefeatedInvestigatorEncounterData data);

    void executeMythosText(MythosCard.MythosColor mythosColor, int id);

    void init();

    void justLoadRumor(String rumorId);

    void executeRlyehRisenEncounter(Integer page);

    void executeVoidBetweenWorldsEncounter(Integer page);

    void executeTheKeyAndTheGateEncounter(Integer page);
}
