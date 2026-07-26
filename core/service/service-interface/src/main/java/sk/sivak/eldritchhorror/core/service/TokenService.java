package sk.sivak.eldritchhorror.core.service;

import rx.Single;
import sk.sivak.eldritchhorror.core.constants.clue.ClueInfo;
import sk.sivak.eldritchhorror.core.constants.investigator.InvestigatorId;
import sk.sivak.eldritchhorror.core.constants.location.LocationId;
import sk.sivak.eldritchhorror.core.constants.omen.OmenId;
import sk.sivak.eldritchhorror.core.eventtype.data.token.GainClueData;
import sk.sivak.eldritchhorror.core.eventtype.data.token.SpendData;
import sk.sivak.eldritchhorror.core.service.data.ClueFocusHealthSanity;

import java.util.List;

/**
 * @author msivak
 */
public interface TokenService extends CommonService {

    Single<List<ClueInfo>> spawnClues(int amount);

    Single<List<ClueInfo>> spawnCluesQuick(int amount);

    void gainHealth(int amount);

    void gainSanity(int amount);

    void gainClueFromPool();

    void gainClueFromSpace(GainClueData gainClueData);

    void gainClueFromPool(InvestigatorId investigatorId);

    void gainFocus();

    void loseFocus();

    void loseClue();

    void loseHealth(int amount);

    void spendHealth(int amount);

    void loseSanity(int amount);

    void spendSanity(int amount);

    Single<SpendData> canSpend(int clue, int focus, int health, int sanity);

    Single<SpendData> spend(int clue, int focus, int health, int sanity);

    Single<SpendData> canSpend(ClueFocusHealthSanity clueFocusHealthSanity);

    Single<SpendData> spend(ClueFocusHealthSanity clueFocusHealthSanity);

    void discardClue(LocationId locationId);

    void spawnClueAt(LocationId locationId);

    void moveClue(LocationId spawnLocation, LocationId targetLocation);
}
